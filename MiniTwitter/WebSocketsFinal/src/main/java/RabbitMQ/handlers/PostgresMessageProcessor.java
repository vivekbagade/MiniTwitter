package RabbitMQ.handlers;

import RabbitMQ.data.TweetRepository;
import RabbitMQ.data.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/21/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostgresMessageProcessor extends MessageProcessor {

    private static UserRepository userRepository;
    private static TweetRepository tweetRepository;

    public PostgresMessageProcessor() {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("Datasource");
        source.setServerName("172.16.155.82");
        source.setPortNumber(5432);
        source.setDatabaseName("vivek");
        source.setUser("vivek");
        source.setPassword("123");
        source.setMaxConnections(30);
        JdbcTemplate jdbcTemplate=new JdbcTemplate(source);
        this.userRepository = new UserRepository(jdbcTemplate);
        this.tweetRepository = new TweetRepository(jdbcTemplate);


    }

    @Override
    public boolean processMessage(String message) {
        return super.processMessage(message);
    }

    @Override
    protected void processModifyUserMessage(JSONObject jsonObject) {
        try {
            userRepository.modifyUser(jsonObject.getString("email"),jsonObject.getString("name"),jsonObject.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processUnFollowMessage(JSONObject jsonObject) {
        try {
            userRepository.unfollow(jsonObject.getString("email"),jsonObject.getString("otherEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processFollowMessage(JSONObject jsonObject) {
        try {
            userRepository.follow(jsonObject.getString("email"),jsonObject.getString("otherEmail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processRegisterMessage(JSONObject jsonObject) {

    }

    @Override
    protected void processAddTweetMessage(JSONObject jsonObject) {
        try {
            jsonObject.put("email",super.sanitizeString(jsonObject.getString("email")));
            tweetRepository.addTweet(jsonObject.getString("email"),jsonObject.getString("content"),jsonObject.getString("timestamp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
