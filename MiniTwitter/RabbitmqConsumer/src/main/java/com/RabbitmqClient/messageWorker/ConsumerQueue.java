package com.RabbitmqClient.messageWorker;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 11/12/12
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumerQueue<E> extends LinkedBlockingQueue<E> {
    private int maxLengthOFQueue;

    public ConsumerQueue(int capacity, String queueName) {
        //super(capacity);
        this.maxLengthOFQueue = capacity;
        this.queueName = queueName;
    }

    private String queueName;

    public boolean doWantToListen(String msg) throws Exception {
        return true;
    }

    public boolean doShouldSleep() {
        return true;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {

        return queueName;
    }

    public int getMaxQueueLength() {
        return maxLengthOFQueue;

    }

}
