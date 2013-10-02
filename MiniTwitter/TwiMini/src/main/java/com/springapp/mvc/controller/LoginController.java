package com.springapp.mvc.controller;

import com.springapp.mvc.data.CassandraRepository;
import com.springapp.mvc.data.TweetRepository;
import com.springapp.mvc.model.ImageUpload;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 7/22/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class LoginController {

    @Autowired
    private CassandraRepository cassandraRepository;
    private final TweetRepository tweetRepository;

    @Autowired
    public LoginController(CassandraRepository cassandraRepository,TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
        //this.cassandraRepository = cassandraRepository;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model,HttpServletRequest request,HttpServletResponse response)
    {
        String email,token;
        if(WebUtils.getCookie(request,"token")!=null && WebUtils.getCookie(request,"email")!=null)
        {
            email=WebUtils.getCookie(request,"email").getValue();
            token=WebUtils.getCookie(request,"token").getValue();
            if(cassandraRepository.getTokenByEmail(email).equals(token))
                return giveHome(model,response,email,token);
        }
        return "login";

    }

    @RequestMapping(value = "/logout**" , method = RequestMethod.GET)
    @ResponseBody
    public List<String> logout(HttpServletRequest request){
        System.out.println("User trying to logout "+request.getParameter("email"));
        List<String> list=new LinkedList<String>();
        if(request.getParameter("token").equals(cassandraRepository.getTokenByEmail(request.getParameter("email"))))
        {
            cassandraRepository.removeToken(request.getParameter("email"));
            list.add("success");
            return list;
        }
        list.add("success");
        return list;
    }

    @RequestMapping(value="/home**",method = RequestMethod.GET)
        public String showHome(HttpServletRequest request,HttpServletResponse response, ModelMap model) throws UnsupportedEncodingException {
        String token = request.getParameter("token");

        byte[] authbytes= DatatypeConverter.parseBase64Binary(request.getParameter("email")) ;
        String email=new String(authbytes,"UTF-8");

        return giveHome(model,response,email,token);
    }

    public String giveHome(ModelMap model,HttpServletResponse response,String email,String token)
    {
        String name =  cassandraRepository.getUsernameByEmail(email);
        int tweetcount = cassandraRepository.getNumberOfTweets(email);
        int subscriptioncount = cassandraRepository.getNumberOfSubscriptons(email);
        int followercount = cassandraRepository.getNumberOfFollowers(email);
        response.addCookie(new Cookie("token",token));
        response.addCookie(new Cookie("email",email));
        model.addAttribute("name",name);
        model.addAttribute("tweetcount",tweetcount);
        model.addAttribute("followingcount",followercount);
        model.addAttribute("subscriptioncount",subscriptioncount);
        List<String> trends = tweetRepository.getRecentTrends();
        model.addAttribute("trends",trends);

        ImageUpload imageUpload = new ImageUpload(email,token);
        model.addAttribute("ImageUploadForm",imageUpload);
        System.out.println("Image Upload object added with email:" + imageUpload.getEmail());
        File imagefile = new File("/home/vivek/TwiMini/src/main/webapp/static/img/" + email + ".jpg");
        if(imagefile.exists()) {
            String profilepiclocation = "/static/img/" + email + ".jpg";
            model.addAttribute("profilepiclocation",profilepiclocation);
        }
        else {
            String profilepiclocation = "/static/img/default.jpg";
            model.addAttribute("profilepiclocation",profilepiclocation);

        }

        return "template";
    }

    @RequestMapping(value="/uploadimage",method = RequestMethod.POST)
    public String uploaduserimage(ModelMap modelMap,@ModelAttribute(value = "ImageUploadForm")ImageUpload imageUpload,BindingResult result,
        HttpServletResponse response)
    {
        if(!result.hasErrors()) {
            System.out.println("Started the process of uploading the image");
            System.out.println("email id is:" +imageUpload.getEmail());
            CommonsMultipartFile file = imageUpload.getFile();
            System.out.println("FileName:"+file.getOriginalFilename());
            System.out.println("Contenttype:"+ file.getContentType());
            String filePath ="/home/vivek/TwiMini/src/main/webapp/static/img/" + imageUpload.getEmail() + ".jpg";

            try {
                FileOutputStream outputStream = new FileOutputStream(new File(filePath));
                outputStream.write(file.getFileItem().get());
                outputStream.close();
                System.out.println("Successfully uploaded the image of the user at:" + filePath);
            } catch (Exception e) {
                System.out.println("Error while saving file at:" + filePath);

            }

        }
        else {
            System.out.println("Image upload had certain errors");
        }
        return giveHome(modelMap,response,imageUpload.getEmail(),imageUpload.getToken());

    }
}

