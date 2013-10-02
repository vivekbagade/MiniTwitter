package RabbitMQ.handlers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.util.HtmlUtils;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/21/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MessageProcessor {
    public boolean processMessage(String message)
    {

        JSONObject jsonObject;
        try
        {
            jsonObject=new JSONObject(message);
            if((Integer)jsonObject.get("type")==1)
                processAddTweetMessage(jsonObject);
            else if((Integer)jsonObject.get("type")==2)
                processRegisterMessage(jsonObject);
            else if((Integer)jsonObject.get("type")==3)
                processFollowMessage(jsonObject);
            else if((Integer)jsonObject.get("type")==4)
                processUnFollowMessage(jsonObject);
            else
                processModifyUserMessage(jsonObject);
        }
        catch (JSONException e) {return false;}
        return true;
    }

    protected abstract void processModifyUserMessage(JSONObject jsonObject);

    protected abstract void processUnFollowMessage(JSONObject jsonObject);

    protected abstract void processFollowMessage(JSONObject jsonObject);

    protected abstract void processRegisterMessage(JSONObject jsonObject);

    protected abstract void processAddTweetMessage(JSONObject jsonObject);

    public String sanitizeString(String message)
    {
        return HtmlUtils.htmlEscape(message);
    }
}
