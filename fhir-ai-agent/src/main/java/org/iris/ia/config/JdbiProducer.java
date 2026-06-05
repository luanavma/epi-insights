package org.iris.ia.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;

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
