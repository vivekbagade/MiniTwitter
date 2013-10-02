package RabbitMQ.handlers;

import RabbitMQ.data.CassandraRepository;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.OperationType;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class CassandraMessageProcessor extends MessageProcessor {
    private static CassandraRepository cassandraRepository;

    public CassandraMessageProcessor() {
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
        this.cassandraRepository = new CassandraRepository(keyspace);
    }

    @Override
    public boolean processMessage(String message) {

        return super.processMessage(message);
    }

    public void processModifyUserMessage(JSONObject jsonObject)
    {
        try
        {
            cassandraRepository.registerUser(jsonObject.getString("email"),jsonObject.getString("name"),jsonObject.getString("password"),false);
        }
        catch (JSONException e){e.printStackTrace();}
    }

    public void processAddTweetMessage(JSONObject jsonObject)
    {
        try {
            try {
                jsonObject.put("content", super.sanitizeString(jsonObject.getString("content")));
                System.out.println("VIP "+jsonObject.getString("email"));
                cassandraRepository.addTweet(jsonObject.getString("email"), jsonObject.getString("content"), jsonObject.getString("timestamp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processRegisterMessage(JSONObject jsonObject)
    {
        try
        {
            cassandraRepository.registerUser(jsonObject.getString("email"),jsonObject.getString("name"),jsonObject.getString("password"),false);
        }
        catch (JSONException e){e.printStackTrace();}
    }

    public void processFollowMessage(JSONObject jsonObject)
    {
        try {
            cassandraRepository.followUser(jsonObject.getString("email"),jsonObject.getString("otherEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processUnFollowMessage(JSONObject jsonObject)
    {
        try {
            cassandraRepository.unfollowUser(jsonObject.getString("email"),jsonObject.getString("otherEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
