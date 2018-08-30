package net.assimilationmc.ellie.assicore.task.fakeenjin.rpc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionOptions;
import net.assimilationmc.ellie.assicore.api.AssiCore;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinRPC {

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Boolean.class, new BooleanAdapter())
            .create();

    private static final Integer READ_TIMEOUT = 15000;
    private static final Integer CONNECT_TIMEOUT = 15000;
    private static Integer nextRequestId = 0;

    public static URL getUrl(String clazz) {
        try {
            return new URL("https://api.enjin.com/api/v1/"+clazz);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONRPC2SessionOptions getOptions() {
        JSONRPC2SessionOptions options = new JSONRPC2SessionOptions();
        //options.setReadTimeout(READ_TIMEOUT.intValue());
        //options.setConnectTimeout(CONNECT_TIMEOUT.intValue());
        options.ignoreVersion(true);
        return options;
    }

    public static JSONRPC2Session getSession(String clazz) {
        URL url = getUrl(clazz);
        if (url == null) {
            AssiCore.getCore().logI("Api url is null");
            return null;
        }
        JSONRPC2Session session = new JSONRPC2Session(url);
        session.setOptions(getOptions());
        return session;
    }

    public static int genNum() {
        return (int) Math.round(Math.random() * (999999 - 100000) + 100000);
    }

}
