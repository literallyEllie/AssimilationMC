package net.assimilationmc.enjinclient.worker;

import net.assimilationmc.enjinclient.connection.ConnectionFactory;
import net.assimilationmc.enjinclient.connection.UserData;
import net.assimilationmc.enjinclient.module.Friends;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */

public class FriendWorker {

    public static List<UserData> getFriendsOfflineSince(JSONObject jsonObject, int days){

        List<UserData> userDatas = new ArrayList<>();
        JSONArray array = (JSONArray) ((JSONObject) jsonObject.get("result")).get("friends");

        System.out.println("Looping through "+array.size()+" friends.");
        Date now = new Date();
        Date daysBefore = new Date(now.getTime() - (days * 86400000));
        System.out.println("Deadline date: "+new SimpleDateFormat("dd-MM-yyyy").format(daysBefore));

        for (Object o : array) {
            JSONObject friend = (JSONObject) o;
            UserData userData = new UserData(Integer.valueOf((String) friend.get("friend_id")));
            userData.setUsername((String)friend.get("username"));
            userData.setLastSeen(Long.valueOf(String.valueOf(friend.get("seen"))));
            userData.setFavourite((boolean)friend.get("favorite"));
            userData.setOnline((boolean)friend.get("is_online"));
            userDatas.add(userData);
        }

        Iterator<UserData> iterator = userDatas.iterator();
        while(iterator.hasNext()) {
            UserData userData = iterator.next();

            Date lastSeen = new Date(userData.getLastSeen());
            lastSeen.setTime(userData.getLastSeen() * 1000L);

            if (userData.isFavourite() || lastSeen.after(daysBefore)) {
                iterator.remove();
            }
        }
        System.out.println("Returning "+userDatas.size()+" results");

        return userDatas;
    }

    public static void removeFriendsAsync(ConnectionFactory connectionFactory, List<UserData> data){

        System.out.println("Removing "+data.size()+" friends...");
        long start = System.currentTimeMillis();

        Iterator<UserData> userDataIterator = data.iterator();

        int i = 0;
        while(userDataIterator.hasNext()){
            UserData userData = userDataIterator.next();
            try {
                connectionFactory.send(Friends.removeFriend(connectionFactory.getUserData().getSessionID(), userData.getUserID()));
                connectionFactory.readOutput();
                i++;

                if(data.size() > 60)
                    Thread.sleep(60000);
                else if(data.size() > 30)
                    Thread.sleep(15000);
                else Thread.sleep(60000);
            }catch(IOException | InterruptedException e) {
                e.printStackTrace();
                System.out.println("Failed to remove friend " + userData.getUsername());
            }finally {
                userDataIterator.remove();
            }
        }
        System.out.println("Removed "+i+" friends in "+(System.currentTimeMillis() - start)+"ms");

    }

    public static UserData getRandomFriend(ConnectionFactory connectionFactory, String type){
        System.out.println("Getting random friend");
        long start = System.currentTimeMillis();

        try {
            connectionFactory.send(Friends.getList(connectionFactory.getUserData().getSessionID(), type, -1, -1, "", "", false));
            Thread.sleep(1000);

            JSONObject returned = connectionFactory.readOutput();

            JSONObject result_plain = (JSONObject) returned.get("result");
            JSONArray result = (JSONArray) result_plain.get("friends");

            JSONObject friend = (JSONObject) result.get(new Random().nextInt(result.size()));


            System.out.println("Finished task in "+(System.currentTimeMillis() - start)+"ms");
            return new UserData("", Integer.parseInt((String)friend.get("friend_id")), "undefined");

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
            System.out.println("Failed to get random friend.");
        }

        return new UserData(-1);
    }

}
