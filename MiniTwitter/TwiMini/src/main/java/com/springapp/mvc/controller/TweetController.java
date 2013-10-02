package com.springapp.mvc.controller;

import com.springapp.mvc.data.CassandraRepository;
import com.springapp.mvc.model.Tweet;
import com.springapp.mvc.rabbitmq.DataExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/22/13
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TweetController {
    private final CassandraRepository cassandraRepository;
    private final DataExchange dataExchange;
    private SanitizationHelper helper;

    @Autowired
    public TweetController(CassandraRepository cassandraRepository,DataExchange dataExchange,SanitizationHelper helper) throws IOException {
        this.cassandraRepository=cassandraRepository;
        this.dataExchange=dataExchange;
        this.helper=helper;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/feed/{email}")
    @ResponseBody
    public List<Tweet> getFeed(@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        System.out.println("Started the process of getting tweets for the user");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getFeed(finalEmail, "zzzzz");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feed/{email}/{lastTimeStamp}")
    @ResponseBody
    public List<Tweet> getNextFeed(@PathVariable("lastTimeStamp") String lastTimeStamp,@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        System.out.println("Started the process of getting tweets for the user");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getFeed(finalEmail, lastTimeStamp);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/tweets")
    @ResponseBody
    public Map<String,String> addTweet(@RequestBody Map<String,String> keyMappedData) throws JSONException
    {
        Map<String,String> result = new HashMap<String, String>();

        if(helper.isSafeEmail(keyMappedData.get("email")) && helper.isSafeString(keyMappedData.get("content")) &&
                keyMappedData.get("token").equals(cassandraRepository.getTokenByEmail(keyMappedData.get("email"))))
        {
            System.out.println(keyMappedData.get("token") + keyMappedData.get("email") + keyMappedData.get("content"));
            JSONObject jsonObject=new JSONObject(keyMappedData);
            jsonObject.put("timestamp",getCurrentTimeStamp());
            jsonObject.put("type",1);
            String passable=jsonObject.toString();
            dataExchange.insert(passable);

            result.put("result","1");
            return result;
        }
        result.put("result","1");
        return result;
    }

    private String getCurrentTimeStamp()
    {
        java.util.Date date= new java.util.Date();
        Timestamp timestamp=(new Timestamp(date.getTime()));
        return timestamp.toString();
    }

    @RequestMapping(value="/tweets/{email}" ,method = RequestMethod.GET)
    @ResponseBody
    public List<Tweet> fetchUserFirstTweets(@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getUserTweets(finalEmail, "zzzzz");
    }

    @RequestMapping(value="/tweets/{email}/{lastTimeStamp}" ,method = RequestMethod.GET)
    @ResponseBody
    public List<Tweet> fetchUserNextTweets(@PathVariable("email") String email,@PathVariable("lastTimeStamp") String lastTimeStamp,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getUserTweets(finalEmail, lastTimeStamp);
    }
}
