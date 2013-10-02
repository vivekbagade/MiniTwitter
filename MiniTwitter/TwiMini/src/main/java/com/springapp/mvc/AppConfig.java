package com.springapp.mvc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.OperationType;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/22/13
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan(basePackages = "com.springapp.mvc")
@PropertySource(value = "classpath:/application.properties")
@EnableWebMvc
@EnableTransactionManagement
public class AppConfig{
    private static Connection connection =null;
    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        PGPoolingDataSource source = new PGPoolingDataSource();

        source.setDataSourceName("Datasource");
        source.setServerName("172.16.155.82");
        source.setPortNumber(5432);
        source.setDatabaseName("vivek");
        source.setUser("vivek");
        source.setPassword("123");
        source.setMaxConnections(30);
        System.out.println("hello");
        return new JdbcTemplate(source);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Keyspace keyspace(){
        CassandraHostConfigurator configurator = new CassandraHostConfigurator("172.16.155.82");
        configurator.setPort(9160);
        configurator.setMaxActive(100);
        configurator.setAutoDiscoverHosts(true);
        configurator.setRunAutoDiscoveryAtStartup(true);
        configurator.setCassandraThriftSocketTimeout(100);

        Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", configurator);
        Keyspace keyspace = HFactory.createKeyspace("MiniTwitter", cluster);

        keyspace.setConsistencyLevelPolicy(new ConsistencyLevelPolicy() {
            @Override
            public HConsistencyLevel get(OperationType operationType) {
                return HConsistencyLevel.ONE;
            }

            @Override
            public HConsistencyLevel get(OperationType operationType, String s) {
                return HConsistencyLevel.ONE;
            }
        });
        return keyspace;
    }

    @Bean
    public Channel getChannel()  {

        ConnectionFactory connectionFactory=new ConnectionFactory();
       connectionFactory.setHost("localhost");

        Channel channel=null;

        try {
            getConnection(connectionFactory);
            channel=connection.createChannel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return channel;
    }

    private void getConnection(ConnectionFactory connectionFactory) throws IOException {
        if(connection==null)
             connection=connectionFactory.newConnection();

    }

    @Bean
    @Scope("prototype")
    public String getString()
    {
        System.out.println("hoi");
        return "hoi";
    }

}
