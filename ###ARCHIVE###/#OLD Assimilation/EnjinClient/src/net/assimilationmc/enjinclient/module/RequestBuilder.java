package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class RequestBuilder {

    private JSONObject jsonObject = new JSONObject();
    private JSONObject params = new JSONObject();


    public RequestBuilder(){
        jsonObject.put("jsonrpc", "2.0");
        jsonObject.put("id", Math.round(Math.random() * (999999 -
                100000) + 100000));
    }

    public RequestBuilder addParam(String key, Object value){
        params.put(key, value);
        return this;
    }

    public RequestBuilder setMethod(String method){
        jsonObject.put("method", method);
        return this;
    }

    public JSONObject build(){
        jsonObject.put("params", params);
        return jsonObject;
    }

}
