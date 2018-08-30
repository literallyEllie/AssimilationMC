package net.assimilationmc.enjinclient.connection;

import net.assimilationmc.enjinclient.EnjinClient;
import net.assimilationmc.enjinclient.RequestHandler;
import net.assimilationmc.enjinclient.module.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionFactory {

    private URL url;
    private HttpURLConnection connection;

    private UserData userData;

    public ConnectionFactory(URL url){
        this.url = url;
    }

    public synchronized void openConnection(JSONObject initialStatement, boolean autoRead) throws Exception {
        if(!isConnection()) {
            if(EnjinClient.debug) System.out.println("Opening connection.");
            this.connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            first(initialStatement);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to establish connection! HTTP error code: " + connection.getResponseCode());
            }
            if(autoRead){
                if(EnjinClient.debug)
                    System.out.println("Output: "+readOutput());
                else readOutput();
            }

            if(EnjinClient.debug) System.out.println("Done.");
            return;
        }
        throw new RuntimeException("Connection is already established!");
    }

    public void closeConnection(){
        if(isConnection()) {
            this.connection.disconnect();
            this.connection = null;
            this.userData = null;
        }
    }

    private void first(JSONObject request) throws IOException {
        if(isConnection()){
            if(EnjinClient.debug) System.out.println("Sending initial object of: "+request.toJSONString());

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(request.toJSONString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            if(EnjinClient.debug) System.out.println("Sent object");
            return;
        }
        throw new RuntimeException("Connection is not established!");
    }

    public synchronized void send(JSONObject request) throws IOException {
        if(isConnection()){
            if(EnjinClient.debug) System.out.println("Sending object of: "+request.toJSONString());
            this.connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(request.toJSONString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to establish connection! HTTP error code: " + connection.getResponseCode());
            }
            if(EnjinClient.debug) System.out.println("Sent object");
            return;
        }
        throw new RuntimeException("Connection is not established!");
    }

    public JSONObject readOutput() throws IOException {
        if(isConnection()){

            BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

            StringBuilder stringBuilder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                stringBuilder.append(output);
            }
            try {
                JSONObject obj = (JSONObject) new JSONParser().parse(stringBuilder.toString());

                Object result = obj.get("result");

                if(result == null){

                    if(obj.containsKey("error")){
                        JSONObject jsonObject = (JSONObject) obj.get("error");
                        System.out.println("ERROR: error code "+jsonObject.get("code")+"; "+jsonObject.get("message"));

                        if(userData == null){
                            userData = new UserData("undefined", -1, "undefined");
                        }
                    }
                    return obj;
                }

                if(result instanceof JSONObject) {
                    JSONObject jsonResult = (JSONObject)result;

                    if (jsonResult.containsKey("session_id") ) {
                        if(!jsonResult.containsKey("username") && userData == null){
                            this.userData = new UserData((String)jsonResult.get("session_id"), (String)jsonResult.get("authy"));
                            send(User.authySMS(userData.getPartialLogin()));
                            readOutput();
                            System.out.println("Error. Please enter the AUTHY code sent to your phone to continue");
                            RequestHandler.waitingOnAuthCode = true;
                        }
                        else{
                            if(userData != null && userData.getUserID() == -1){
                                this.userData.setUsername((String)jsonResult.get("username"));
                                this.userData.setUserID(Integer.parseInt((String) jsonResult.get("user_id")));
                                this.userData.read(jsonResult);
                            }else
                                this.userData = new UserData((String) jsonResult.get("username"), Integer.parseInt((String) jsonResult.get("user_id")), (String) jsonResult.get("session_id"));
                        }
                    }
                }

                return obj;

            }catch(ParseException e){
                e.printStackTrace();
                System.out.println("Failed to to parse response");
            }
        }
        return new JSONObject();
    }

    public boolean isConnection(){
        return connection != null;
    }

    public UserData getUserData() {
        return userData;
    }



}
