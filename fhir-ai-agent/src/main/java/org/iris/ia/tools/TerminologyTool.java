package org.iris.ia.tools;

import java.util.Locale;

import org.iris.ia.dto.TerminologyResult;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Resolver bem simples de "terminologia"/tipo de recurso FHIR baseado na pergunta.
 *
 * Em um próximo passo isso pode ser trocado por um agente LLM ou por consulta a um servidor Terminology.
 */
@ApplicationScoped
public class TerminologyTool {

    @Tool("""
            Tries to infer the most relevant FHIR resourceType and a concept term from a free-text clinical question.

            Returns a TerminologyResult with:
            - conceptId: optional/free
            - system: optional/free
            - term: inferred term
            - resourceType: inferred FHIR type (Patient, Observation, Encounter, MedicationRequest, Condition, Procedure)
            """)
    public TerminologyResult getTerminologyResult() {
        String sql = """
                SELECT
                    ResourceType,
                    GetJSON(GetJSON(ResourceString,'code'),'coding') AS CodingJson,
                    GetProp(GetJSON(ResourceString,'code'),'text') AS TextValue
                FROM HSFHIR_X0001_R.Rsrc
                WHERE ResourceType IN ('Condition','Observation')
                GROUP BY
                    ResourceType,
                    GetProp(GetJSON(ResourceString,'code'),'text')
                ORDER BY
                    ResourceType""";

        return new TerminologyResult(
                null,
                null,
                null,
                null);
    }

}
