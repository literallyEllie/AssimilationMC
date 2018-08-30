package net.assimilationmc.ellie.assicore.task.fakeenjin.rpc.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class RawResponse {

    private Map<String, List<String>> headers;
    private int statusCode;
    private String statusMessage;
    private int contentLength;
    private String contentType;
    private String contentEncoding;
    private String content;

    protected static RawResponse parse(HttpURLConnection connection)
            throws IOException {
        String encoding = connection.getContentEncoding();
        InputStream is;
        if ((encoding != null) && (encoding.equalsIgnoreCase("gzip"))) {
            is = new GZIPInputStream(connection.getInputStream());
        } else {
            if ((encoding != null) && (encoding.equalsIgnoreCase("deflate"))) {
                is = new InflaterInputStream(connection.getInputStream(), new Inflater(true));
            } else {
                is = connection.getInputStream();
            }
        }
        StringBuilder responseText = new StringBuilder();

        BufferedReader input = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        while ((line = input.readLine()) != null) {
            responseText.append(line);
            responseText.append(System.getProperty("line.separator"));
        }
        input.close();

        RawResponse response = new RawResponse();

        response.content = responseText.toString();

        response.statusCode = connection.getResponseCode();
        response.statusMessage = connection.getResponseMessage();

        response.headers = connection.getHeaderFields();

        response.contentLength = connection.getContentLength();
        response.contentType = connection.getContentType();
        response.contentEncoding = encoding;

        return response;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public String getContent() {
        return this.content;
    }

    public Map<String, List<String>> getHeaderFields() {
        return this.headers;
    }

    public String getHeaderField(String name) {
        List<String> values = this.headers.get(name);
        if ((values == null) || (values.size() <= 0)) {
            return null;
        }
        return values.get(0);
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

}
