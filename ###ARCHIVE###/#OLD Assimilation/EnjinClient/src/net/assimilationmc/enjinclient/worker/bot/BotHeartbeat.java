package net.assimilationmc.enjinclient.worker.bot;

import net.assimilationmc.enjinclient.module.Friends;
import net.assimilationmc.enjinclient.module.Messages;
import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Created by Ellie on 23/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class BotHeartbeat extends Thread {

    private BotWorker botWorker;
    private final String sessionId;

    private String lastToken = UUID.randomUUID().toString();

    public BotHeartbeat(BotWorker botWorker){
        this.botWorker = botWorker;
        this.sessionId = botWorker.connectionFactory.getUserData().getSessionID();
    }

    private int run = 0;

    @Override
    public void run() {

        for(;;){
            run++;
            try{

                if(divisible(20)){
                    checkRequests();
                    checkPrivateMessage();
                }








                sleep(1000);

            }catch(Exception e){
                System.out.println("Exception in bot heart beat!");
                e.printStackTrace();
            }
        }

    }

    private boolean divisible(int a){
        return run % a == 0;
    }

    private void checkRequests() throws Exception {
        String token = UUID.randomUUID().toString();
        botWorker.connectionFactory.send(Friends.getList(sessionId, "pending", 1, 1, token, lastToken, false));
        JSONObject jsonObject = botWorker.connectionFactory.readOutput();
        boolean changed = (Boolean) ((JSONObject)jsonObject.get("result")).get("differential_update");
        lastToken = token;
        if(changed){
            p("Detected friend request change, adding..");
            botWorker.acceptRequests();
        }
    }

    private void checkPrivateMessage() throws Exception {

        botWorker.connectionFactory.send(Messages.getList(botWorker.connectionFactory.getUserData().getSessionID(),
                1, "all", "", "", "", -1, true, -1, -1));
        //JSONObject jsonObject = botWorker.connectionFactory.readOutput().get("result");
        //System.out.println(jsonObject);



    }

    private void p(String message){
        System.out.println("HB: "+message);
    }


}
