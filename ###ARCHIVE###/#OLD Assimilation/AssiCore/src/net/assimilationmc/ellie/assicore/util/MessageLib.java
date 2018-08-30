package net.assimilationmc.ellie.assicore.util;
/**
 * Created by Ellie on 05/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class MessageLib {

    // Errors
    public static String NO_PERMISSION = String.format(ColorChart.PREFIX, "c") + "Error! " + ColorChart.R + "No permission.";
    public static String NO_CONSOLE = "Console is disabled for this command.";
    public static String MODULE_OFFLINE = String.format(ColorChart.PREFIX, "c") + "Error! " + ColorChart.R + "%s failed to load. Contact an administrator as soon as possible.";
    public static String COMMAND_FAIL = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Failed to execute the following command: %s.";
    public static String CMD_DISABLED = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"This command is disabled.";
    public static String UNKNOWN_CMD = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Unknown command.";
    public static String CORRECT_USAGE = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Correct usage: "+ColorChart.COMMAND_USAGE+"/%s"+ColorChart.R+" - "+
            ColorChart.COMMAND_DESC+"%s"+ColorChart.R +".";
    public static String INVALID_NUMBER = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Invalid integer.";
    public static String PLAYER_OFFLINE = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Player offline.";
    public static String INVALID_SUB_CMD = String.format(ColorChart.PREFIX, "c") + "Error! "+ ColorChart.R +"Invalid sub-command";


    // Unstarted currently
    public static class FRIEND {

        public static String FRIEND_ACCEPTED = "%s has accepted your friend request. You are now friends! <3";
        public static String FRIEND_ADD = "You have sent a friend request to %s";
        public static String FRIEND_REMOVED = "You have removed %s from your friend list";
        public static String ALREADY_FRIEND = "You are already friends with that player";
        public static String NOT_FRIENDS = "You are not friends with this player";

        public static String REQUEST_RECIEVE = "You have received a friend request from %s";
        public static String REQUEST_DECLINE = "You have declined %s's friend request";
        public static String REQUEST_ACCEPT = "You have accepted %s's friend request";

    }

}
