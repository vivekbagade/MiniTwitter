package com.RabbitmqClient.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 1/6/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class RmqConfiguration {
    private String host;
    private String queue;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;

    public RmqConfiguration(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        this(ConnectionFactory.DEFAULT_HOST, queue, durable, exclusive, autoDelete, arguments);
    }

    public RmqConfiguration(String host, String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) {
        this.host = host;
        this.queue = queue;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getQueue() {
        return queue;
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
