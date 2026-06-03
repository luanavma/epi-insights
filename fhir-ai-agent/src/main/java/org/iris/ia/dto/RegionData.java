package org.iris.ia.dto;

public record RegionData(
        String city,
        String state,
        Integer totalCases,
        String mainSymptoms,
        Double latitude,
        Double longitude
) {
}
