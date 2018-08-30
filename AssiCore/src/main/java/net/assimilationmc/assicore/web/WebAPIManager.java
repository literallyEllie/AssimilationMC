package net.assimilationmc.assicore.web;

import com.google.gson.Gson;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.Module;
import net.assimilationmc.assicore.util.Callback;
import net.assimilationmc.assicore.util.D;
import net.assimilationmc.assicore.web.request.JSONRequestBuilder;
import net.assimilationmc.assicore.web.request.RequestResponse;
import net.assimilationmc.assicore.web.request.WebEndpoint;
import net.assimilationmc.assicore.web.request.WebErrorID;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebAPIManager extends Module {

    private Gson gson;
    private WebServerData webServerData;
    private boolean enabled;

    public WebAPIManager(AssiPlugin plugin) {
        super(plugin, "Web API Manager");
    }

    @Override
    protected void start() {
        enabled = new File("WEB_SERVER").exists();
        if (!enabled) return;
        this.webServerData = new WebDataPropertyReader(new File("WEB_SERVER")).readWebData();

        this.gson = new Gson();

        testConnection();
    }

    @Override
    protected void end() {

    }

    private void testConnection() {
        JSONRequestBuilder jsonRequestBuilder = defaultBuilder();
        jsonRequestBuilder.setEndpoint(WebEndpoint.PING);

        sendRequest(jsonRequestBuilder, data -> {
            if (data.getCode() != 200) {
                enabled = false;
                throw new IllegalArgumentException("Invalid web credentials for server! Error code " + data.getErrorId().getErrorId() + ": " + data.getMessage());
            }
            log("Successfully authenticated with the Web API (credentials valid)");
        });
    }

    public void sendRequest(JSONRequestBuilder requestBuilder, Callback<RequestResponse> responseCallback) {
        if (!enabled) return;

        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> {

            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost(requestBuilder.getAddress());
                StringEntity stringEntity = new StringEntity(requestBuilder.build(gson));
                post.addHeader("content-type", "application/json");
                post.setEntity(stringEntity);

                HttpResponse response = httpClient.execute(post);
                final StatusLine statusLine = response.getStatusLine();

                JSONParser parser = new JSONParser();
                RequestResponse requestResponse;
                try {
                    JSONObject jsonObject = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity(), "UTF-8"));

                    WebErrorID id = null;

                    if (jsonObject.containsKey("errorId")) {
                        id = WebErrorID.fromString(String.valueOf(jsonObject.get("errorId")));

                        if (id == WebErrorID.UNKNOWN) {
                            log(Level.WARNING, "Unrecognized error response: " + jsonObject.get("errorId"));
                        }
                    }

                    requestResponse = new RequestResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase(),
                            String.valueOf(jsonObject.get("message")), id);

                } catch (ParseException e) {
                    log(Level.WARNING, "Failed to parse back response from webserver.");
                    e.printStackTrace();
                    return;
                }

                responseCallback.callback(requestResponse);
            } catch (IOException e) {
                log(Level.SEVERE, "Error making request to web server, is it offline?");
                e.printStackTrace();
            }

        });

    }

    public void sendOneWayRequest(JSONRequestBuilder requestBuilder) {
        if (!enabled) return;
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> sendSyncOneWayRequest(requestBuilder));
    }

    public void sendSyncOneWayRequest(JSONRequestBuilder requestBuilder) {
        if (!enabled) return;
        try {

            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setConnectionTimeToLive(1, TimeUnit.SECONDS).build();
            HttpPost post = new HttpPost(requestBuilder.getAddress());
            post.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(requestBuilder.build(gson));
            post.addHeader("content-type", "application/json");
            post.setEntity(stringEntity);
            httpClient.execute(post);
            post.abort();
        } catch (IOException e) {
            log(Level.SEVERE, "Error making request to web server, is it offline?");
            e.printStackTrace();
        }

    }

    public JSONRequestBuilder defaultBuilder() {
        if (!enabled) return null;
        return new JSONRequestBuilder("http://" + webServerData.getAddress() + ":" + webServerData.getPort(), webServerData.getToken());
    }

    public boolean isEnabled() {
        return enabled;
    }

}
