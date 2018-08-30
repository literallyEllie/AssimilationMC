package net.assimilationmc.assicore.web.request;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class JSONRequestBuilder {

    private transient final String address;
    @SerializedName("token")
    private final String token;
    @SerializedName("endpoint")
    private String endpoint;
    @SerializedName("method")
    private String method;
    @SerializedName("payload")
    private Map<String, Object> payload;

    public JSONRequestBuilder(String address, String token) {
        this.address = address;
        this.token = token;
        this.payload = Maps.newHashMap();
    }

    public String getAddress() {
        return address;
    }

    public String getEndpoint() {
        return endpoint.toLowerCase();
    }

    public JSONRequestBuilder setEndpoint(WebEndpoint endpoint) {
        this.endpoint = endpoint.name().toLowerCase();
        return this;
    }

    public String getMethod() {
        return method;
    }

    public JSONRequestBuilder setMethod(RequestMethod requestMethod) {
        this.method = requestMethod.name();
        return this;
    }

    public JSONRequestBuilder addParameter(String key, Object value) {
        payload.put(key, value);
        return this;
    }

    public String build(Gson gson) {
        return gson.toJson(this);
    }

}
