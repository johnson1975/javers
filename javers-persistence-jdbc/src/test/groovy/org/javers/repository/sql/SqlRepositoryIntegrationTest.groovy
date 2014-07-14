package org.javers.repository.jdbc

import org.javers.repository.sql.SqlRepositoryBuilder
import spock.lang.Ignore
import spock.lang.Specification

/**
 * [Integration Test] requires PostgreSQL
 *
 * @author bartosz walacik
 */
class SqlRepositoryIntegrationTest extends Specification {

    @Ignore
    def "should create Postgre schema"() {
        given:
        def sql = SqlRepositoryBuilder.sqlDiffRepository()
                 .configure("integration/jdbc-postgre-test.properties")
                 .build()

        expect:
        true
    }
}
