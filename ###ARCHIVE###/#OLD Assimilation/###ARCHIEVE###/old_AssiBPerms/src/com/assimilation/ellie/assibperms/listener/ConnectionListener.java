package com.assimilation.ellie.assibperms.listener;

import com.assimilation.ellie.assibperms.AssiBPerms;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Ellie on 14/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent e){

        ProxiedPlayer proxiedPlayer = e.getPlayer();
        AssiBPerms.getAssiBPerms().getUserManager().updatePlayerPerms(proxiedPlayer);

    }

}
