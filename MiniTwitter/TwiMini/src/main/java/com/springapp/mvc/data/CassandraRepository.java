package com.springapp.mvc.data;

import com.springapp.mvc.model.Tweet;
import com.springapp.mvc.model.User;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/19/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class CassandraRepository {

    private final Keyspace keyspace;
    private final CassandraHelper helper;

    @Autowired
    public CassandraRepository(Keyspace keyspace,CassandraHelper helper) {
        this.keyspace = keyspace;
        this.helper=helper;
    }

    public List<User> getFollowers(String email,String lastEmail)
    {
        List<User> ret = new LinkedList<User>();
        SliceQuery sliceQuery1 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery1.setColumnFamily("edges");

        sliceQuery1.setKey(email).setRange(helper.getCompositeLS(2L, lastEmail), helper.getCompositeLS(2L, "zzzzzzzz"), false, 30);
        QueryResult<ColumnSlice<Composite, String>> result1 = sliceQuery1.execute();
        List<HColumn<Composite, String>> list = result1.get().getColumns();


        SliceQuery sliceQuery2 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        if(result1.get().getColumns().size()<10)
        {
            sliceQuery2.setColumnFamily("edges");
            sliceQuery2.setKey(email).setRange(helper.getCompositeLS(0L, lastEmail), helper.getCompositeLS(0L, "zzzzzzzz"), false, 1000000);
            QueryResult<ColumnSlice<Composite, String>> result2 = sliceQuery2.execute();
            list.addAll(result2.get().getColumns());
        }

        int i = 0;
        for (HColumn<Composite, String> value : list) {
            User user=new User();
            user.setEmail(list.get(i).getValue());
            user.setName(helper.getUsername(list.get(i).getValue()));
            ret.add(user);
            i++;
        }
        return ret;
    }

    public List<User> getSubscriptions(String email,String lastEmail)
    {
        List<User> ret = new LinkedList<User>();
        SliceQuery sliceQuery1 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery1.setColumnFamily("edges");

        sliceQuery1.setKey(email).setRange(helper.getCompositeLS(1L, lastEmail), helper.getCompositeLS(1L, "zzzzzzzz"), false, 30);
        QueryResult<ColumnSlice<Composite, String>> result1 = sliceQuery1.execute();
        List<HColumn<Composite, String>> list = result1.get().getColumns();


        SliceQuery sliceQuery2 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        if(result1.get().getColumns().size()<10)
        {
            sliceQuery2.setColumnFamily("edges");
            sliceQuery2.setKey(email).setRange(helper.getCompositeLS(0L, lastEmail), helper.getCompositeLS(0L, "zzzzzzzz"), false, 1000000);
            QueryResult<ColumnSlice<Composite, String>> result2 = sliceQuery2.execute();
            list.addAll(result2.get().getColumns());
        }

        int i = 0;
        for (HColumn<Composite, String> value : list) {
            User user=new User();
            user.setEmail(list.get(i).getValue());
            user.setName(list.get(i).getValue());
            ret.add(user);
            i++;
        }
        return ret;
    }


    public List<Tweet> getUserTweets(String email,String lastTimestamp)
    {
        System.out.println("Slice query with "+lastTimestamp);
        List<Tweet> ret=new LinkedList<Tweet>();
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        Composite start=helper.getCompositeSS(lastTimestamp,"zzzzz");
        Composite end=helper.getCompositeSS("", "");

        sliceQuery.setColumnFamily("tweets").setKey(email).setRange(start,end,true,20);
        QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
        List<HColumn<Composite,String>> list=result.get().getColumns();

        for(HColumn<Composite,String> tweetinfo : list)
        {
            Tweet tweet=new Tweet();
            tweet.setTweetid(tweetinfo.getName().getComponent(1).getValue(StringSerializer.get()).toString());
            tweet.setContent(tweetinfo.getValue());
            tweet.setEmail(email);
            tweet.setTimestamp(tweetinfo.getName().getComponent(0).getValue(StringSerializer.get()).toString());
            ret.add(tweet);
        }
        return ret;
    }

    public List<Tweet> getFeed(String email,String lastTimestamp)
    {
        List<Tweet> ret=new LinkedList<Tweet>();
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), CompositeSerializer.get());
        Composite start=helper.getCompositeSS(lastTimestamp, "zzzzzzzz");
        Composite end=helper.getCompositeSS("", "");

        sliceQuery.setColumnFamily("tweetsforuser").setKey(email).setRange(start, end, true, 20);
        QueryResult<ColumnSlice<Composite,Composite>> result=sliceQuery.execute();
        List<HColumn<Composite,Composite>> list=result.get().getColumns();

        for(HColumn<Composite,Composite> tweetinfo :list)
        {
            Tweet tweet=new Tweet();
            System.out.println("tweetid is "+tweetinfo.getName().getComponent(1).getValue(StringSerializer.get()).toString());
            tweet.setTweetid(tweetinfo.getName().getComponent(1).getValue(StringSerializer.get()).toString());
            tweet.setTimestamp(tweetinfo.getName().getComponent(0).getValue(StringSerializer.get()).toString());
            tweet.setEmail(tweetinfo.getValue().getComponent(0).getValue(StringSerializer.get()).toString());
            tweet.setContent(helper.getTweetContent(tweet.getEmail(),tweet.getTweetid(),tweet.getTimestamp()));
            ret.add(tweet);
        }

        return ret;
    }

    public boolean authorize(String email, String password)
    {
        System.out.println("Authorize called");
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),StringSerializer.get(),StringSerializer.get());
        sliceQuery.setColumnFamily("users").setRange("password","password",false,1).setKey(email);
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        System.out.println("the actual password is "+result.get().getColumns().get(0).getValue());
        return (result.get().getColumns().get(0).getValue().equals(password));
    }

    public boolean isEmailPresent(String email)
    {
        System.out.println(helper.isEmailTaken(email));
        return helper.isEmailTaken(email);
    }
    public String getTokenByEmail(String email)
    {
        System.out.println("getEmailFromToken called");
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),StringSerializer.get(),StringSerializer.get());
        sliceQuery.setColumnFamily("tokens").setRange("token","token",false,1).setKey(email);
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        try
        {
            System.out.println("token is "+result.get().getColumns().get(0).getValue());
            return result.get().getColumns().get(0).getValue();
        }
        catch (Exception e)
        {
            System.out.println("No token of email");
            return "";
        }
    }

    public String getAndAddToken(String email)
    {
        String token=helper.getMD5(email+helper.getCurrentTimeStamp());
        while (!addToken(email,token));
        return token;
    }

    public boolean addToken(String email, String token) {
        try{
            Mutator<String> mutator=HFactory.createMutator(keyspace,StringSerializer.get());
            HColumn<String,String> column=HFactory.createColumn("token",token);
            mutator.addInsertion(email,"tokens",column);
            mutator.execute();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public String getUsernameByEmail(String email)
    {
        return helper.getUsername(email);
    }

    public int getNumberOfFollowers(String email)
    {
        QueryResult<Integer> result1= HFactory.createCountQuery(keyspace,StringSerializer.get(),CompositeSerializer.get())
                .setColumnFamily("edges")
                .setKey(email).setRange(helper.getCompositeLS(0L,""),helper.getCompositeLS(0L,"zzzzzz"),100000000).execute();
        QueryResult<Integer> result2= HFactory.createCountQuery(keyspace,StringSerializer.get(),CompositeSerializer.get())
                .setColumnFamily("edges")
                .setKey(email).setRange(helper.getCompositeLS(2L,""),helper.getCompositeLS(2L,"zzzzzz"),100000000).execute();

        return result1.get()+result2.get();

    }

    public int getNumberOfSubscriptons(String email)
    {
        QueryResult<Integer> result1= HFactory.createCountQuery(keyspace,StringSerializer.get(),CompositeSerializer.get())
                .setColumnFamily("edges")
                .setKey(email).setRange(helper.getCompositeLS(0L,""),helper.getCompositeLS(0L,"zzzzzz"),100000000).execute();
        QueryResult<Integer> result2= HFactory.createCountQuery(keyspace,StringSerializer.get(),CompositeSerializer.get())
                .setColumnFamily("edges")
                .setKey(email).setRange(helper.getCompositeLS(1L,""),helper.getCompositeLS(1L,"zzzzzz"),100000000).execute();

        return result1.get()+result2.get();
    }
    public int getNumberOfTweets(String email)
    {
        QueryResult<Integer> result = HFactory.createCountQuery(keyspace,StringSerializer.get(),CompositeSerializer.get())
                .setColumnFamily("tweets")
                .setKey(email).setRange(null,null,1000000000).execute();

        return result.get();
    }

    public List<String> getSearchResults(String string)
    {
        List<String> ret=new LinkedList<String>();
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace,StringSerializer.get(),StringSerializer.get(),StringSerializer.get());
        sliceQuery.setColumnFamily("search").setRange(string+"zzz",string, true, 10).setKey("users");
        QueryResult<ColumnSlice<String,String>> result=sliceQuery.execute();

        List<HColumn<String, String>> list = result.get().getColumns();
        int i = 0;
        for (HColumn<String, String> value : list) {
            ret.add(list.get(i).getName());
            i++;
        }
        return ret;

    }

    public void removeToken(String email) {
        try{
            Mutator<String> mutator=HFactory.createMutator(keyspace,StringSerializer.get());
            mutator.addDeletion(email,"tokens");
            mutator.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String isFollower(String one,String two) {
        if(helper.checkIfTwoWayConnected(one, two) || helper.checkIfOneConnected(one, two))
            return "Following";
        return "Follow";
    }
}
