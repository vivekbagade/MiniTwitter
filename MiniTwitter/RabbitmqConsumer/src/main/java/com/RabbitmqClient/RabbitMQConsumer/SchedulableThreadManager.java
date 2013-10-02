package com.RabbitmqClient.RabbitMQConsumer;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 6/7/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchedulableThreadManager extends ThreadManager {
    private ScheduledExecutorService schedulerService;


    public SchedulableThreadManager(String name, int numberOfThreads, String runnableClass) {
        super(name, numberOfThreads, runnableClass);
        this.schedulerService = Executors.newScheduledThreadPool(numberOfThreads);


        this.futures = new Object[numberOfThreads];

        //startThread(runnableClass, numberOfThreads);
        LOG.info(futures);
    }

    protected void startThread() {
        String runnableClass=this.runnableClass;
        int numberOfThreads=this.numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                futures[i] = schedulerService.scheduleAtFixedRate((Runnable) Class.forName(runnableClass).getConstructor(int.class).newInstance(i), getNextStartTimeDelay(), 3600000, TimeUnit.MILLISECONDS);
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

    }

    private long getNextStartTimeDelay() {
        Calendar now = Calendar.getInstance();
        Calendar wakeTime = Calendar.getInstance();
        wakeTime.add(Calendar.HOUR, 1);
        wakeTime.set(Calendar.MINUTE, 0);
        wakeTime.set(Calendar.SECOND, 0);
        wakeTime.set(Calendar.MILLISECOND, 0);
        LOG.info("sleeping for " + (wakeTime.getTime().getTime() - now.getTime().getTime()) + "next wake up on " + wakeTime.getTime());
        return wakeTime.getTime().getTime() - now.getTime().getTime();
    }
}
