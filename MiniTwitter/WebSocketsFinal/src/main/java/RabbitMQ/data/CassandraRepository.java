package RabbitMQ.data;

import RabbitMQ.model.Tweet;
import RabbitMQ.model.User;
import WebSocket.TweetSocketServlet;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import org.apache.catalina.websocket.WebSocketServlet;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/19/13
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class CassandraRepository {

    private static Keyspace keyspace;
    private static CassandraHelper helper;

    public CassandraRepository(Keyspace keyspace) {
        this.keyspace=keyspace;
        this.helper=new CassandraHelper(keyspace);
    }

    public void addTweet(String email,String tweet,String timestamp) throws IOException
    {

        while (!execAddTweet(email,tweet,timestamp)) ;

        String[] timestamp_pieces = timestamp.split(":");
        String timestamp_WithoutSeconds = timestamp_pieces[0] + ":" + timestamp_pieces[1];
        System.out.println("Timestamp without seconds" + timestamp_WithoutSeconds);
        addWordsToCounterColumn(tweet,timestamp_WithoutSeconds);
        System.out.println("Tweet words successfully inserted in the Casandra database\n");
    }

    public boolean execAddTweet(String email,String tweet,String timestamp)
    {
        try {
            System.out.println("adding tweet of "+email+" "+tweet);
            String tweetID=helper.getMD5(timestamp + tweet);
            helper.addInOwn(email, tweet, timestamp, tweetID);
            helper.addInFollowers(email, tweetID, timestamp, tweet);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public void addWordsToCounterColumn(String Tweet,String Timestampwithoutseconds) {

        String wordDelimiters = "[,.!?# ]+";
        boolean wordinserted=false;
        for(String word:Tweet.split(wordDelimiters)) {
            System.out.println("Word:"+ word);
            if(word.length()>1 && !(isStopWord(word))) {
                wordinserted=false;
                System.out.println("Word that passed checks" + word);
                while(!wordinserted) {
                try {

                helper.insertCounterColumnForWord(word,Timestampwithoutseconds);  //inserts or increments whatever the case may be.
                //helper.CounterColumnValueForWord("will");
                wordinserted=true;
                }
                catch (Exception e) {
                 wordinserted=false;
                }
            }
          }

            System.out.println("Proceeding to the next word\n");

        }

    }

    public boolean isStopWord(String word) {

        System.out.println("Checking whether" + word + "is a stop word\n");
        String[]stopWords = new String[]{"as","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","ain't","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren’t","around","as","aside","ask","asking","associated","at","available","away","awfully","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c’mon","c’s","came","can","can’t","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn’t","course","currently","definitely","described","despite","did","didn’t","different","do","does","doesn’t","doing","don’t","done","down","downwards","during","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","had","hadn’t","happens","hardly","has","hasn’t","have","haven’t","having","he","he’s","hello","help","hence","her","here","here’s","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i’d","i’ll","i’m","i’ve","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isn’t","it","it’d","it’ll","it’s","its","itself","just","keep","keeps","kept","know","knows","known","last","lately","later","latter","latterly","least","less","lest","let","let’s","like","liked","likely","little","look","looking","looks","ltd","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","que","quite","qv","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","shouldn’t","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t’s","take","taken","tell","tends","th","than","thank","thanks","thanx","that","that’s","thats","the","their","theirs","them","themselves","then","thence","there","there’s","thereafter","thereby","therefore","therein","theres","thereupon","these","they","they’d","they’ll","they’re","they’ve","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","value","various","very","via","viz","vs","want","wants","was","wasn’t","way","we","we’d","we’ll","we’re","we’ve","welcome","well","went","were","weren’t","what","what’s","whatever","when","whence","whenever","where","where’s","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","who’s","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","won’t","wonder","would","would","wouldn’t","yes","yet","you","you’d","you’ll","you’re","you’ve","your","yours","yourself","yourselves","zero"};
        for (int i=0;i<stopWords.length;i++) {
            if(word.compareToIgnoreCase(stopWords[i])==0){
                return true;
            }
        }
        return false;
    }


    public void registerUser(String email,String name,String password,boolean isModifyRequest)
    {
       while(!execRegisteruser(email,name,password,isModifyRequest));
    }

    public boolean execRegisteruser(String email,String name,String password,boolean isModifyRequest) {
        try {
        if(helper.isEmailTaken(email) && !isModifyRequest)
        {
            //do Something
            System.out.println("email taken");
            return true;
        }
        Mutator<String> mutator=HFactory.createMutator(keyspace,StringSerializer.get());
        Mutator<String> mutator2=HFactory.createMutator(keyspace,StringSerializer.get());
        if(isModifyRequest)
        {
            mutator.addDeletion(email,"users","password",StringSerializer.get());
            mutator.addDeletion(email,"users","name",StringSerializer.get());
            mutator.execute();
        }
        HColumn<String,String> column1=HFactory.createColumn("name",name);
        HColumn<String,String> column2=HFactory.createColumn("password",password);
        HColumn<String,String> column3=HFactory.createColumn(email,"");
        mutator.addInsertion(email,"users",column1);
        mutator.addInsertion(email,"users",column2);
        mutator2.addInsertion("users","search",column3);
        mutator.execute();
        mutator2.execute();
        }
        catch (Exception e) {
            return false;
        }
        return true;

    }



    public void followUser(String user,String subscription)
    {
         while(!execfollowuser(user,subscription));
    }

    public boolean execfollowuser(String user,String subscription) {
        try {
        if (helper.checkrev(user, subscription)) {
            helper.makeLink(subscription, user, 0);
            helper.makeLink(user, subscription, 0);
        } else {
            helper.makeLink(user, subscription, 1);
            helper.makeLink(subscription, user, 2);
        }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public void unfollowUser(String user,String subscription)
    {
        while(!execunfollowuser(user,subscription));
    }

    public boolean execunfollowuser(String user,String subscription) {
        try {
        if (helper.checkIfTwoWayConnected(user, subscription)) {
            helper.makeLink(subscription, user, 1);
            helper.makeLink(user, subscription, 2);
        } else if(helper.checkIfOneConnected(user, subscription)){
            helper.makeLink(user, subscription, -1);
            helper.makeLink(subscription, user, -1);
        }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<User> getFollowers(String email,String lastEmail)
    {
        List<User> ret = new LinkedList<User>();
        SliceQuery sliceQuery1 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery1.setColumnFamily("edges");

        sliceQuery1.setKey(email).setRange(helper.getCompositeLS(2L, lastEmail), helper.getCompositeLS(2L, "zzzzzzzz"), false, 10);
        QueryResult<ColumnSlice<Composite, String>> result1 = sliceQuery1.execute();
        List<HColumn<Composite, String>> list = result1.get().getColumns();


        SliceQuery sliceQuery2 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        if(result1.get().getColumns().size()<10)
        {
            sliceQuery2.setColumnFamily("edges");
            sliceQuery2.setKey(email).setRange(helper.getCompositeLS(0L, lastEmail), helper.getCompositeLS(0L, "zzzzzzzz"), false, 1000000);
            QueryResult<ColumnSlice<Composite, String>> result2 = sliceQuery2.execute();
            list.addAll(result2.get().getColumns());
        }

        int i = 0;
        for (HColumn<Composite, String> value : list) {
            User user=new User();
            user.setEmail(list.get(i).getValue());
            user.setName(helper.getUsername(email));
            ret.add(user);
            i++;
        }
        return ret;
    }

    public List<User> getSubscriptions(String email,String lastEmail)
    {
        List<User> ret = new LinkedList<User>();
        SliceQuery sliceQuery1 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        sliceQuery1.setColumnFamily("edges");

        sliceQuery1.setKey(email).setRange(helper.getCompositeLS(1L, lastEmail), helper.getCompositeLS(1L, "zzzzzzzz"), true, 10);
        QueryResult<ColumnSlice<Composite, String>> result1 = sliceQuery1.execute();
        List<HColumn<Composite, String>> list = result1.get().getColumns();


        SliceQuery sliceQuery2 = HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        if(result1.get().getColumns().size()<10)
        {
            sliceQuery2.setColumnFamily("edges");
            sliceQuery2.setKey(email).setRange(helper.getCompositeLS(0L, lastEmail), helper.getCompositeLS(0L, "zzzzzzzz"), true, 1000000);
            QueryResult<ColumnSlice<Composite, String>> result2 = sliceQuery2.execute();
            list.addAll(result2.get().getColumns());
        }

        int i = 0;
        for (HColumn<Composite, String> value : list) {
            User user=new User();
            user.setEmail(list.get(i).getValue());
            user.setName(helper.getUsername(email));
            ret.add(user);
            i++;
        }
        return ret;
    }

    public List<Tweet> getUserTweets(String email,String lastTimestamp)
    {
        List<Tweet> ret=new LinkedList<Tweet>();
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), StringSerializer.get());
        Composite start=helper.getCompositeSS(lastTimestamp,"");
        Composite end=helper.getCompositeSS("zzzzzzzzz", "zzzzzzzzzz");

        sliceQuery.setColumnFamily("tweets").setKey(email).setRange(start,end,true,10);
        QueryResult<ColumnSlice<Composite, String>> result = sliceQuery.execute();
        List<HColumn<Composite,String>> list=result.get().getColumns();

        for(HColumn<Composite,String> tweetinfo : list)
        {
            Tweet tweet=new Tweet();
            tweet.setTweetid(tweetinfo.getName().getComponent(1).getValue(StringSerializer.get()).toString());
            tweet.setContent(tweetinfo.getValue());
            tweet.setEmail(email);
            tweet.setTimestamp(tweetinfo.getName().getComponent(0).getValue(StringSerializer.get()).toString());
            ret.add(tweet);
        }
        return ret;
    }

    public List<Tweet> getFeed(String email,String lastTimestamp)
    {
        List<Tweet> ret=new LinkedList<Tweet>();
        SliceQuery sliceQuery=HFactory.createSliceQuery(keyspace, StringSerializer.get(), CompositeSerializer.get(), CompositeSerializer.get());
        Composite start=helper.getCompositeSS(lastTimestamp, "");
        Composite end=helper.getCompositeSS("zzzzzzzzz", "zzzzzzzzzz");

        sliceQuery.setColumnFamily("tweetsforuser").setKey(email).setRange(start, end, true, 10);
        QueryResult<ColumnSlice<Composite,Composite>> result=sliceQuery.execute();
        List<HColumn<Composite,Composite>> list=result.get().getColumns();

        for(HColumn<Composite,Composite> tweetinfo :list)
        {
            Tweet tweet=new Tweet();
            tweet.setTweetid(tweetinfo.getName().getComponent(1).getValue(StringSerializer.get()).toString());
            tweet.setTimestamp(tweetinfo.getName().getComponent(0).getValue(StringSerializer.get()).toString());
            tweet.setEmail(tweetinfo.getValue().getComponent(0).getValue(StringSerializer.get()).toString());
            tweet.setContent(helper.getTweetContent(tweet.getEmail(),tweet.getTweetid()));
            ret.add(tweet);
        }

        return ret;
    }
}
