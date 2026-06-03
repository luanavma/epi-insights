package org.iris.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.iris.ia.dto.SimilarRegionData;
import org.iris.ia.tools.RegionClinicalVectorTool;

@Path("/regions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegionResource {

    @Inject
    RegionClinicalVectorTool regionClinicalVectorTool;

    @GET
    @Path("/similar")
    public List<SimilarRegionData> findSimilarRegions(
            @QueryParam("city") String city,
            @QueryParam("state") String state
    ) {

        if (city == null || city.isBlank()) {
            throw new BadRequestException("city is required");
        }

        if (state == null || state.isBlank()) {
            throw new BadRequestException("state is required");
        }

        return regionClinicalVectorTool.findSimilarRegions(
                city.toUpperCase().trim(),
                state.toUpperCase().trim()
        );
    }
}