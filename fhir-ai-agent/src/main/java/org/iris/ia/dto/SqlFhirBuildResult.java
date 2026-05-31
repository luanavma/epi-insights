package org.iris.ia.dto;

public record SqlFhirBuildResult(
    boolean success,
	String sql,
	SqlQueryResponse query) {
}
