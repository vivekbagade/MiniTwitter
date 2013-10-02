package RabbitMQ.handlers;

import com.RabbitmqClient.RabbitMQConsumer.ManagableThread;
import com.RabbitmqClient.messageWorker.ConsumerQueue;
import com.RabbitmqClient.messageWorker.IConsumer;
import com.RabbitmqClient.utils.AppConfigQueue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/21/13
 * Time: 12:49 PM
*/
public class CassandraConsumer extends ManagableThread implements IConsumer{
    private static final AppConfigQueue appConfigQueue = AppConfigQueue.getInstance();
    private static ConsumerQueue<String> queue=new ConsumerQueue<String>(appConfigQueue.getInt("MAX_LENGTH_MESSAGE_QUEUE"),"CassandraConsumerQueue"){
        @Override
        public boolean doWantToListen(String message)
        {
            try
            {
                JsonObject jsonMsg = new JsonParser().parse(message).getAsJsonObject();
                return true;
            }
            catch (Exception e)
            {
                System.out.println("Error in Json Parsing");
                return false;
            }
        }
    };

    private static CassandraMessageProcessor cassandraMessageProcessor=new CassandraMessageProcessor();

    public CassandraConsumer(int threadNum)
    {
        super(threadNum);
        TwitterQueueProcessor tw=new TwitterQueueProcessor((int) (Math.random()*6));
        tw.subscribe(this);
        System.out.println(tw);
    }

    public ConsumerQueue<String> getQueue()
    {
        return queue;
    }

    public void run()
    {

        String str;
        while(true)
        {
            try
            {
                str=queue.take();
                System.out.println(str);
                System.out.println(cassandraMessageProcessor);
                cassandraMessageProcessor.processMessage(str);
                System.out.println("end");
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
        }
    }
}
