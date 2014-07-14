package org.javers.repository.jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.javers.repository.jdbc.schema.FixedSchemaFactory
import org.javers.repository.sql.SqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.repository.sql.schema.FixedSchemaFactory
import org.polyjdbc.core.dialect.Dialect;
import spock.lang.Specification;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.javers.repository.jdbc.JdbcDiffRepositoryBuilder.*;

/**
 * @author bartosz walacik
 */
class SqlRepositoryBuilderTest extends Specification{

    def "should build SqlRepository with default H2 config"(){
        when:
        SqlRepositoryBuilder builder = SqlRepositoryBuilder.sqlDiffRepository()
        builder.build()

        then:
        builder.getContainerComponent(SqlRepository)
        builder.getContainerComponent(Dialect).code == "H2"
        builder.getContainerComponent(BasicDataSource).url == "jdbc:h2:mem:test"
    }


    def "should create Commit table if not exists"() {
        when:
        SqlRepositoryBuilder builder = SqlRepositoryBuilder.sqlDiffRepository()
        builder.build()

        then:
        DataSource ds = builder.getContainerComponent(BasicDataSource.class)
        1 == queryForInt(ds,
                        "SELECT  count(*) \n" +
                        "FROM    INFORMATION_SCHEMA.TABLES\n"+
                        "WHERE   TABLE_NAME      = '" + FixedSchemaFactory.COMMIT_TABLE_NAME.toUpperCase() + "'\n")
    }

    /**
     * some jdbc java boilerplate
     */
    private int queryForInt(DataSource ds, String sql) {
        int result;
        try {
            Connection con = ds.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            ResultSet rset = stmt.executeQuery();
            rset.next();
            result = rset.getInt(1);

            rset.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
 }
