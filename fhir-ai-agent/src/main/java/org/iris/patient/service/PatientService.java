package org.iris.patient.service;

import java.util.Optional;

import org.iris.patient.dto.PatientInfoDTO;
import org.iris.patient.model.Patient;
import org.iris.patient.repository.PatientRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PatientService {
    @Inject
    PatientRepository patientRepository;

    public PatientInfoDTO patientGetInfo(String patientKey) {
        Optional<Patient> patientOpt = patientRepository.find("key", patientKey).firstResultOptional();

        Patient patient = patientOpt.orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        PatientInfoDTO dto = new PatientInfoDTO();
        dto.setKey(patient.key);
        dto.setName(patient.name);
        dto.setAddress(patient.address);
        dto.setBirthDate(patient.birthDate != null ? patient.birthDate.toString() : null);
        dto.setGender(patient.gender);

        dto.setMedications(patientRepository.findMedicationTextByPatient(patientKey));
        dto.setConditions(patientRepository.findConditionsByPatient(patientKey));
        dto.setAllergies(patientRepository.findAllergyByPatient(patientKey));

        return dto;

    }

}
