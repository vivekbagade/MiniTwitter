package com.RabbitmqClient.messageCollecor;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 1/9/13
 * Time: 6:31 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IQueueConsumer {
    public void consumeMessage(byte[] body);
}
