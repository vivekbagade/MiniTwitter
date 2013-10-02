package com.springapp.mvc.data;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
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
    private final Keyspace keyspace;

    @Autowired
    public CassandraHelper(Keyspace keyspace) {
        this.keyspace = keyspace;
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
        sliceQuery.setKey(email).setRange("","zzzzzzzzzz",false,1);
        QueryResult<ColumnSlice<String,String>> result = sliceQuery.execute();
        if(result.get().getColumns().isEmpty())
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


    public String getCurrentTimeStamp()
    {
        java.util.Date date= new java.util.Date();
        Timestamp timestamp=(new Timestamp(date.getTime()));
        return timestamp.toString();
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

    public String getTweetContent(String email, String tweetid,String timestamp) {
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),CompositeSerializer.get(),StringSerializer.get());
        Composite start=getCompositeSS(timestamp,tweetid);
        Composite end=getCompositeSS(timestamp,"zzzz");
        System.out.println("timeStamp"+timestamp);
        sliceQuery.setColumnFamily("tweets").setKey(email).setRange(start,end,false,1);
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        try
        {
            return result.get().getColumns().get(0).getValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No TweetContent of requested id");
            return "";
        }
    }

}
