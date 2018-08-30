package net.assimilationmc.ellie.assicore.event;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import org.bukkit.event.Cancellable;

import java.util.IllegalFormatException;

/**
 * Created by Ellie on 16.7.17 for AssimilationMCMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class AssiChatEvent extends AssiEvent implements Cancellable {

    private boolean cancel = false;
    private final AssiPlayer player;
    private String message;
    private String format;

    private ChatOverride chatOverride;

    public AssiChatEvent(AssiPlayer player, String message, String format){
        this.player = player;
        this.message = message;
        this.format = format;
    }

    public AssiPlayer getPlayer() {
        return player;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) throws IllegalFormatException, NullPointerException {
        try {
            format = String.format(format, this.player, this.message);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        this.format = format;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * If you are wanting to cancel the event to only send it to some players. See {@link ChatOverride}
     * To change format use AssiChatEvent#setFormat. Message placeholder is %2$s
     *
     * @param cancel Should the event pass.
     */
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public ChatOverride getChatOverride() {
        return chatOverride;
    }

    public void setChatOverride(ChatOverride chatOverride) {
        this.chatOverride = chatOverride;
    }

}
