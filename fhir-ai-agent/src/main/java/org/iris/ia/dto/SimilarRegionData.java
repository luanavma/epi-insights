package org.iris.ia.dto;

public record SimilarRegionData(
        String targetCity,
        String targetState,
        String similarCity,
        String similarState,
        Double totalOccurrences,
        String identifiedDiseases,
        String identifiedDiseaseCodes,
        String mainSymptoms,
        String clinicalSummary,
        Double similarity
) {
}