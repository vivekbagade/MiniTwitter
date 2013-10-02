package com.RabbitmqClient.RabbitMQConsumer;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 12/12/12
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ThreadManager {
    protected String name;
    protected Object[] futures;
    protected static final Logger LOG = Logger.getLogger(ThreadManager.class);
    protected String runnableClass;
    protected int numberOfThreads;

    public ThreadManager(String name, int numberOfThreads, String runnableClass) {
        this.name = name;
        this.numberOfThreads = numberOfThreads;
        this.runnableClass = runnableClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Object[] getFutures() {
        return futures;
    }

    protected abstract void startThread();

    public abstract void replanishThreads();
}
