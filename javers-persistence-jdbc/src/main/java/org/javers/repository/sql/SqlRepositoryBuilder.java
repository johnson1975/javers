package org.javers.repository.sql;

import org.javers.core.AbstractJaversBuilder;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Supports two configuring methods:
 * <ul>
 *     <li/>by properties file, see {@link #configure(String)}
 *     <li/>programmatically using builder style methods
 * </ul>
 *
 * By default, builder creates h2 in-memory repository
 *
 * @author bartosz walacik
 */
public class SqlRepositoryBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

    private SqlRepositoryConfiguration jdbcConfiguration;

    private SqlRepositoryBuilder() {
        jdbcConfiguration = new SqlRepositoryConfiguration();
    }

    public static SqlRepositoryBuilder sqlDiffRepository() {
        return new SqlRepositoryBuilder();
    }

    /**
     * loads a properties file from classpath, example file:
     * <pre>
     *   jdbc.dialect =POSTGRES
     *   jdbc.database.url =jdbc:postgresql://localhost/javers_db
     *   jdbc.database.username =javers
     *   jdbc.database.password =javers
     * </pre>
     * @param classpathName classpath resource name, ex. "configuration/jdbc-postgres.properties",
     *                      see {@link ClassLoader#getResourceAsStream(String)}
     */
    public SqlRepositoryBuilder configure(String classpathName){
        jdbcConfiguration.readProperties(classpathName);
        return this;
    }

    public SqlRepositoryBuilder withDialect(DialectName dialect) {
        jdbcConfiguration.withDialect(dialect);
        return this;
    }

    public SqlRepositoryBuilder withDatabaseUrl(String databaseUrl) {
        jdbcConfiguration.withDatabaseUrl(databaseUrl);
        return this;
    }

    public SqlRepositoryBuilder withUsername(String username) {
        jdbcConfiguration.withUsername(username);
        return this;
    }

    public SqlRepositoryBuilder withPassword(String password) {
        jdbcConfiguration.withPassword(password);
        return this;
    }

    public SqlRepository build() {
        logger.info("starting up SQL repository module ...");
        bootContainer(new JaversSqlModule(),
                      Arrays.asList(jdbcConfiguration.createConnectionPool(),
                                    jdbcConfiguration.getPollyDialect()));

        ensureSchema();

        return getContainerComponent(SqlRepository.class);
    }

    private void ensureSchema() {
        JaversSchemaManager schemaManager = getContainerComponent(JaversSchemaManager.class);
        schemaManager.ensureSchema();
    }

    /**
     * For testing only
     */
    @Override
    protected <T> T getContainerComponent(Class<T> ofClass) {
        return super.getContainerComponent(ofClass);
    }
}
