package RabbitMQ.data;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import org.springframework.stereotype.Repository;

import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/21/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class CassandraHelper {
    private static Keyspace keyspace;
    URL url= null;
    HttpURLConnection con=null;

    public CassandraHelper(Keyspace keyspace) {
          this.keyspace=keyspace;
        try {
            url = new URL("http://localhost:8081/websocket/api");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("url init prob");
        }
    }

    public String getMD5(String plaintext)
    {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public boolean isEmailTaken(String email)
    {
        SliceQuery sliceQuery= HFactory.createSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get());
        sliceQuery.setColumnFamily("users");
        sliceQuery.setKey(email).setRange("","",false,1);
        QueryResult<ColumnSlice<String,String>> result = sliceQuery.execute();
        if(result.get().getColumns().isEmpty())
            return false;
        return true;
    }

    public boolean checkrev(String first, String second)
    {
        SliceQuery sliceQuery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery.setColumnFamily("edges");
        sliceQuery.setKey(second).setRange(getCompositeLS(0L, first), getCompositeLS(1L, first), false, 1);

        QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
        if (result.get().getColumns().isEmpty() || result.get().getColumns().get(0) == null ||result.get().getColumns().get(0).getValue() == null)
            return false;
        return true;
    }

    public boolean checkIfTwoWayConnected(String first,String second)
    {
        SliceQuery sliceQuery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery.setColumnFamily("edges");

        sliceQuery.setKey(second).setRange(getCompositeLS(0L, first), getCompositeLS(0L, first), false, 1);
        QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
        if (result.get().getColumns().isEmpty() || result.get().getColumns().get(0).getValue() == null)
            return false;
        return true;
    }

    public boolean checkIfOneConnected(String first,String second)
    {
        SliceQuery sliceQuery = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery.setColumnFamily("edges");

        sliceQuery.setKey(second).setRange(getCompositeLS(2L, first), getCompositeLS(2L, first), false, 1);
        QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
        if (result.get().getColumns().isEmpty() || result.get().getColumns().get(0).getValue() == null)
            return false;
        return true;
    }

    public void makeLink(String first, String second, int i) {
        System.out.println(first + " " + second);
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        try {
            mutator.addDeletion(first, "edges", getCompositeLS(0L, second), CompositeSerializer.get());
            mutator.addDeletion(first, "edges", getCompositeLS(1L, second), CompositeSerializer.get());
            mutator.addDeletion(first, "edges", getCompositeLS(2L, second), CompositeSerializer.get());
            mutator.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(i==-1)
                return;
            HColumn<Composite, String> column = HFactory.createColumn(getCompositeLS(new Long(i), second), second);
            Mutator<String> addInsertion = mutator.addInsertion(first, "edges", column);
            mutator.execute();
        }

    }

    public Composite getCompositeLS(Long l, String first) {
        Composite composite = new Composite();
        composite.addComponent(l, LongSerializer.get());
        composite.addComponent(first, StringSerializer.get());
        return composite;
    }

    public Composite getCompositeSS(String first, String second) {
        Composite composite = new Composite();
        composite.addComponent(first, StringSerializer.get());
        composite.addComponent(second, StringSerializer.get());
        return composite;
    }

    public List<String> getFollowers(String email) {
        List<String> ret = new LinkedList<String>();
        SliceQuery sliceQuery1 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery1.setColumnFamily("edges");

        SliceQuery sliceQuery2 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery2.setColumnFamily("edges");

        sliceQuery1.setKey(email).setRange(getCompositeLS(2L, ""), getCompositeLS(2L, "zzzzzzzz"), false, 1000000);
        QueryResult<ColumnSlice<Composite, String>> result1 = sliceQuery1.execute();

        sliceQuery2.setKey(email).setRange(getCompositeLS(0L, ""), getCompositeLS(0L, "zzzzzzzz"), false, 1000000);
        QueryResult<ColumnSlice<Composite, String>> result2 = sliceQuery2.execute();

        List<HColumn<Composite, String>> list = result2.get().getColumns();
        list.addAll(result1.get().getColumns());
        int i = 0;
        for (HColumn<Composite, String> value : list) {
            ret.add(list.get(i).getValue());
            i++;
        }
        return ret;
    }

    public void addInFollowers(String email, String tweetID, String timestamp,String tweet)
    {
        List<String> followers = getFollowers(email);
        for(String follower:followers)
        {
            try{
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                String urlParameters = "email="+follower+"&content="+tweet+"&tweeterEmail="+email;

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
            }
            catch (Exception e){}
            addRelevantTweet(email,follower,timestamp,tweetID);
        }
    }

    public void addRelevantTweet(String email, String follower, String timestamp, String tweetID)
    {
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        HColumn<Composite,Composite> column=HFactory.createColumn(getCompositeSS(timestamp, tweetID),getCompositeSS(email, tweetID));
        mutator.addInsertion(follower,"tweetsforuser",column);
        mutator.execute();
    }

    public void addInOwn(String email,String tweet,String timestamp,String tweetID)
    {
        Composite composite=getCompositeSS(timestamp, tweetID);

        Mutator<String> mutator= HFactory.createMutator(keyspace,StringSerializer.get());
        HColumn<Composite,String> column = HFactory.createColumn(composite,tweet);
        mutator.addInsertion(email,"tweets",column);

        mutator.execute();
    }

    public String getUsername(String email)
    {
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),StringSerializer.get(),StringSerializer.get());
        sliceQuery.setColumnFamily("users").setRange("name","name",false,1).setKey(email);
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        try
        {
            return result.get().getColumns().get(0).getValue();
        }
        catch (Exception e)
        {
            System.out.println("No user");
            return "";
        }
    }

    public String getTweetContent(String email, String tweetid) {
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),CompositeSerializer.get(),StringSerializer.get());
        Composite start=getCompositeSS("",tweetid);
        sliceQuery.setColumnFamily("tweets").setKey(email).setRange(start,start,false,1);
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        try
        {
            return result.get().getColumns().get(0).getValue();
        }
        catch (Exception e)
        {
            System.out.println("No TweetContent of requested id");
            return "";
        }
    }

    public void insertCounterColumnForWord(String word,String timestampwithoutseconds) {

        Mutator<String> mutator = HFactory.createMutator(keyspace,StringSerializer.get());
        // inserting a counter columns with initial value 1L
        mutator.insertCounter(timestampwithoutseconds,"trends",HFactory.createCounterColumn(word, 1L,StringSerializer.get()));
        System.out.println("Inserting counter column for:" + word + "at" + timestampwithoutseconds);
        mutator.execute();
        System.out.println(word + "successfully inserted into cassandra");
    }

}
