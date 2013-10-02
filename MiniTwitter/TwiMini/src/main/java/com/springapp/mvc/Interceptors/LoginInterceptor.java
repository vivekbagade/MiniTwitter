package com.springapp.mvc.Interceptors;

import com.springapp.mvc.data.CassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

public class LoginInterceptor implements HandlerInterceptor{


    private final CassandraRepository cassandraRepository;

    @Autowired
    public LoginInterceptor(CassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("prehandle called");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(request.getParameter("email")) ;
        String finalEmail=new String(authbytes,"UTF-8");
        String token = request.getParameter("token");
        System.out.println("The received token is "+token);
        String actualToken=cassandraRepository.getTokenByEmail(finalEmail);
        if(!actualToken.equals(token))
        {
            response.sendRedirect("error");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
