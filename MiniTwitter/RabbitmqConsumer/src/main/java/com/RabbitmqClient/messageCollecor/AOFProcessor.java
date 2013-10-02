package com.RabbitmqClient.messageCollecor;


import com.RabbitmqClient.RabbitMQConsumer.ManagableThread;
import com.RabbitmqClient.messageWorker.IConsumer;
import com.RabbitmqClient.utils.MailSender;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 12/18/12
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AOFProcessor extends ManagableThread {
    private static final Logger LOG = Logger.getLogger(AOFProcessor.class);
    private final SimpleDateFormat keyFormatHr = new SimpleDateFormat("yyyyMMddHH");
    private final SimpleDateFormat keyFormatMin = new SimpleDateFormat("yyyyMMddHHmm");
    private final SimpleDateFormat keyFormatDate = new SimpleDateFormat("yyyyMMdd");
    private int count = 0;
    private String AOFFileBaseName = null;

    public void setAOFFileBaseName(String AOFFileBaseName) {
        this.AOFFileBaseName = AOFFileBaseName;
    }

    protected abstract ConsumerQueueList getSubscriptionList();

    public AOFProcessor(int threadNum, String fileBaseName) {
        super(threadNum);
        this.AOFFileBaseName = fileBaseName;

    }


    public void subscribe(IConsumer consumer) {
        getSubscriptionList().subscribe(consumer);

    }

    public void run() {

        Date now = new Date();
      /*  for (int i = 0; i < now.getHours(); i++) {
            String fileNameModifier =  keyFormatDate.format(now) + String.format("%02d", i);
            String filePath = this.AOFFileBaseName + fileNameModifier + ".txt";
            LOG.info("importing from file : " + filePath);
            if (publishMessageFromFile(filePath, now)) continue;
        }*/

        now = new Date();
        LOG.info("in while loop");
        String fileNameModifier = keyFormatHr.format(now);
        String filePath = this.AOFFileBaseName + fileNameModifier + ".txt";
        LOG.info("importing from file : " + filePath);
        if (!publishMessageFromFile(filePath, now)) {
            LOG.error("FAILURE PROCESSING aof " + filePath + "  " + count + "   imported at " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date()));
            new MailSender("FAILURE PROCESSING aof", "number of msg processed by AOF " + filePath + "  " + count + "   imported at " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date())).run();
        }


    }

    private boolean publishMessageFromFile(String filePath, Date timeOfFile) {
        String message = "[]";
        BufferedReader br = null;
        try {
            count = 0;
            br = new BufferedReader(new FileReader(filePath));
            while ((message = br.readLine()) != null) {
                count++;
                LOG.info(message);
                message = updateTimestamp(timeOfFile, message);
                LOG.info(message);
                publishMessage(message);
            }
            if (count != 0) {
                LOG.error("number of msg processed by AOF " + filePath + "  " + count + "   imported at " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date()));
                new MailSender("AOF File imported", "number of msg processed by AOF " + filePath + "  " + count + "   imported at " + new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date())).run();
            }

        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException e) {
            LOG.error("error reading file :" + filePath, e);
        } finally {
            try {
                if (br != null) br.close();
                // File thisFile = new File(filePath);
                //thisFile.delete();
            } catch (IOException ex) {
                LOG.error("error closing file :" + filePath, ex);
            }
        }
        return false;
    }

    private String updateTimestamp(Date timeOfFile, String message) {
        try {
            JsonObject root = new JsonParser().parse(message).getAsJsonObject();
            timeOfFile.setMinutes(59);
            root.remove("minutewise_timestamp");
            root.remove("hourwise_timestamp");
            root.addProperty("minutewise_timestamp", keyFormatMin.format(timeOfFile));
            root.addProperty("hourwise_timestamp", keyFormatHr.format(timeOfFile));
            message = root.toString();
        } catch (Exception e) {

        }
        return message;
    }

    private void publishMessage(String message) {
        try {
            getSubscriptionList().publishMessage(message);
        } catch (Exception e) {
            LOG.error("error putting AOF message to queue : ", e);
        }

    }

    private void waitForNextFile() {
        Calendar now = Calendar.getInstance();
        Calendar wakeTime = Calendar.getInstance();
        wakeTime.add(Calendar.HOUR, 1);
        wakeTime.set(Calendar.MINUTE, 0);
        try {
            LOG.info("sleeping for " + (wakeTime.getTime().getTime() - now.getTime().getTime()));
            Thread.sleep(wakeTime.getTime().getTime() - now.getTime().getTime());
        } catch (InterruptedException e) {
            LOG.error("error sleeping AOFProcessor {}", e);
        }
    }
}
