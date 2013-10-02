package com.RabbitmqClient.rabbitmq;

import com.RabbitmqClient.messageCollecor.QueueProcessor;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 1/6/13
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rabbitmq {
    private RmqConfiguration configuration;
    private ConnectionFactory connectionFactory;// = new ConnectionFactory();
    private ExecutorService executorService;// = Executors.newFixedThreadPool(1);
    private Connection conn;// = factory.newConnection(executorService);
    private Channel channel;// = conn.createChannel();

    public Rabbitmq(RmqConfiguration configuration) throws Exception {
        try {
            this.configuration = configuration;
            this.executorService = Executors.newFixedThreadPool(1);
            this.connectionFactory = new ConnectionFactory();
            this.connectionFactory.setHost(configuration.getHost());
            this.conn = connectionFactory.newConnection(executorService);
            this.channel = conn.createChannel();
            channel.queueDeclare(configuration.getQueue(), true, false, false, null);
        } catch (Exception e) {

            if (this.channel != null) {
                this.channel.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
            if (this.executorService != null) {
                executorService.shutdown();
            }
            throw e;
        }
    }

    public Rabbitmq(RmqConfiguration configuration, int numberOfThread) throws IOException {
        this.configuration = configuration;

        this.executorService = Executors.newFixedThreadPool(numberOfThread);
        this.connectionFactory = new ConnectionFactory();
        this.conn = connectionFactory.newConnection(executorService);
        this.channel = conn.createChannel();
        channel.queueDeclare(configuration.getQueue(), true, false, false, null);
    }


    public Channel getChannel() {
        return channel;
    }

    public String setConsumeFunction(final QueueProcessor consumer, boolean autoAck, String consumerTag) throws Exception {
        return channel.basicConsume(this.configuration.getQueue(), autoAck, consumerTag,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        consumer.consumeMessage(body);
                    }

                });
    }

    public boolean isconnectionOpen() {
        return this.conn.isOpen();
    }

    public void shutOffQueue(String consumerTag) throws IOException {
        try {
            this.channel.basicCancel(consumerTag);
        } catch (Exception e) {

        }
        try {
            this.channel.close();
        } catch (Exception e) {

        }
        try {
            this.conn.close();
        } catch (Exception e) {

        }
        try {
            this.executorService.shutdown();
        } catch (Exception e) {

        }

    }

}
