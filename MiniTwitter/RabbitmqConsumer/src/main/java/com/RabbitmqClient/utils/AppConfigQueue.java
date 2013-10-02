package com.RabbitmqClient.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.util.List;

public class AppConfigQueue {
    private static final Logger LOG = Logger.getLogger(AppConfigQueue.class);
    private static AppConfigQueue appConfigQueue = new AppConfigQueue();

    private Configuration config;

    private AppConfigQueue() {
        try {
            config = new PropertiesConfiguration("application.properties");
            //TODO: do not catch here
        } catch (ConfigurationException e) {
            LOG.error("Error loading properties file. " + e);
        }
    }

    public static AppConfigQueue getInstance() {
        return appConfigQueue;
    }

    public String getString(String field) {
        return config.getString(field);
    }

    public int getInt(String field) {
        return config.getInt(field);
    }

    public boolean getBoolean(String field) {
        return config.getBoolean(field);
    }

    public List<String> getStringList(String list) {
        return config.getList(list);
    }
}
