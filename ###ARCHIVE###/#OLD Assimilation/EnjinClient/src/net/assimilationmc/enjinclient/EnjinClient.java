package net.assimilationmc.enjinclient;

import net.assimilationmc.enjinclient.worker.bot.BotWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinClient {

    public static boolean debug = true;
    public static BotWorker botWorker;
    static boolean loggedIn = false;

    private EnjinClient() {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    try {

        System.out.println("Welcome to the EnjinClient v2.0");
        System.out.println("Type 'help' for a list of commands\n ");

        while (true) {

            String input = bufferedReader.readLine();
            if(input != null) {
                RequestHandler.handle(input);
            }

        }

    }catch(IOException e){
        e.printStackTrace();
        System.out.println("Error: Failed to handle request");
    }finally {
            try{
                bufferedReader.close();
            }catch(IOException e){
                e.printStackTrace();
                System.out.println("Failed to close reader");
            }

    }



     //   try {

           // connectionFactory.send(Wall.postMessage(connectionFactory.getUserData().getSessionID(), 7005362,
                    //"link", "Wow check out this link!", "friends", "www.youtube.com", "UTubueroni", "A website for the biggest gamers!", "", 20, 10, "Title", "Description"));
           // System.out.println(connectionFactory.readOutput());

           //

            //connectionFactory.send(Friends.getList(connectionFactory.getUserData().getSessionID(), "all", -1, -1, false));
            //JSONObject result = connectionFactory.readOutput();

            //List<UserData> a = FriendWorker.getFriendsOfflineSince(result, 20);
            //FriendWorker.removeFriendsAsync(connectionFactory, a);




       //     connectionFactory.closeConnection();
     //   }catch(Exception e){
    //        e.printStackTrace();
    //        System.out.println("Connection is invalid!");
   //     }
    }

    public static void main(String[] args){
        new EnjinClient();
    }



}
