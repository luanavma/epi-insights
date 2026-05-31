package org.iris.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SqlExecutor {

    @Inject
    Jdbi jdbi;

    public List<Map<String,Object>> execute(String sql) {

        return jdbi.withHandle(handle ->
            handle.createQuery(sql)
                  .mapToMap()
                  .list()
        );
    }
}
