package net.assimilationmc.ellie.assicore.task.fakeenjin;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinExecutedCommand {

    private String id;
    private String hash;
    private String response;
    private transient String command;

    public String toString() {
        return "ExecutedCommand(id=" + getId() + ", hash=" + getHash() + ", response=" + getResponse() + ", command=" + getCommand() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EnjinExecutedCommand)) {
            return false;
        }
        EnjinExecutedCommand other = (EnjinExecutedCommand) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$id = getId();
        Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        Object this$hash = getHash();
        Object other$hash = other.getHash();
        if (this$hash == null ? other$hash != null : !this$hash.equals(other$hash)) {
            return false;
        }
        Object this$response = getResponse();
        Object other$response = other.getResponse();
        return this$response == null ? other$response == null : this$response.equals(other$response);
    }

    protected boolean canEqual(Object other) {
        return other instanceof EnjinExecutedCommand;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $hash = getHash();
        result = result * 59 + ($hash == null ? 43 : $hash.hashCode());
        Object $response = getResponse();
        result = result * 59 + ($response == null ? 43 : $response.hashCode());
        return result;
    }

    public String getId() {
        return this.id;
    }

    public String getHash() {
        return this.hash;
    }

    public String getResponse() {
        return this.response;
    }

    public String getCommand() {
        return this.command;
    }

    public EnjinExecutedCommand(String id, String command, String response) {
        this.id = id;
        this.command = command;
        this.response = response;
        this.hash = generateHash(command);
    }

    private String generateHash(String command) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(command.getBytes("UTF-8"));

            BigInteger bigInt = new BigInteger(1, digest);
            String hash = bigInt.toString(16);
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}