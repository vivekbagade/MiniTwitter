package com.RabbitmqClient.messageCollecor;

import com.RabbitmqClient.messageWorker.ConsumerQueue;
import com.RabbitmqClient.messageWorker.IConsumer;
import org.apache.log4j.Logger;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 1/9/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumerQueueList {
    private Logger LOG = Logger.getLogger(ConsumerQueueList.class);
    private HashSet<ConsumerQueue<String>> publisherQueueList = new HashSet<ConsumerQueue<String>>();

    public ConsumerQueueList(HashSet<ConsumerQueue<String>> publisherQueueList) {
        this.publisherQueueList = publisherQueueList;
    }

    private HashSet<ConsumerQueue<String>> getPublisherQueueList() {
        return publisherQueueList;
    }

    public void setPublisherQueueList(HashSet<ConsumerQueue<String>> publisherQueueList) {
        this.publisherQueueList = publisherQueueList;
    }

    public void subscribe(IConsumer consumer) {
        this.publisherQueueList.add(consumer.getQueue());
    }

    public boolean isEmpty() {
        return this.publisherQueueList.isEmpty();
    }

    public String getQueuesStats() {
        String queueStats = "";
        for (ConsumerQueue<String> queue : getPublisherQueueList()) {
            queueStats += "###" + queue.getQueueName() + "-->" + queue.size();
        }
        return queueStats;
    }


    public void publishMessage(String message) throws Exception {
        for (ConsumerQueue<String> queue : getPublisherQueueList()) {
            if (queue.doWantToListen(message))
                queue.put(message);

        }
    }

    public boolean isAnyQueueFull() {
        boolean die = false;
        for (ConsumerQueue<String> queue : getPublisherQueueList()) {
            if (queue.size() > queue.getMaxQueueLength()) {
                die = true;
                break;
            }
        }
        return die;
    }

}
