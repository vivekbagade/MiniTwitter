package WebSocket;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 8/17/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */

public class TweetSocketServlet extends WebSocketServlet {
    Map<String,TweetMessageInbound> sockets=new HashMap<String,TweetMessageInbound>();
    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest request)
    {
        System.out.println("Called");
        byte[] authbytes= DatatypeConverter.parseBase64Binary(request.getParameter("email"));
        String email="";
        try {
            email=new String(authbytes,"UTF-8");
            if(sockets.containsKey(email))
                return sockets.get(email);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Problem with Email Decoding");
        }
        TweetMessageInbound tweetMessageInbound=new TweetMessageInbound();
        sockets.put(email,tweetMessageInbound);
        tweetMessageInbound.owner=email;
        System.out.println("email "+email +" locked");
        return tweetMessageInbound;
    }

    private class TweetMessageInbound extends MessageInbound
    {
        WsOutbound sender;
        public String owner;
        @Override
        protected void onOpen(WsOutbound outbound) {
            sender=outbound;
        }

        @Override
        protected void onClose(int status) {
            sockets.remove(owner);
            System.out.println("Closed a web Server");
        }

        @Override
        protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
            System.out.println(byteBuffer.toString());
        }

        @Override
        protected void onTextMessage(CharBuffer charBuffer) throws IOException {
            System.out.println(charBuffer.toString());
        }

        public void sendToClient(String message,String email,String tweeterEmail) throws IOException {
            sender.writeTextMessage(CharBuffer.wrap(email+":"+message+":"+tweeterEmail));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getParameter("email")+" "+req.getParameter("content"));
        if(sockets.containsKey(req.getParameter("email")))
            System.out.println("Contains key");
        sockets.get(req.getParameter("email")).sendToClient(req.getParameter("content"),req.getParameter("email"),req.getParameter("tweeterEmail"));
    }
}
