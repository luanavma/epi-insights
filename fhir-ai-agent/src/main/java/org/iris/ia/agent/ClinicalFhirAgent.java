package org.iris.ia.agent;

import org.iris.ia.dto.AskRequest;
import org.iris.ia.dto.AskResponse;

import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
@RegisterAiService
public interface ClinicalFhirAgent {

    public AskResponse ask(AskRequest request);
}
