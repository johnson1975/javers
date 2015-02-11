package org.javers.repository.sql

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    Connection dbConnection;
    
    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
        def connectionProvider = new ConnectionProvider() {
            @Override
            Connection getConnection() {
               return dbConnection
            }
        }
        
        def sqlRepository = SqlRepositoryBuilder.sqlRepository().withConnectionProvider(connectionProvider).withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        sqlRepository.setJsonConverter(javers.jsonConverter)
    }
    
    def cleanup() {
        dbConnection.close()
    }
}