package com.assimilation.ellie.assibungee.server;

import java.util.List;

/**
 * Created by Ellie on 12/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ServerPingResponse {

    private String description;
    private Players players;
    private Version version;

    public String getDescription() {
        return this.description;
    }

    public Players getPlayers() {
        return this.players;
    }

    public Version getVersion() {
        return this.version;
    }

    public class Players {

        private int max;
        private int online;
        private List<Player> sample;

        public int getMax() {
            return this.max;
        }

        public int getOnline() {
            return this.online;
        }

        public List<Player> getSample() {
            return this.sample;
        }

    }

    public class Player {

        private String name;
        private String id;

        public String getName() {
            return this.name;
        }

        public String getID() {
            return this.id;
        }

    }

    public class Version {

        private String name;
        private int protocol;

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }
    }

}
