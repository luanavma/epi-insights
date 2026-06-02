package org.iris.ia.agent;

import org.iris.ia.dto.AISummary;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface AISummaryAgent {
    @SystemMessage("""
    You are an health AI assistant responsible for generating a summary and recommendations based on the analysis of the question and the data provided.
     The steps are:
        1 - Analyze the question and the data provided.
        2 - Generate a summary of the analysis.
        3 - Generate a list of recommendations based on the analysis.
     The answer should be concise and informative, providing insights and actionable recommendations to the user.
     "
    """)
    @UserMessage("""
    Generate a summary and recommendations based on the analysis of the question: {{question}} and the data provided: {{data}}
    """)
    AISummary summary(String question, String data);
}
