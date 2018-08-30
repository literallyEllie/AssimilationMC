package net.assimilationmc.assiuhc.logger;

public enum GameEventType {

    START, // who is a player at end of Lobby game phase.
    WARMUP, // Marks warmup start
    END, // Marks game end

    DEATH, // and killed by if applicable
    TEAM_KNOCKOUT, // when a team is wiped out

    WIN, // If win and if it was team or player.

}
