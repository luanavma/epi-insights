package org.iris.api;

import org.iris.ia.agent.ClinicalFhirAgent;
import org.iris.ia.agent.ImproveAskAgent;
import org.jboss.logging.Logger;
import java.util.Map;
import java.util.UUID;
import org.iris.ia.dto.AskRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/fhir-agent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FhirAgentResource {
    
    private static final Logger LOG = Logger.getLogger(FhirAgentResource.class);
    @Inject
    ImproveAskAgent improveAskAgent;
    
    @Inject
    ClinicalFhirAgent clinicalFhirAgent;

    @POST
    @Path("/ask")
    public Response ask(AskRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "missing question"))
                    .build();
        }

        try {
            String improvedQuestion = improveAskAgent.improve(request.question());
            return Response.ok(clinicalFhirAgent.ask(improvedQuestion)).build();
        } catch (Exception e) {
            String id = UUID.randomUUID().toString();
            LOG.errorf(e, "Error id %s while handling /fhir-agent/ask", id);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("details", "Error id " + id, "message", e.getMessage()))
                    .build();
        }
    }
}
