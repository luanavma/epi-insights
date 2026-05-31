package org.iris.ia.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

import org.iris.ia.tools.DateTools;
import org.iris.ia.tools.FhirSqlTool;
import org.iris.ia.tools.TerminologyTool;

@ApplicationScoped
@RegisterAiService(tools = {
        FhirSqlTool.class,
        TerminologyTool.class,
        DateTools.class
})
public interface ClinicalFhirAgent {

    @SystemMessage("""
    Você é um assistente FHIR.
    Quando precisar consultar dados, use a ferramenta FhirSqlTool.
    Responda em português.
    """)
    @UserMessage("""
    {{question}}.
    """)
    String ask(String question);
}
