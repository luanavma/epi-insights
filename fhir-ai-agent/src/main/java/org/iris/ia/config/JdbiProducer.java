package org.iris.ia.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
public class JdbiProducer {

    @Inject
    DataSource dataSource;

    @Produces
    @ApplicationScoped
    public Jdbi createJdbi() {
        return Jdbi.create(dataSource);
    }

}
