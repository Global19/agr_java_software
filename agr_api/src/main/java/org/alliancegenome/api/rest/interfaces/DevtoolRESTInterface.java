package org.alliancegenome.api.rest.interfaces;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.api.entity.CacheStatus;
import org.alliancegenome.api.entity.CacheSummary;
import org.alliancegenome.neo4j.view.View;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/cache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DevtoolRESTInterface {

    @GET
    @JsonView(View.Cacher.class)
    @Path("/status")
    CacheSummary getCacheStatus();

    @GET
    @JsonView(View.CacherDetail.class)
    @Path("/{cacheSpace}")
    CacheStatus getCacheStatusPerSpace(@PathParam("cacheSpace") String cacheSpace);


    @GET
    @Path("/{cacheSpace}/{id}")
    String getCacheObject(
            @PathParam("id") String id,
            @PathParam("cacheSpace") String cacheName
    );
}
