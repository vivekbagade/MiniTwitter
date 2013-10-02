//TODO:// package name in small case
package com.RabbitmqClient.RabbitMQConsumer;


import com.RabbitmqClient.messageCollecor.QueueProcessor;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: rohit.b
 * Date: 9/19/12
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class Application {
    private static final Logger LOG = Logger.getLogger(Application.class);
    private final ThreadManager[] managerList;// =   Services.getInstance().getServicesToRun();


    /*  public static void main(String[] args) {
            Application main = new Application();
      }*/
    public void start() {
        for(ThreadManager thread:managerList){
            thread.startThread();
        }
        while (true) {
            for (ThreadManager manager : managerList) {
                manager.replanishThreads();
            }
            printStats();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                LOG.error("thread interupted", e);
            }
        }
    }

    public Application(ThreadManager[] managerList, List<String> RabbitMqHostList) {
//        Signal.handle(new Signal("HUP"), new SignalHandler() {   // hup signal sent from init script, kill -9 cannot be cought
//
//            public void handle(Signal sig) {
//                //todo handle killing all threads
//
//          }
//        });
        this.managerList = managerList;
        QueueProcessor.setQUEUE_HOST_LIST(RabbitMqHostList);
    }

    public void printStats() {
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

        StringBuilder threadDump = new StringBuilder();

/*            for (Thread thread : allStackTraces.keySet()) {
                threadDump.append("---------------------------------------").append("\n");
                threadDump.append(thread.getName()).append("\n");
                for ( int i = 0; i < allStackTraces.get(thread).length; i++ ) {
                    threadDump.append( allStackTraces.get(thread)[i].toString() ).append("\n");
                }
            }
            LOG.info(threadDump + "---\n\n\n");*/

        LOG.error("Total Threads " + allStackTraces.keySet().size());
        LOG.error("Memory max: " + Runtime.getRuntime().maxMemory() + ", Memory Total:" + Runtime.getRuntime().totalMemory() + ", Memory Free:" + Runtime.getRuntime().freeMemory());
    }

}
