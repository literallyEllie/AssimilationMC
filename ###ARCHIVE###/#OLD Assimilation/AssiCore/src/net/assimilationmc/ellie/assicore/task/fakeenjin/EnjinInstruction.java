package net.assimilationmc.ellie.assicore.task.fakeenjin;

/**
 * Created by Ellie on 21/04/2017 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class EnjinInstruction {

    private EnjinInstructionCode code;
    private Object data;

    public String toString() {
        return "Instruction(code=" + getCode() + ", data=" + getData() + ")";
    }

    public EnjinInstruction(EnjinInstructionCode code, Object data) {
        this.code = code;
        this.data = data;
    }

    public EnjinInstruction() {
    }

    public EnjinInstructionCode getCode() {
        return this.code;
    }

    public Object getData() {
        return this.data;
    }

}
