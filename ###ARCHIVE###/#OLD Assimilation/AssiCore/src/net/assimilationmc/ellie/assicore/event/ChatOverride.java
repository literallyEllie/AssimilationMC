package net.assimilationmc.ellie.assicore.event;

import net.assimilationmc.ellie.assicore.api.AssiPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ellie on 29/08/2017 for AssimilationMC.
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
public class ChatOverride {

    private final String logTag;
    private Set<AssiPlayer> recipients;

    public ChatOverride(String logTag) {
        this.logTag = logTag;
        this.recipients = new HashSet<>();
    }

    public String getLogTag() {
        return logTag;
    }

    public Set<AssiPlayer> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<AssiPlayer> recipients) {
        this.recipients = recipients;
    }

}
