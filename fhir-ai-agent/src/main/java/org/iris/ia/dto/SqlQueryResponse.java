package org.iris.ia.dto;

import java.util.List;
import java.util.Map;

/**
 * Resultado da execução de SQL (somente leitura).
 */
public record SqlQueryResponse(
        List<Map<String, Object>> rows,
        String error) {
}
