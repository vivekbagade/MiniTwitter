package com.springapp.mvc.data;



import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.OperationType;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

public class UserRepository {


    public static void main(String args[])
      {   System.out.println("done mutation");
          CassandraHostConfigurator configurator = new CassandraHostConfigurator("127.0.0.1");
          configurator.setPort(9160);
          configurator.setMaxActive(100);
          configurator.setAutoDiscoverHosts(true);
          configurator.setRunAutoDiscoveryAtStartup(true);
          configurator.setCassandraThriftSocketTimeout(100);

          Cluster cluster= HFactory.getOrCreateCluster("Test Cluster", "127.0.0.1:9160");
          Keyspace keyspace=HFactory.createKeyspace("MiniTwitter",cluster);

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
          System.out.println("done mutation");
          Composite composite=new Composite();
          composite.addComponent("Second Tweet", StringSerializer.get());
          composite.addComponent(200L, LongSerializer.get());
          System.out.println("done mutation");


          Composite composite1=new Composite();
          composite1.addComponent("vivek@bagade.com",StringSerializer.get());
          composite1.addComponent(100L,LongSerializer.get());


          Mutator<String> mutator1=HFactory.createMutator(keyspace, StringSerializer.get());
          HColumn<Composite,Composite> column = HFactory.createColumn( composite,composite1);
          Mutator<String> addInsertion=mutator1.addInsertion("1","tweetsforuser",column);

          mutator1.execute();
          System.out.println("done mutation");
      }
}
