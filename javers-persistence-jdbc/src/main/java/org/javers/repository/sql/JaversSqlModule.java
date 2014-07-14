package org.javers.repository.sql;

import org.javers.common.pico.JaversModule;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repository
 *
 * @author bartosz walacik
 */
public class JaversSqlModule implements JaversModule{
    private static Class[] moduleComponents = new Class[] {SqlRepository.class,
                                                           FixedSchemaFactory.class,
                                                           JaversSchemaManager.class,
                                                           DataSourceTransactionManager.class,
                                                           QueryRunnerFactory.class};

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }
}
