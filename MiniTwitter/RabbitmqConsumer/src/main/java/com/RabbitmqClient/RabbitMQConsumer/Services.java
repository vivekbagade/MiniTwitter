package com.RabbitmqClient.RabbitMQConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 12/21/12
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Services {
    private static Services services = new Services();

    private ThreadManager[] serviceList;

    public void setServiceList(ThreadManager[] serviceList) {
        this.serviceList = serviceList;
    }

    public static Services getInstance() {
        return services;
    }


    public ThreadManager[] getServicesToRun() {
        return serviceList;
    }


}
