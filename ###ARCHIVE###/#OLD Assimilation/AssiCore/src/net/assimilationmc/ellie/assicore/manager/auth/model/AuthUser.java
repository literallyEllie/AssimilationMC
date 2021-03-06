package net.assimilationmc.ellie.assicore.manager.auth.model;

import java.util.UUID;

/**
 * Created by Ellie on 02/09/2017 for AssimilationMC.
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
public class AuthUser {

    private int id;
    private UUID uuid;
    private String secret;
    private String lastIp;
    private boolean verified;

    public AuthUser() {
    }

    public AuthUser(UUID uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLast_ip(String lastIp) {
        this.lastIp = lastIp;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

}
