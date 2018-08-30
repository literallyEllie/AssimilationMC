package net.assimilationmc.assicore.web.request;

public enum WebErrorID {

    INVALID_REQUEST("InvalidMethod"),
    DATA_PARSE_FAIL("DataParseFail"),
    BAD_AUTH("BadAuthentication"),
    BAD_END_POINT("EndPoint"),
    BAD_PAYLOAD("BadPayload"),
    ACTION_FAILURE("ActionFailure"),

    TOO_MANY_SERVERS("TooManyActiveServers"),
    SERVER_NOT_FOUND("ServerNotFound"),

    INVALID_GAME_TYPE("InvalidGameType"),
    MAP_NOT_FOUND("MapNotFound"),

    TOKEN_EXISTS("TokenExists"),
    TOKEN_NO_EXIST("TokenNoExists"),

    UNKNOWN("");

    private final String errorId;

    WebErrorID(String errorId) {
        this.errorId = errorId;
    }

    public static WebErrorID fromString(String in) {
        for (WebErrorID webErrorID : values()) {
            if (webErrorID.getErrorId().equals(in)) {
                return webErrorID;
            }
        }
        return UNKNOWN;
    }

    public String getErrorId() {
        return errorId;
    }

}
