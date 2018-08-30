package net.assimilationmc.enjinclient.worker.bot;

import net.assimilationmc.enjinclient.connection.ConnectionFactory;
import net.assimilationmc.enjinclient.module.Friends;
import net.assimilationmc.enjinclient.module.Profile;
import net.assimilationmc.enjinclient.module.Site;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Ellie on 22/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BotWorker {

    ConnectionFactory connectionFactory;
    private BotState botState;
    private int siteId;
    private int currentProfile;
    private int limit;
    private int added;
    private BotHeartbeat botHeartbeat;

    private long started;

    private int goodVibesSent;

    public BotWorker(ConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
        botState = BotState.INIT;
        System.out.println("Setting up BOT mode.");
        botState = BotState.IDLE;
        this.botHeartbeat = new BotHeartbeat(this);
        botHeartbeat.start();
    }

    public void acceptRequests(){

        started = System.currentTimeMillis();
        botState = BotState.ADDING;
        try {
            connectionFactory.send(Friends.acceptAllRequests(connectionFactory.getUserData().getSessionID()));
            connectionFactory.readOutput();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Failed to accept friends");
        }
        done();
    }


    public void beginFriendRampage(int siteId, int limit){
        this.siteId = siteId;
        this.limit = limit;

        try {
            String session = connectionFactory.getUserData().getSessionID();

            started = System.currentTimeMillis();
            botState = BotState.ADDING;
            connectionFactory.send(Site.getStats(connectionFactory.getUserData().getSessionID(), siteId));
            JSONObject object = connectionFactory.readOutput();
            this.currentProfile = Integer.parseInt((String)((JSONObject)((JSONObject)object.get("result")).get("latest_user")).get("user_id"));
            Thread.sleep(1000);
            System.out.println("Adding user ID:" + currentProfile);
            connectionFactory.send(Friends.sendRequest(session, currentProfile));
            connectionFactory.readOutput();

            connectionFactory.send(Profile.getProfile(session, currentProfile));
            System.out.println(connectionFactory.readOutput());

            for (added = 1; added < limit; added++) {



                Thread.sleep(1000);
            }

            System.out.println("Added "+added+" friends.");

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
            System.out.println("Failed to get site info of "+siteId);
        }

        done();
    }

    private void done(){
        botState = BotState.IDLE;
        System.out.println("Task finished in "+ (System.currentTimeMillis() - started)+"ms");
    }

    public boolean isBusy(){
        return botState != BotState.IDLE;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public int getSiteID() {
        return siteId;
    }

    private void setCurrentProfile(int currentProfile) {
        this.currentProfile = currentProfile;
    }

    public int getCurrentProfile() {
        return currentProfile;
    }

    public int getAdded() {
        return added;
    }

    private void incrementAdded(){
        this.added = added + 1;
    }

    public boolean limitReached(){
        return this.added == limit;
    }

    public String generateFeelsGoodMessage(){
        int random = new Random().nextInt(9);
        List<String> messages = Arrays.asList(
                "Hey {name}! You're looking beautiful today!",
                "Wow {name}, you're a cool person",
                "{name} will you marry me? I know I'm just a bot but everyone deserves a happy life.",
                "{name} you're almost as hot as me, I mean, great personality +rep",
                "{name} Remember to stay mighty!",
                "Inhale Love, Exhale Hatred <3",
                "Never compare to yourself to anyone else because you're a great person <3",
                "{name}, you make my morning ;)",
                "{name} I got a poem off Googl-, I mean I wrote a poem for you. The sun is up, the sky is blue, today is beutiful and so are you <333",
                "Your life is a message to the world so make sure it's inspiring! <3");
        goodVibesSent = goodVibesSent + 1;
        return messages.get(random);
    }

    public BotHeartbeat getBotHeartbeat() {
        return botHeartbeat;
    }
}

