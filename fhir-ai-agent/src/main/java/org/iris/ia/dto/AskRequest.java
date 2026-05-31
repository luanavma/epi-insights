package org.iris.ia.dto;

public record AskRequest(
        String chatId,
        String question) {
}
