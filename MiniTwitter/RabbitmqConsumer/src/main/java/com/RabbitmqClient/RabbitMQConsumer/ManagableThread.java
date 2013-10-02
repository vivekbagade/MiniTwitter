package com.RabbitmqClient.RabbitMQConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 12/21/12
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ManagableThread implements Runnable {
    protected int threadnum;

    public ManagableThread(int threadnum) {
        this.threadnum = threadnum;
    }
}
