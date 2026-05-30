package org.iris.patient.repository;

import java.util.List;

import org.iris.patient.model.Patient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {

    @Inject
    EntityManager em;

    @Inject
    ObjectMapper mapper;

    @Tool("obtain previous medications in use")
    @SuppressWarnings("unchecked")
    public List<String> findMedicationTextByPatient(String patientKey) {
        List<String> resourceJsonList = em.createNativeQuery("""
                    SELECT ResourceString
                    FROM HSFHIR_X0001_R.Rsrc
                    WHERE Key IN (
                        SELECT Key
                        FROM HSFHIR_X0001_S.MedicationRequest
                        WHERE patient = :patientKey
                    )
                """)
                .setParameter("patientKey", patientKey)
                .getResultList();

        return resourceJsonList.stream()
                .map(this::extractMedicationText)
                .filter(text -> text != null && !text.isBlank())
                .distinct()
                .toList();
    }

    @Tool("obtain clinical Conditions history")
    @SuppressWarnings("unchecked")
    public List<String> findConditionsByPatient(String patientKey) {
        List<String> resourceJsonList = em.createNativeQuery("""
                SELECT ResourceString
                FROM HSFHIR_X0001_R.Rsrc WHERE Key IN (SELECT
                Key
                FROM HSFHIR_X0001_S.Condition
                WHERE Patient =:patientKey
                )
                """)
                .setParameter("patientKey", patientKey)
                .getResultList();

        return resourceJsonList.stream()
                .map(this::extractConditionText)
                .filter(text -> text != null && !text.isBlank())
                .distinct()
                .toList();
    }

    @Tool("obtain patient allergy history")
    @SuppressWarnings("unchecked")
    public List<String> findAllergyByPatient(String patientKey) {
        List<String> resourceJsonList = em.createNativeQuery("""
                SELECT ResourceString
                FROM HSFHIR_X0001_R.Rsrc WHERE Key IN (SELECT
                Key
                FROM HSFHIR_X0001_S.AllergyIntolerance
                WHERE Patient =:patientKey)
                """)
                .setParameter("patientKey", patientKey)
                .getResultList();
        return resourceJsonList.stream()
                .map(this::extractAllergyText)
                .filter(text -> text != null && !text.isBlank())
                .distinct()
                .toList();
    }

    private String extractMedicationText(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return node.at("/medicationCodeableConcept/text").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractConditionText(String json) {
        try {
            JsonNode node = mapper.readTree(json);

            String verificationStatus = node.at("/verificationStatus/coding/0/code").asText();
            if (!"confirmed".equals(verificationStatus)) {
                return null;
            }

            String clinicalStatus = node.at("/clinicalStatus/coding/0/code").asText();
            if (!"active".equals(clinicalStatus) && !"resolved".equals(clinicalStatus)) {
                return null;
            }
            return node.at("/code/text").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractAllergyText(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            String verificationStatus = node.at("/verificationStatus/coding/0/code").asText();
            if (!"confirmed".equals(verificationStatus)) {
                return null;
            }

            String clinicalStatus = node.at("/clinicalStatus/coding/0/code").asText();
            if (!"active".equals(clinicalStatus)) {
                return null;
            }

            return node.at("/code/text").asText();
        } catch (Exception e) {
            return null;
        }
    }

}