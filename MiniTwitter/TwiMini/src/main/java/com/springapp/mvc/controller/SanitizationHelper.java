package com.springapp.mvc.controller;

import org.springframework.stereotype.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/30/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class SanitizationHelper {
    public boolean isSafeEmail(String email)
    {
        return email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    public boolean isSafeString(String string)
    {
        if(string.length()>140)
            return false;
        return string.matches("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)");
    }

}
