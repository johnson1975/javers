package org.javers.repository.sql;

import org.javers.common.properties.AbstractConfiguration;
import org.javers.common.properties.PropertyConfiguration;
import org.javers.common.validation.Validate;
import org.polyjdbc.core.dialect.Dialect;

import javax.sql.DataSource;

import static org.javers.repository.sql.DataSourceFactory.createDBCP;

/**
 * @author bartosz walacik
 */
public class SqlRepositoryConfiguration extends AbstractConfiguration {
    public static final String DIALECT_PROPERTY =   "jdbc.dialect";
    public static final String URL_PROPERTY =       "jdbc.database.url";
    public static final String USERNAME_PROPERTY =  "jdbc.database.username";
    public static final String PASSWORD_PROPERTY =  "jdbc.database.password";

    private DialectName dialectName;
    private String databaseUrl;
    private String username;
    private String password;

    public SqlRepositoryConfiguration() {
        super(new PropertyConfiguration("jdbc-default.properties"));
        assemble();
    }

    public void withDialect(DialectName dialect) {
        Validate.argumentIsNotNull(dialect);
        this.dialectName = dialect;
    }

    public void withDatabaseUrl(String databaseUrl) {
        Validate.argumentIsNotNull(databaseUrl);
        this.databaseUrl = databaseUrl;
    }

    public void withUsername(String username) {
        this.username = username;
    }

    public void withPassword(String password) {
        this.password = password;
    }

    //-- getters

    /**
     * Sql dialect, mandatory
     */
    public DialectName getDialectName() {
        return dialectName;
    }

    /**
     * JDBC connection url, mandatory
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Database username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return never returns null
     */
    public Dialect getPollyDialect() {
        return dialectName.getPolyDialect();
    }

    protected DataSource createConnectionPool() {
        return  createDBCP(getDialectName().getDriverClass(),
                           getDatabaseUrl(),
                           getUsername(),
                           getPassword());
    }

    @Override
    public void assemble() {
        if (containsPropertyKey(DIALECT_PROPERTY)) {
            dialectName = getEnumProperty(DIALECT_PROPERTY, DialectName.class);
        }
        if (containsPropertyKey(URL_PROPERTY))  {
            databaseUrl = getStringProperty(URL_PROPERTY);
        }
        if (containsPropertyKey(USERNAME_PROPERTY))  {
            username = getStringProperty(USERNAME_PROPERTY);
        }
        if (containsPropertyKey(PASSWORD_PROPERTY))  {
            password = getStringProperty(PASSWORD_PROPERTY);
        }
    }
}
