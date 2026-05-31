package org.iris.ia.dto;

public record AskResponse(
        String chatId,
        String question,
        String answer,
        SqlFhirBuildResult buildResult) {
}
