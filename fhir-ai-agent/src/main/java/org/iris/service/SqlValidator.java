package org.iris.service;

import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SqlValidator {

    /**
     * Validação bem defensiva: só permite um SELECT único.
     *
     * Obs: isso não substitui controles de permissão no banco.
     */
    public void validateReadOnlySelect(String sql) {
        if (sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("SQL vazio");
        }

        String s = sql.trim();
        String lower = s.toLowerCase(Locale.ROOT);

        // evitar múltiplas statements
        if (lower.contains(";") || lower.contains("--") || lower.contains("/*") || lower.contains("*/")) {
            throw new IllegalArgumentException("SQL contém tokens não permitidos");
        }

        if (!lower.startsWith("select") && !lower.startsWith("with")) {
            throw new IllegalArgumentException("Somente SELECT/CTE (WITH) é permitido");
        }

        // bloquear DML/DDL mais comuns
        String[] forbidden = { "insert ", "update ", "delete ", "merge ", "drop ", "alter ", "create ", "truncate ",
                "grant ", "revoke ", "execute ", "call ", "xp_" };
        for (String f : forbidden) {
            if (lower.contains(f)) {
                throw new IllegalArgumentException("SQL contém comando não permitido: " + f.trim());
            }
        }
    }
}
