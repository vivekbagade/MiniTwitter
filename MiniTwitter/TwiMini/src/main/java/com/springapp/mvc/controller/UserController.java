package com.springapp.mvc.controller;

import com.springapp.mvc.data.CassandraRepository;
import com.springapp.mvc.data.UserRepository;
import com.springapp.mvc.model.User;
import com.springapp.mvc.rabbitmq.DataExchange;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.KeySlice;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
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
public class UserController {

    private final CassandraRepository cassandraRepository;
    private final DataExchange dataExchange;
    private final UserRepository userRepository;
    private final SanitizationHelper helper;

    @Autowired
    public UserController(CassandraRepository cassandraRepository,DataExchange dataExchange,UserRepository userRepository,SanitizationHelper helper) {
        this.cassandraRepository=cassandraRepository;
        this.dataExchange=dataExchange;
        this.userRepository=userRepository;
        this.helper=helper;
    }


    @RequestMapping(value = "/login" , method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> login(@RequestBody Map<String, String> user) throws JSONException {

        Map<String,String> jsonObject=new HashMap<String, String>();

        if(helper.isSafeEmail(user.get("email")) && helper.isSafeString(user.get("password")) && cassandraRepository.isEmailPresent(user.get("email"))
             && cassandraRepository.authorize(user.get("email"), user.get("password")))
        {
            jsonObject.put("result","success");
            jsonObject.put("email",user.get("email"));
            String token=cassandraRepository.getAndAddToken(user.get("email"));
            jsonObject.put("token",token);
            userRepository.addToken(user.get("email"),token);
            return jsonObject;
        }
        jsonObject.put("result","failure");
        return jsonObject;
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    @ResponseBody
    public void userUpdate(@RequestBody Map<String, String> user) throws JSONException
    {
        if(helper.isSafeEmail(user.get("email")) && helper.isSafeString(user.get("password"))
                && cassandraRepository.isEmailPresent(user.get("email")))
        {
            JSONObject jsonObject=new JSONObject(user);
            jsonObject.put("type",1);
            String passable=jsonObject.toString();
            dataExchange.insert(passable);
        }
    }

    @RequestMapping(value = "/followers/{email}",method = RequestMethod.GET)
    @ResponseBody
    public List<User> followersList(@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getFollowers(finalEmail,"");
    }


    @RequestMapping(value = "/followers/{email}/{lastFollowerEmail}",method = RequestMethod.GET)
    @ResponseBody
    public List<User> nextFollowersList(@PathVariable("email") String email,@PathVariable("lastFollowerEmail") String lastFollowerEmail,
                                        HttpServletResponse response) throws UnsupportedEncodingException
    {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        authbytes= DatatypeConverter.parseBase64Binary(lastFollowerEmail) ;
        String lastEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getFollowers(finalEmail,lastEmail);
    }

    @RequestMapping(value = "/subscriptions/{email}",method = RequestMethod.GET)
    @ResponseBody
    public List<User> subscriptionsList(@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getSubscriptions(finalEmail, "");
    }

    @RequestMapping(value = "/subscriptions/{email}/{lastSubscriptionEmail}",method = RequestMethod.GET)
    @ResponseBody
    public List<User> nextSubscriptionsList(@PathVariable("email") String email,@PathVariable("lastSubscriptionEmail") String lastSubscriptionEmail,
                                            HttpServletResponse response) throws UnsupportedEncodingException
    {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        authbytes= DatatypeConverter.parseBase64Binary(lastSubscriptionEmail) ;
        String lastEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getSubscriptions(finalEmail,lastEmail);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> add(@RequestBody Map<String, String> user) throws JSONException {

        System.out.println("Creating new user: " + user.get("email") + " " + user.get("password"));
        Map<String,String> result=new HashMap<String, String>();
        if(helper.isSafeEmail(user.get("email"))  && helper.isSafeString(user.get("password")) && helper.isSafeString(user.get("name"))
                && !userRepository.isUserPresent(user.get("email")))
        {
            userRepository.addUser(user.get("email"),user.get("password"),user.get("name"));
            JSONObject jsonObject=new JSONObject(user);
            jsonObject.put("type",2);
            String passable=jsonObject.toString();
            dataExchange.insert(passable);
            result.put("result","1");
            result.put("email", user.get("email"));
            result.put("token",cassandraRepository.getAndAddToken(user.get("email")));
            return result;
        }
        result.put("result","0");
        System.out.println("result bogus");
        return result;
    }

    @RequestMapping(value = "/subscriptions/{id}" ,method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String,String> unfollow(@RequestBody Map<String,String> info) throws JSONException {
        Map<String,String> result = new HashMap<String, String>();
        if(helper.isSafeEmail(info.get("otherEmail")) && helper.isSafeEmail(info.get("email")) &&
                info.get("token").equals(cassandraRepository.getTokenByEmail(info.get("email"))))
        {
            JSONObject jsonObject=new JSONObject(info);
            jsonObject.put("email",info.get("email"));
            jsonObject.put("type",4);
            String passable=jsonObject.toString();
            dataExchange.insert(passable);
            result.put("result","1");
            return result;
        }
        result.put("result","0");
        return result;
    }

    @RequestMapping(value = "/users/{id}" ,method = RequestMethod.PUT)
    @ResponseBody
    public Map<String,String> follow(@RequestBody Map<String,String> info) throws JSONException {
        Map<String,String> result = new HashMap<String, String>();
        if(helper.isSafeEmail(info.get("otherEmail")) && helper.isSafeEmail(info.get("email")) &&
                info.get("token").equals(cassandraRepository.getTokenByEmail(info.get("email"))))
        {
            JSONObject jsonObject=new JSONObject(info);
            jsonObject.put("type",3);
            String passable=jsonObject.toString();
            dataExchange.insert(passable);
            result.put("result","1");
            return result;
        }
        result.put("result","0");
        return result;
    }

    @RequestMapping(value = "/search/{email}" , method=RequestMethod.GET)
    @ResponseBody
    public List<String> search(@PathVariable("email") String email,HttpServletResponse response) throws UnsupportedEncodingException {
        //response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(email) ;
        String finalEmail=new String(authbytes,"UTF-8");
        return cassandraRepository.getSearchResults(finalEmail);
    }

    @RequestMapping(value = "/isfollower**")
    @ResponseBody
    public String isFollower(HttpServletRequest request) throws UnsupportedEncodingException {
        byte[] authbytes= DatatypeConverter.parseBase64Binary(request.getParameter("email")) ;
        String one=new String(authbytes,"UTF-8");
        authbytes= DatatypeConverter.parseBase64Binary(request.getParameter("otherEmail")) ;
        String two=new String(authbytes,"UTF-8");;
        return cassandraRepository.isFollower(one,two);
    }
}