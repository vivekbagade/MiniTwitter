import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

public class AppConfig {


    Cluster cluster= HFactory.getOrCreateCluster("HectorCluster",new CassandraHostConfigurator("localhost:9160"));
    Keyspace keyspace=HFactory.createKeyspace("KeySpace1",cluster);
}
