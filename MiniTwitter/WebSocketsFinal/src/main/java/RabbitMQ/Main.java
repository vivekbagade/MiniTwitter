package RabbitMQ;

import RabbitMQ.handlers.CassandraConsumer;
import RabbitMQ.handlers.PostgresConsumer;
import RabbitMQ.handlers.TwitterQueueProcessor;
import com.RabbitmqClient.RabbitMQConsumer.Application;
import com.RabbitmqClient.RabbitMQConsumer.ExecutableThreadManager;
import com.RabbitmqClient.RabbitMQConsumer.ThreadManager;
import com.RabbitmqClient.utils.AppConfigQueue;

public class Main
{
    private static final AppConfigQueue appConfigQueue = AppConfigQueue.getInstance();
    private final int numMsgConsumer = appConfigQueue.getInt("numMsgConsumer");
    private final int numQueueConsumer = appConfigQueue.getInt("numQueueConsumer");

    public Main()
    {
        Application application=new Application(new ThreadManager[]{
                new ExecutableThreadManager("PostgresConsumer",numMsgConsumer, PostgresConsumer.class.getName()),
                new ExecutableThreadManager("CassandraConsumer",numMsgConsumer, CassandraConsumer.class.getName()),
                new ExecutableThreadManager("TwitterQueueProcessor",numQueueConsumer, TwitterQueueProcessor.class.getName())
        },appConfigQueue.getStringList("QUEUE_HOST_LIST"));
        application.start();
    }

    public static void main(String args[])
    {
        Main main=new Main();
    }
}
