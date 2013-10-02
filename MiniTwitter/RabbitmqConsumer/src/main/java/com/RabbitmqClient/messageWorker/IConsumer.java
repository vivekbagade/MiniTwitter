package com.RabbitmqClient.messageWorker;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 11/12/12
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IConsumer {
    public ConsumerQueue<String> getQueue();
}
