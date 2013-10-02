package com.springapp.mvc.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/22/13
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class DataExchange {
    private final String EXCHANGE_NAME = "miniTwitter";
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private final Channel channel;

    @Autowired
    public DataExchange(Channel channel) throws IOException {
        this.channel=channel;
    }

    public void insert(String message)
    {
        System.out.println("DataExchange called");
        try {
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        try{
            channel.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("Could not close connection");
        }
    }
}


