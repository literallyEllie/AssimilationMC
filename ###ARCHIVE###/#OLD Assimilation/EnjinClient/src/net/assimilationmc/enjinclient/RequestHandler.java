package net.assimilationmc.enjinclient;

import net.assimilationmc.enjinclient.connection.ConnectionFactory;
import net.assimilationmc.enjinclient.connection.UserData;
import net.assimilationmc.enjinclient.module.User;
import net.assimilationmc.enjinclient.module.Wall;
import net.assimilationmc.enjinclient.worker.bot.BotWorker;
import net.assimilationmc.enjinclient.worker.FriendWorker;
import net.assimilationmc.enjinclient.worker.WallWorker;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ellie on 01/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class RequestHandler {

    private static ConnectionFactory connectionFactory;

    public static boolean waitingOnAuthCode;

    static void handle(String input){

        input = input.toLowerCase();

        String[] args = input.split(" ");

        if(args.length == 1){
            if(waitingOnAuthCode){

                try{
                    Integer.parseInt(args[0]);
                }catch(NumberFormatException e){
                    System.out.println("Invalid length. Please enter Authy code.");
                    return;
                }
                if(args[0].length() != 7){
                    System.out.println("Invalid length. Please enter Authy code.");
                    return;
                }

                try {
                    connectionFactory.send(User.authyVerify(args[0], connectionFactory.getUserData().getPartialLogin()));
                    connectionFactory.readOutput();
                    waitingOnAuthCode = false;
                    System.out.println("Logged in as "+connectionFactory.getUserData().getUsername());
                    EnjinClient.loggedIn = true;
                }catch(IOException e){
                    e.printStackTrace();
                    System.out.println("Failed to send code.");
                    return;
                }
                return;
            }

            enArgs(input);
        }

        if(args.length > 1){

            if(!EnjinClient.loggedIn){
                System.out.println("You have to log in first!");
                return;
            }

            if(args[0].equalsIgnoreCase("bot")){

                if(EnjinClient.botWorker == null){
                    System.out.println("Bot mode not enabled.");
                    return;
                }

                if(EnjinClient.botWorker.isBusy()){
                    System.out.println("Bot is busy. Do 'bot stop' to force end the task.");
                    return;
                }

                if(args[1].equalsIgnoreCase("acceptRequests")){
                    EnjinClient.botWorker.acceptRequests();
                    return;
                }

                if(args[1].equalsIgnoreCase("sendGoodVibes")){

                    if(args.length > 1){

                        UserData user;


                        if(args.length > 2) {
                            try {
                                user = WallWorker.getData(connectionFactory, Integer.parseInt(args[2]));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid user code.");
                                return;
                            }
                        }else{
                            user = FriendWorker.getRandomFriend(connectionFactory, "all");
                        }

                        String message = EnjinClient.botWorker.generateFeelsGoodMessage();

                        if(args.length > 3){
                            message = getFinalArg(args, 3);
                        }


                        try {
                            connectionFactory.send(Wall.postMessage(connectionFactory.getUserData().getSessionID()
                                    , user.getUserID(), "text", message.replace("{name}", user.getUsername()), "", "", "", "", "", -1, -1, "", ""));
                            Thread.sleep(1000);
                            connectionFactory.readOutput();

                        }catch(IOException | InterruptedException e){
                            e.printStackTrace();
                            p("Failed to post feels good message :(");
                        }
                        System.out.println("Posted <3");


                    }

                    //  1    2              3     4
                    // bot sendGoodVibes [user] [message]

                }

                if(args[1].equalsIgnoreCase("friendRamp")){

                    if(args.length == 4){

                        int siteId;
                        int limit;
                        try{
                            siteId = Integer.parseInt(args[2]);
                            limit = Integer.parseInt(args[3]);
                        }catch(NumberFormatException e){
                            System.out.println("Invalid number at "+e.getLocalizedMessage());
                            return;
                        }

                        System.out.println("Beginning rampage starting at "+siteId+" with a limit of "+limit);
                        EnjinClient.botWorker.beginFriendRampage(siteId, limit);

                    }else{
                        System.out.println("bot friendRamp <starting-siteId> <limit> - Literally crawl through everyone's profile adding random people");
                    }


                }
            }

        }



    }

    private static void enArgs(String arg){
        switch (arg.trim().toLowerCase()){
            case "help":
                p("List of base commands:\n");
                p("login - Login to the API server");
                p("user - Gives information about current user");
                p("debug - Enable debug mode");
                p("friends - Commands for friends");
                p("wall - Commands for wall");
                p("notifications - Commands for notifications");
                p("bot - Bot mode");
                p("exit - Exit the application\n");
                break;

            case "login":
                if(EnjinClient.loggedIn){
                    System.out.println("Already logged in!");
                    break;
                }
                try {
                    p("Logging in...");
                    connectionFactory = new ConnectionFactory(new URL("https://api.enjin.com/api/v1/api.php"));
                    connectionFactory.openConnection(User.login("dompo.hayes@gmail.com", "-ayuPrA48Ph+"), true);
                    if(!waitingOnAuthCode && !connectionFactory.getUserData().getUsername().equals("undefined")) {
                        Thread.sleep(5000);
                        p("Logged in as " + connectionFactory.getUserData().getUsername());
                        EnjinClient.loggedIn = true;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    p("Failed to log in");
                }
                break;
            case "debug":
                EnjinClient.debug = !EnjinClient.debug;
                p("Debug set to "+EnjinClient.debug);
                break;
            case "friends":
                p("--- Friends ---\n");
                p("friends offlineFriends [0 - 25] - get offline friends since x days");
                p("friends removeInactive - Remove friends since [0 - 25]");
                break;
            case "wall":

                break;
            case "notifications":

                break;
            case "user":
                if(!EnjinClient.loggedIn){
                    p("Not logged in.");
                    break;
                }

                UserData userData = connectionFactory.getUserData();
                p("User information\n");
                p("Name: "+userData.getUsername());
                p("User ID: "+userData.getUserID());
                if(EnjinClient.debug) p("SessionID: "+userData.getSessionID());
                p("Last seen: "+userData.getLastSeen());
                p("Site count: "+userData.getSites().size());
                p("Unread mail count: "+userData.getMail());
                p("Unread notifications: "+userData.getNotifications());
                p("Pending friend requests: "+userData.getPendingFriendRequests());
                p("-------------------");
                break;
            case "bot":
                if(!EnjinClient.loggedIn){
                    p("Login first.");
                    break;
                }
                if(EnjinClient.botWorker == null){
                    EnjinClient.botWorker = new BotWorker(connectionFactory);
                    break;
                }
                System.out.println("Logged in in BOT mode.\n");
                System.out.println("bot friendRamp <starting-siteId> <limit> - Literally crawl through everyone's profile adding random people");
                System.out.println("bot acceptRequests - Accept all requests sent");
                System.out.println("bot sendGoodVibes [user] [message] - Send some good vibes to a random friend or a random message, or specify");
                break;
            case "exit":
                if(connectionFactory != null){
                    connectionFactory.closeConnection();
                }
                if(EnjinClient.botWorker != null){
                    try {
                        EnjinClient.botWorker.getBotHeartbeat().join();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                        p("Failed to finish.");
                    }
                }
                p("Shut down.");
                System.exit(0);
                break;
            default:
                p("Unknown command");
        }
    }

    private static void p(String a){
        System.out.println(a);
    }

    public static String getFinalArg(final String[] args, final int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        final String msg = sb.toString();
        sb.setLength(0);
        return msg;
    }

}
