package com.RabbitmqClient.RabbitMQConsumer;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: niteeshk
 * Date: 13/12/12
 * Time: 7:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpiredMsgStats {

    private final SimpleDateFormat keyFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private final int msgWindow = 100000;
    private final int timeWindow = 2 * 60 * 1000;
    private static final Logger LOG = Logger.getLogger(ExpiredMsgStats.class);

    private int expiredMessageCount = 0;
    private long maxTimeInCurrentWindow = 0;
    private int msgCount = 0;

    public synchronized void updateStats(JsonObject object) {
        if (++msgCount > msgWindow) {
            PrintAndReset();
        }
        try {
            String timestamp = object.get("minutewise_timestamp").getAsString();
            Date now = new Date();
            Date msgTimeStamp = keyFormat.parse(timestamp);
            long timeDiff = (now.getTime() - msgTimeStamp.getTime());
            maxTimeInCurrentWindow = Math.max(timeDiff, maxTimeInCurrentWindow);
            if (timeDiff > timeWindow) {
                expiredMessageCount++;
            }
        } catch (ParseException e) {
            LOG.error("", e);
            return;
        } catch (Exception e) {
            LOG.error("", e);
            return;
        }

    }

    private void PrintAndReset() {
        LOG.error("expired Message Count current Window =" + expiredMessageCount +
                "\n max Time In Current Window =" + maxTimeInCurrentWindow);
        expiredMessageCount = 0;
        maxTimeInCurrentWindow = 0;
        msgCount = 0;
    }


}
