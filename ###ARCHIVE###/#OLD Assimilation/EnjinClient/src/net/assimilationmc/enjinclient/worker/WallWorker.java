package net.assimilationmc.enjinclient.worker;

import net.assimilationmc.enjinclient.connection.ConnectionFactory;
import net.assimilationmc.enjinclient.connection.UserData;
import net.assimilationmc.enjinclient.module.Profile;
import org.json.simple.JSONObject;

/**
 * Created by Ellie on 01/01/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class WallWorker {

    public static void postText(){

    }

    public static UserData getData(ConnectionFactory connectionFactory, int userdata){


        try {
            connectionFactory.send(Profile.getProfile(connectionFactory.getUserData().getSessionID(), userdata));

            JSONObject jsonObject = connectionFactory.readOutput();

            UserData userData = new UserData(userdata);

            JSONObject result = (JSONObject) jsonObject.get("result");

            userData.setUsername((String)result.get("username"));
            userData.setLastSeen(Long.parseLong((String)result.get("last_login")));
            userData.setOnline((Boolean)result.get("is_online"));
            userData.setFavourite(Boolean.parseBoolean((String)result.get("favourite")));
            userData.setLocation((String)result.get("location"));
            userData.setProfileViews(Integer.parseInt((String)result.get("number_views")));
            userData.setFriendType((String)result.get("friend_type"));
            return userData;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Failed to get profile information!");
        }

        return new UserData(userdata);
    }

}
