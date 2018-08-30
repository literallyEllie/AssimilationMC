package net.assimilationmc.ellie.assiuhc.games;

import net.assimilationmc.ellie.assicore.util.Util;

/**
 * Created by Ellie on 23/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class TimeoutCountdown {

    private final String name;
    private final int game;
    private final int taskid;
    private final long start;
    private final long end;

    public TimeoutCountdown(String name, int game, int taskid, long futureEnd){
        this.name = name;
        this.game = game;
        this.taskid = taskid;
        this.start = System.currentTimeMillis();
        this.end = futureEnd;
    }

    public String getName() {
        return name;
    }

    public int getGame() {
        return game;
    }

    public int getTaskID() {
        return taskid;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String toMessage() {
        return "\n&c\n&c&lATTENTION&c&l You have another &6"+ Util.formatDateDiff(end - start)+"&c to rejoin your game again! \n&cOr you will be timeouted from playing competitive!\n" +
                "&cDo &6/uhc rejoin &cto rejoin or &6/uhc abandon &cto take the forfeit.\n&c";
    }

}
