package org.iris.ia.dto;

public record TerminologyResult(
    String conceptId,
    String system,
    String term,
    String resourceType
) {
}
