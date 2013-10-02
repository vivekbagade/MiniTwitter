package RabbitMQ.model;

public class Tweet {
    private String tweetid;
    private String email;
    private String timestamp;
    private String content;

    public String getTweetid() {
        return tweetid;
    }

    public void setTweetid(String tweetid) {
        this.tweetid = tweetid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
