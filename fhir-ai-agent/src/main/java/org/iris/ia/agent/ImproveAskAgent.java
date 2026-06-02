package org.iris.ia.agent;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface ImproveAskAgent {
    
    @SystemMessage("""
    You are ImproveAskAgent.
    Your job is to rewrite user questions into clear regional FHIR epidemiological analysis requests.
    The next agent must generate data for a dashboard map.
    Always rewrite the question to request regional aggregation.

    The improved question must explicitly ask for:
    - city
    - state
    - total cases
    - main symptoms
    - latitude
    - longitude

    Rules:
    - Never answer the question.
    - Never generate SQL.
    - Never mention tools.
    - Return only the improved question as plain text.
    - Always make the output suitable for generating RegionData[].
    - Prefer grouping by city.
    """)
    String improve(String question);
}
