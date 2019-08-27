package org.alliancegenome.cache.manager;

import java.io.IOException;
import java.util.List;

import org.alliancegenome.api.entity.CacheStatus;
import org.alliancegenome.cache.CacheAlliance;
import org.alliancegenome.core.config.ConfigHelper;
import org.alliancegenome.core.service.JsonResultResponse;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.eviction.EvictionType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CacheManager<T, U extends JsonResultResponse<T>> {

    public static RemoteCacheManager rmc = null;
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        setupCaches();
    }

    public synchronized static void setupCaches() {

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.addServer()
        .host(ConfigHelper.getCacheHost())
        .port(ConfigHelper.getCachePort())
        .socketTimeout(500000)
        .connectionTimeout(500000)
        .tcpNoDelay(true);

        rmc = new RemoteCacheManager(cb.build());

        rmc.start();

        for (CacheAlliance cache : CacheAlliance.values()) {
            log.debug("Creating Cache: " + cache.getCacheName());
            
            org.infinispan.configuration.cache.ConfigurationBuilder cb2 = new org.infinispan.configuration.cache.ConfigurationBuilder();
            
            cb2
            .memory()
            .storageType(StorageType.BINARY)
            .evictionType(EvictionType.MEMORY)
            .size(1_500_000_000)
            .persistence()
            .passivation(false)
            .addSingleFileStore()
                .preload(true)
                .shared(false)
                .fetchPersistentState(true)
                .location("/data/" + cache.getCacheName()).async().enable().threadPoolSize(5);
            
            
            rmc.administration().getOrCreateCache(cache.getCacheName(), cb2.build());
            
            // log.info("Clearing cache if exists"); // This might need to run if the cacher is running
            // rmc.getCache(cache.getCacheName()).clear(); // But should not be run via the API
            
            log.debug("Cache: " + cache.getCacheName() + " finished creating");
        }

    }

    private static RemoteCache<String, String> getCacheSpace(CacheAlliance cache) {
        //log.info("Getting Cache Space: " + cache.getCacheName());
        return rmc.getCache(cache.getCacheName());
    }

    public void putCache(String primaryKey, JsonResultResponse<T> result, Class<?> classView, CacheAlliance cacheSpace) throws JsonProcessingException {
        RemoteCache<String, String> cache = getCacheSpace(cacheSpace);
        String value = mapper.writerWithView(classView).writeValueAsString(result);

        cache.put(primaryKey, value);
    }

    public List<T> getResultList(String entityID, Class<?> classView, Class<? extends JsonResultResponse<T>> clazz, CacheAlliance cacheSpace) {
        return getResultListGeneric(entityID, classView, clazz, cacheSpace);
    }
    
    public static CacheStatus getCacheStatus(CacheAlliance cacheSpace) {
    
        CacheStatus status = new CacheStatus(cacheSpace.getCacheName());
        
        RemoteCache<String, String> cache = getCacheSpace(cacheSpace);
        
        status.setNumberOfEntities(cache.size());
        
        log.info("Cache Status: " + status);
        
        return status;
    }

    private List<T> getResultListGeneric(String entityID, Class<?> classView, Class<? extends JsonResultResponse<T>> clazz, CacheAlliance cacheSpace) {

        String json = getCacheSpace(cacheSpace).get(entityID);
        if (json == null)
            return null;
        JsonResultResponse<T> result;
        try {
            result = mapper.readerWithView(classView).forType(clazz).readValue(json);
        } catch (IOException e) {
            log.error("Error during deserialization ", e);
            throw new RuntimeException(e);
        }
        return result.getResults();
    }

    public static void close() {
        try {
            rmc.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("closing cache");
    }

}
