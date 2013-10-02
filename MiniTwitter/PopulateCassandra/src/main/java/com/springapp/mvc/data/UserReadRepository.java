package com.springapp.mvc.data;


import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.OperationType;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

public class UserReadRepository {

    public Composite getComposite( String username, long tweetid) {
        Composite composite = new Composite();
        composite.addComponent(username, StringSerializer.get());
        composite.addComponent(tweetid, LongSerializer.get());
        return composite;
    }


    public static void main(String args[]) {
        UserReadRepository userReadRepository = new UserReadRepository();
        CassandraHostConfigurator configurator = new CassandraHostConfigurator("localhost");
        configurator.setPort(9160);
        configurator.setMaxActive(100);
        configurator.setAutoDiscoverHosts(true);
        configurator.setRunAutoDiscoveryAtStartup(true);
        configurator.setCassandraThriftSocketTimeout(100);

        Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "127.0.0.1:9160");
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


        Composite composite = userReadRepository.getComposite("vivek@bagade.com",200L);
        Composite compositeEnd = userReadRepository.getComposite("zzzz",99999);
        SliceQuery<String, Composite, String> q = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());

        q.setColumnFamily("tweetsforuser").setKey("1").setRange(compositeEnd,composite,false,3);

        QueryResult<ColumnSlice<Composite,String>> result = q.execute();
        System.out.println(result.get().getColumns().get(1).getValue());
    }
}
