package RabbitMQ.data;


import RabbitMQ.model.Tweet;
import RabbitMQ.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Repository
public class TweetRepository {
    private static JdbcTemplate jdbcTemplate;

    public TweetRepository(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate=jdbcTemplate;
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

    /*public int getUserIDByEmail(String email)
    {
        return jdbcTemplate.queryForObject("Select userid from users where email=?",new Object[]{email},new BeanPropertyRowMapper<User>(User.class)).getUserID();
    }*/


    public List<Tweet> getRelevanttweets(String email,String tstamp)
    {
        System.out.println("Fetching the userid of the user for email id" + email);
        if(tstamp!="")
        {
            return jdbcTemplate.query("Select email,content,tstamp from posts where tweetid in(select tweetid from tweetsforuser where email=? AND tstamp < ? ORDER BY tstamp DESC limit 5)",new Object[]{email,tstamp},new BeanPropertyRowMapper<Tweet>(Tweet.class));
        }
        else
        {
            return jdbcTemplate.query("Select email,content,tstamp from posts where tweetid in(select tweetid from tweetsforuser where email=? ORDER BY tstamp DESC limit 5)",new Object[]{email},new BeanPropertyRowMapper<Tweet>(Tweet.class));
        }
    }
}
