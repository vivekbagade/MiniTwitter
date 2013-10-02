package com.RabbitmqClient.RabbitMQConsumer;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 6/7/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutableThreadManager extends ThreadManager {
    protected ExecutorService executorService;


    public ExecutorService getExecutorService() {
        return executorService;
    }


    public ExecutableThreadManager(String name, int numberOfThreads, String runnableClass) {
        super(name, numberOfThreads, runnableClass);
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);


        this.futures = new Object[numberOfThreads];

        //startThread(runnableClass, numberOfThreads);
        LOG.info(futures);
    }

    protected void startThread() {
        String runnableClass=this.runnableClass;
        int numberOfThreads=this.numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                Object o=Class.forName(runnableClass).getConstructor(int.class).newInstance(i);
                LOG.error(o);
                futures[i] = executorService.submit((Runnable) o);
            } catch (InstantiationException e) {
                LOG.error("In ThreadManager error", e);
            } catch (IllegalAccessException e) {
                LOG.error("In ThreadManager error", e);
            } catch (InvocationTargetException e) {
                LOG.error("In ThreadManager error", e);

            } catch (NoSuchMethodException e) {
                LOG.error("In ThreadManager error", e);
            } catch (ClassNotFoundException e) {
                LOG.error("In ThreadManager error", e);
            }
            LOG.error("Started " + runnableClass + " " + i);
        }
    }


    public void replanishThreads() {
        for (int i = 0; i < numberOfThreads; i++) {
            if (((Future<Void>) this.futures[i]).isDone()) {
                try {
                    futures[i] = executorService.submit((Runnable) Class.forName(runnableClass).getConstructor(int.class).newInstance(i));
                    LOG.info("replanishing thread " + runnableClass + "-" + i);
                } catch (InstantiationException e) {
                    LOG.error("In ThreadManager error", e);
                } catch (IllegalAccessException e) {
                    LOG.error("In ThreadManager error", e);
                } catch (InvocationTargetException e) {
                    LOG.error("In ThreadManager error", e);
                } catch (NoSuchMethodException e) {
                    LOG.error("In ThreadManager error", e);
                } catch (ClassNotFoundException e) {
                    LOG.error("In ThreadManager error", e);
                }
                LOG.error("Started " + runnableClass + " " + i);
            }
        }
    }
}
