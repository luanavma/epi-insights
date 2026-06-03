package org.iris.ia.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.iris.ia.dto.SimilarRegionData;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

@ApplicationScoped
public class RegionClinicalVectorTool {

    @Inject
    Jdbi jdbi;

    @Tool("""
        Use this tool to find regions with similar clinical behavior.
        It compares vectors from dc.RegionClinicalVector using VECTOR_COSINE.
        Use when the user asks:
        - regiões parecidas
        - comportamento parecido
        - similar regions
        - cidades semelhantes
    """)
    @SuppressWarnings("null")
    public List<SimilarRegionData> findSimilarRegions(String city, String state) {
        String sql = """
            SELECT TOP 10
                target.AddressCity AS TargetCity,
                target.AddressState AS TargetState,
                other.AddressCity AS SimilarCity,
                other.AddressState AS SimilarState,
                other.TotalOccurrences AS TotalOccurrences,
                other.IdentifiedDiseases AS IdentifiedDiseases,
                other.IdentifiedDiseaseCodes AS IdentifiedDiseaseCodes,
                other.MainSymptoms AS MainSymptoms,
                other.ClinicalSummary AS ClinicalSummary,
                VECTOR_COSINE(target.ClinicalVector, other.ClinicalVector) AS Similarity
            FROM dc.RegionClinicalVector target
            JOIN dc.RegionClinicalVector other
                ON other.VectorVersion = target.VectorVersion
               AND NOT (
                    other.AddressCity = target.AddressCity
                    AND other.AddressState = target.AddressState
               )
            WHERE target.AddressCity = :city
              AND target.AddressState = :state
            ORDER BY Similarity DESC
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("city", city)
                        .bind("state", state)
                        .map((rs, ctx) -> new SimilarRegionData(
                                rs.getString("TargetCity"),
                                rs.getString("TargetState"),
                                rs.getString("SimilarCity"),
                                rs.getString("SimilarState"),
                                rs.getDouble("TotalOccurrences"),
                                rs.getString("IdentifiedDiseases"),
                                rs.getString("IdentifiedDiseaseCodes"),
                                rs.getString("MainSymptoms"),
                                rs.getString("ClinicalSummary"),
                                rs.getDouble("Similarity")
                        ))
                        .list()
        );
    }
}