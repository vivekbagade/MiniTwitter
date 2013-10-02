package com.springapp.mvc.controller;

import com.springapp.mvc.data.CassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 8/6/13
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ProfileController
{
    private final CassandraRepository cassandraRepository;

    @Autowired
    public ProfileController(CassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @RequestMapping(value = "/user/{email}" , method = RequestMethod.GET)
    public String giveUserProfile(ModelMap model,@PathVariable("email") String encodedEmail) throws UnsupportedEncodingException
    {
        byte[] authbytes= DatatypeConverter.parseBase64Binary(encodedEmail) ;
        String email=new String(authbytes,"UTF-8");

        return giveProfile(model,email);
    }

    public String giveProfile(ModelMap model,String email)
    {
        String name =  cassandraRepository.getUsernameByEmail(email);
        int tweetCount = cassandraRepository.getNumberOfTweets(email);
        int subscriptionCount = cassandraRepository.getNumberOfSubscriptons(email);
        int followerCount = cassandraRepository.getNumberOfFollowers(email);
        model.addAttribute("name",name);
        model.addAttribute("email",email);
        model.addAttribute("tweetCount",tweetCount);
        model.addAttribute("followingCount",followerCount);
        model.addAttribute("subscriptionCount",subscriptionCount);
        File imagefile = new File("/home/vivek/TwiMini/src/main/webapp/static/img/" + email + ".jpg");
        if(imagefile.exists()) {
            String profilepiclocation = "/static/img/" + email + ".jpg";
            model.addAttribute("profilepiclocation",profilepiclocation);
        }
        else {
            String profilepiclocation = "/static/img/default.jpg";
            model.addAttribute("profilepiclocation",profilepiclocation);

        }

        return "profile";
    }
}
