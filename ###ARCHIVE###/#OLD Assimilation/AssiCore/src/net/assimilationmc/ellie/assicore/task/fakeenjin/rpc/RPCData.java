package net.assimilationmc.ellie.assicore.task.fakeenjin.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

/**
 * Created by Ellie on 22/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class RPCData<T> {

    private Integer id;
    private T result;
    private RPCError error;
    private transient JSONRPC2Request request;
    private transient JSONRPC2Response response;

    public String toString() {
        return "RPCData(id=" + getId() + ", result=" + getResult() + ", error=" + getError() + ", request=" + getRequest() + ", response=" + getResponse() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RPCData)) {
            return false;
        }
        RPCData<?> other = (RPCData) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$id = getId();
        Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        Object this$result = getResult();
        Object other$result = other.getResult();
        if (this$result == null ? other$result != null : !this$result.equals(other$result)) {
            return false;
        }
        Object this$error = getError();
        Object other$error = other.getError();
        return this$error == null ? other$error == null : this$error.equals(other$error);
    }

    protected boolean canEqual(Object other) {
        return other instanceof RPCData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $id = getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $result = getResult();
        result = result * 59 + ($result == null ? 43 : $result.hashCode());
        Object $error = getError();
        result = result * 59 + ($error == null ? 43 : $error.hashCode());
        return result;
    }

    public Integer getId() {
        return this.id;
    }

    public T getResult() {
        return this.result;
    }

    public RPCError getError() {
        return this.error;
    }

    public JSONRPC2Request getRequest() {
        return this.request;
    }

    public void setRequest(JSONRPC2Request request) {
        this.request = request;
    }

    public JSONRPC2Response getResponse() {
        return this.response;
    }

    public void setResponse(JSONRPC2Response response) {
        this.response = response;
    }

}
