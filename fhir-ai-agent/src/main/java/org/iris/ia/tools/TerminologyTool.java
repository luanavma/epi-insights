package org.iris.ia.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.iris.ia.dto.TerminologyResult;

import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.iris.service.SqlExecutor;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class TerminologyTool {

    @Inject
    SqlExecutor sqlExecutor;

    @Nonnull
    @SuppressWarnings("null")
    @Tool("""
        Use this tool to discover FHIR terminology codes from the database.
        It returns distinct code/text values for Condition and Observation resources.
        Use it before generating disease-specific SQL when the coding representation is unknown.
        """)
    public List<TerminologyResult> discoverTerminology(String question) {
        String sql = """
                SELECT
                    ResourceType,
                    GetJSON(GetJSON(ResourceString,'code'),'coding') AS CodingJson,
                    GetProp(GetJSON(ResourceString,'code'),'text') AS TextValue
                FROM HSFHIR_X0001_R.Rsrc
                WHERE ResourceType IN ('Condition','Observation')
                GROUP BY
                    ResourceType,
                    GetJSON(GetJSON(ResourceString,'code'),'coding'),
                    GetProp(GetJSON(ResourceString,'code'),'text')
                ORDER BY
                    ResourceType,
                    TextValue
                """;

        List<Map<String,Object>> rows = sqlExecutor.execute(sql);

        return rows.stream()
        .map(row -> {
            // normalize keys to lowercase for robust access
            Map<String,Object> norm = row.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey() == null ? "" : e.getKey().toLowerCase(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
            Object rt = norm.get("resourcetype");
            Object coding = norm.get("codingjson");
            Object text = norm.get("textvalue");
            return new TerminologyResult(
                    rt == null ? null : rt.toString(),
                    coding == null ? null : coding.toString(),
                    text == null ? null : text.toString()
            );
        })
        .toList();
    }
}
