package com.springapp.mvc.model;

//import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;



import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 8/19/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageUpload {

    private String email;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private CommonsMultipartFile file;

    public ImageUpload(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public ImageUpload() {

    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CommonsMultipartFile getFile() {
        return file;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }


}