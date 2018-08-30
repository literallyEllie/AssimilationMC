package net.assimilationmc.assicore.web.request;

public class RequestResponse {

    private int code;
    private String message, detailedMessage;

    private WebErrorID errorId;

    public RequestResponse(int code, String message, String detailedMessage, WebErrorID webErrorID) {
        this.code = code;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.errorId = webErrorID;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public WebErrorID getErrorId() {
        return errorId;
    }

    public boolean isError() {
        return errorId != null;
    }

}
