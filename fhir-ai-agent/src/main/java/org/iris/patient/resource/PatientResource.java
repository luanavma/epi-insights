package org.iris.patient.resource;

import java.util.List;

import org.iris.patient.dto.PatientInfoDTO;
import org.iris.patient.service.PatientService;
import org.iris.patient.service.ai.PatientAIService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/patient")
public class PatientResource {

    @Inject
    PatientAIService patientAIService;

    @Inject
    PatientService patientService;

    @GET
    @Path("/ia/analyze-medication")
    @Produces(MediaType.TEXT_PLAIN)
    public String analyzeMedicationRisks(@QueryParam("key") String key) {

        return patientAIService.analyzeMedicationRisks(key);
    }

    @GET
    @Path("/ia/conditions-history")
    @Produces(MediaType.TEXT_PLAIN)
    public String conditionsHistory(@QueryParam("key") String key) {

        return patientAIService.conditionsHistory(key);
    }

    @GET
    @Path("/ia/answer-question")
    @Produces(MediaType.TEXT_PLAIN)
    public String answerPatientQuestion(@QueryParam("key") String key, @QueryParam("question") String question) {
        return patientAIService.answerQuestion(key, question);
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public PatientInfoDTO searchPatientInfo(@QueryParam("key") String key) {
        return patientService.patientGetInfo(key);
    }
}
