package org.alliancegenome.api.rest.interfaces;

import org.alliancegenome.api.repository.CacheStatus;
import org.alliancegenome.api.repository.CacheSummary;
import org.alliancegenome.api.repository.DiseaseCacheRepository;
import org.alliancegenome.neo4j.repository.ExpressionCacheRepository;
import org.alliancegenome.neo4j.repository.InteractionCacheRepository;
import org.alliancegenome.neo4j.repository.PhenotypeCacheRepository;

public class DevtoolController implements DevtoolRESTInterface {

    @Override
    public CacheSummary getCacheStatus() {
        CacheSummary summary = new CacheSummary();

        DiseaseCacheRepository cacheRepository = new DiseaseCacheRepository();
        InteractionCacheRepository interactionCacheRepository = new InteractionCacheRepository();
        ExpressionCacheRepository expressionCacheRepository = new ExpressionCacheRepository();
        PhenotypeCacheRepository phenotypeCacheRepository = new PhenotypeCacheRepository();

        summary.addCacheStatus(cacheRepository.getCacheStatus());
        summary.addCacheStatus(interactionCacheRepository.getCacheStatus());
        summary.addCacheStatus(expressionCacheRepository.getCacheStatus());
        summary.addCacheStatus(phenotypeCacheRepository.getCacheStatus());


        return summary;
    }
}
