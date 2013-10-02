package RabbitMQ.handlers;

import com.RabbitmqClient.messageCollecor.ConsumerQueueList;
import com.RabbitmqClient.messageCollecor.QueueProcessor;
import com.RabbitmqClient.messageWorker.ConsumerQueue;
import com.RabbitmqClient.utils.AppConfigQueue;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/21/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterQueueProcessor extends QueueProcessor{
    private static final AppConfigQueue appConfigQueue = AppConfigQueue.getInstance();
    private static ConsumerQueueList publisherQueueList = new ConsumerQueueList(new HashSet<ConsumerQueue<String>>());

    public TwitterQueueProcessor(int threadNum)
    {
        super(threadNum,appConfigQueue.getString("QueueName"),"TwitterQueueProcessor");
        System.out.println("Thread no "+threadNum);
    }

    @Override
    public void consumeMessage(byte[] body)
    {
        System.out.println("Consume method called");
        String message=new String((body));
        if(appConfigQueue.getString("SWITCH_ON_OFF").equals("on"))
        {
            try {
                publisherQueueList.publishMessage(message);
            } catch (Exception e) {
                System.out.println("Error with queues");
            }
        }
        else
            System.out.println("Message logging switched off");
    }

    @Override
    protected ConsumerQueueList getSubscriptionList()
    {
        return TwitterQueueProcessor.publisherQueueList;
    }
}
