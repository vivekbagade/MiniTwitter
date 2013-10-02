package com.springapp.mvc.rabbitmq;


import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueRead {
    //@Autowired
    private Channel channel;
    private static Connection connection =null;
    private ExecutorService executorService;

    public Channel getChannel()  {

        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("localhost");
        executorService = Executors.newFixedThreadPool(1);
        Channel channel=null;

        try {
            getConnection(connectionFactory);
            channel=connection.createChannel();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return channel;
    }

    private void getConnection(ConnectionFactory connectionFactory) throws IOException {
        if(connection==null)
            connection=connectionFactory.newConnection(executorService);

    }

    public void receive() throws IOException, InterruptedException {
        channel=getChannel();
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.queueDeclare("TwitterConsumptionQueue", true, false, false, null);
        channel.basicConsume("TwitterConsumptionQueue", true, new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                System.out.println(String.valueOf(body));
            }

        });
//        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//        String message = new String(delivery.getBody());
//        System.out.println(" [x] Received '" + message + "'");
    }

}
