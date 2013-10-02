package com.springapp.mvc.data;


import com.springapp.mvc.model.Tweet;
import com.springapp.mvc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/22/13
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class TweetRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TweetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public int addTweet(String email,String content,String timestamp){

        List<User> followers=jdbcTemplate.query("SELECT email,name from users where email in (select uemail from following where femail=?)", new Object[]{email}, new BeanPropertyRowMapper<User>(User.class));
        System.out.println(followers.size());
        String tweetID = getMD5(email+timestamp);
        for(User follower:followers)
            addRelevantTweet(follower.getEmail(),tweetID,timestamp);
        jdbcTemplate.execute("INSERT INTO posts(tweetid,email,content,tstamp) values ('" + tweetID + "', '"+ email+ "', '"+ content + "', '"+ timestamp +"')");
        System.out.println("Tweet Inserted");
        return 0;
    }

    private void addRelevantTweet(String emailID, String tweetID, String timestamp) {
        System.out.println("Adding into user "+emailID);
        jdbcTemplate.execute("insert into tweetsforuser(email,tweetid,tstamp) values('"+emailID+"','"+tweetID+"','"+timestamp+"')");
    }

    public List<Tweet> getTweetByUserEmail(String email,String tstamp)
    {
        if(tstamp!="")
        {
            return jdbcTemplate.query("SELECT email,content,tstamp from posts where email=? AND tstamp < ? ORDER BY tstamp DESC LIMIT 5",new Object[]{email,tstamp},new BeanPropertyRowMapper<Tweet>(Tweet.class));
        }
        else
        {
            return jdbcTemplate.query("SELECT email,content,tstamp from posts where email=? ORDER BY tstamp DESC LIMIT 5",new Object[]{email},new BeanPropertyRowMapper<Tweet>(Tweet.class));

        }
    }

    public List<String> getRecentTrends() {

        List<String> trends = jdbcTemplate.query("Select word from trends ORDER BY count DESC LIMIT 5", new RowMapper() {
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });
        System.out.println("Top Five trending words of the past hour" +trends);
        return trends;
    }

}
