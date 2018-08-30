package net.assimilationmc.ellie.assicore.task.fakeenjin.rpc;

import net.assimilationmc.ellie.assicore.task.fakeenjin.EnjinInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class SyncResponse {

    public String toString() {
        return "SyncResponse(instructions=" + getInstructions() + ", status=" + getStatus() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SyncResponse)) {
            return false;
        }
        SyncResponse other = (SyncResponse) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$instructions = getInstructions();
        Object other$instructions = other.getInstructions();
        if (this$instructions == null ? other$instructions != null : !this$instructions.equals(other$instructions)) {
            return false;
        }
        Object this$status = getStatus();
        Object other$status = other.getStatus();
        return this$status == null ? other$status == null : this$status.equals(other$status);
    }

    protected boolean canEqual(Object other) {
        return other instanceof SyncResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $instructions = getInstructions();
        result = result * 59 + ($instructions == null ? 43 : $instructions.hashCode());
        Object $status = getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    public List<EnjinInstruction> getInstructions() {
        return this.instructions;
    }

    private List<EnjinInstruction> instructions = new ArrayList<>();
    private String status;

    public String getStatus() {
        return this.status;
    }

}
