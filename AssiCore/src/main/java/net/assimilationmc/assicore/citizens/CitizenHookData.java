package net.assimilationmc.assicore.citizens;

public class CitizenHookData {

    private final int id;
    private String message;
    private String commandExec;

    public CitizenHookData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return hasMessage() ? message : "none";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public String getCommandExec() {
        return hasCommand() ? commandExec : "none";
    }

    public void setCommandExec(String commandExec) {
        this.commandExec = commandExec;
    }

    public boolean hasCommand() {
        return commandExec != null;
    }

}
