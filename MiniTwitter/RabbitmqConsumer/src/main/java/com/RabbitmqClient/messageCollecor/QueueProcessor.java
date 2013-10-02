package com.RabbitmqClient.messageCollecor;


import com.RabbitmqClient.RabbitMQConsumer.ManagableThread;
import com.RabbitmqClient.messageWorker.IConsumer;
import com.RabbitmqClient.rabbitmq.Rabbitmq;
import com.RabbitmqClient.rabbitmq.RmqConfiguration;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 1/9/13
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class QueueProcessor extends ManagableThread {

    private final String queueName;
    private final String consumerTag;
    public static List<String> QUEUE_HOST_LIST = new ArrayList<String>();
    private static final Logger LOG = Logger.getLogger(QueueProcessor.class);

    protected String host;

    protected abstract ConsumerQueueList getSubscriptionList();

    public void subscribe(IConsumer consumer) {
        getSubscriptionList().subscribe(consumer);

    }

    public static void setQUEUE_HOST_LIST(List<String> QUEUE_HOST_LIST) {
        QueueProcessor.QUEUE_HOST_LIST = QUEUE_HOST_LIST;
    }


    public QueueProcessor(int threadNum, String queueName, String consumerTag) {
        super(threadNum);
        this.queueName = queueName;
        this.consumerTag = consumerTag;
        this.host = ConnectionFactory.DEFAULT_HOST;
        //if (threadnum != 0) {
            this.host = getHostForThread();
        //}
    }


    public void run() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
        Rabbitmq rabbitmq = null;
        String consumerTag = null;

        try {
            if (!getSubscriptionList().isAnyQueueFull()) {
                RmqConfiguration rmqConfiguration = new RmqConfiguration(this.host, queueName, true, false, false, null);
                rabbitmq = new Rabbitmq(rmqConfiguration);
                boolean autoAck = true;
                consumerTag = rabbitmq.setConsumeFunction(this, autoAck, this.consumerTag);

                while (true) {
                    if (getSubscriptionList().isAnyQueueFull() || !rabbitmq.isconnectionOpen()) {
                        LOG.error("queue is full rabbitmq.isconnectionOpen() = " + rabbitmq.isconnectionOpen() + " " + getSubscriptionList().getQueuesStats());
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOG.error("Exception while rabbitmqProcessor sleeping", e);
                    }
                }
            } else {
                LOG.error("Killing " + this.consumerTag + "  " + threadnum + " , Queue size" + getSubscriptionList().getQueuesStats() + " because queue is already full");
            }
        } catch (IOException e) {
            LOG.error("Exception while creating connection to com.RabbitmqClient.rabbitmq " + queueName, e);
        } catch (Exception e) {
            LOG.error("Exception while setting consumer function com.RabbitmqClient.rabbitmq" + queueName, e);
        } finally {
            if (rabbitmq != null) {
                try {
                    LOG.error("Killing " + this.consumerTag + " " + threadnum + ", Queue size " + getSubscriptionList().getQueuesStats());
                    rabbitmq.shutOffQueue(consumerTag);
                    LOG.error("Shutdown successful");
                } catch (Exception e) {
                    LOG.error("Exception while shuttingoff queues " + this.consumerTag + " com.RabbitmqClient.rabbitmq : " + queueName, e);
                }
            }
        }

    }

    private String getHostForThread() {
        List<String> loggingServers = QUEUE_HOST_LIST;
        int hostIndex = 0;
        try {
            hostIndex = loggingServers.indexOf(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            hostIndex = -1;
            LOG.error("cannot get host name hence listening to localhost");
        }

        if (-1 == hostIndex) {
            LOG.error("not a logging server host name hence listening to localhost");
            return ConnectionFactory.DEFAULT_HOST;

        }
        try {
            LOG.error(InetAddress.getLocalHost().getHostName() + "thread -" + threadnum + " is listening to " + loggingServers.get((threadnum + hostIndex) % loggingServers.size()) + " for address " + InetAddress.getByName(loggingServers.get((threadnum + hostIndex) % loggingServers.size())));
        } catch (UnknownHostException e) {
            LOG.error("error getting IP address ", e);
        }
        return loggingServers.get((threadnum + hostIndex) % loggingServers.size());

    }


    public abstract void consumeMessage(byte[] body);


}
