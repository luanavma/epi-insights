package org.iris.ia.flow;

import java.util.List;

import org.iris.ia.agent.SQLFhirBuilderAgent;
import org.iris.ia.dto.TerminologyResult;
import org.iris.ia.tools.TerminologyTool;

import jakarta.inject.Inject;

public class FlowSqlExecuteAgents {
    
    @Inject
    TerminologyTool terminologyTool;

    @Inject
    SQLFhirBuilderAgent sqlFhirBuilderAgent;

    public String buildSql(String question) {

        List<TerminologyResult> terminology =
                terminologyTool.discoverTerminology(question);

        return sqlFhirBuilderAgent.buildSql(
                question,
                terminology
        );
    }

}
