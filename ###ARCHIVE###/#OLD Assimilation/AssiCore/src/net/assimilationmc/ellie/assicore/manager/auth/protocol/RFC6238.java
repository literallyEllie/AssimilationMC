package net.assimilationmc.ellie.assicore.manager.auth.protocol;

import com.google.zxing.WriterException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.assimilationmc.ellie.assicore.api.AssiCore;
import net.assimilationmc.ellie.assicore.api.AssiPlayer;
import net.assimilationmc.ellie.assicore.manager.auth.ImageMapRenderer;
import net.assimilationmc.ellie.assicore.manager.auth.model.AuthUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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

public class RFC6238 {

    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private static final String googleFormat = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=" +
                            "otpauth://totp/%s@%s%%3Fsecret%%3D%s";
    private static final Pattern properInputPattern = Pattern.compile("[0-9][0-9][0-9] ?[0-9][0-9][0-9]");

    private final String serverIp;
    private final Map<UUID, String> tempSecrets = new HashMap<>();
    private final AssiCore assiCore;

    public RFC6238(AssiCore assiCore) {
        this.serverIp = "mc.assimilation.net";
        this.assiCore = assiCore;
        validateServerTime();
    }

    public boolean authenticate(AssiPlayer player, AuthUser user, String input) {
        if(input.charAt(3) == ' ')
            input = input.substring(0, 3) + input.substring(4);

        Integer code;
        try {
            code = Integer.parseInt(input);
        }catch(NumberFormatException e){
            player.sendMessage(ChatColor.RED + "Invalid code!");
            return false;
        }

        String authSecret = tempSecrets.get(player.getUuid());
        boolean temp = authSecret != null;
        if(!temp) {
            authSecret = user.getSecret();
        }
        if(authSecret == null) {
            firstTimePlayer(player, user);
            return false;
        }

        boolean result = gAuth.authorize(authSecret, code);
        if(temp && result) {
            tempSecrets.remove(player.getUuid());
            user.setSecret(authSecret);
        }
        return result;
    }

    public void firstTimePlayer(AssiPlayer player, AuthUser user) {
        String newKey = createNewKey();
        tempSecrets.put(user.getUuid(), newKey);

        String message = ChatColor.RED + "Please click this link " + ChatColor.GOLD;

        try{
            message = message + getQRUrl(player.getName(), newKey);
        }catch(UnsupportedEncodingException ignore) {}

        player.getBase().sendMessage(message);

        ImageMapRenderer imageMapRenderer;
        try {
            imageMapRenderer = new ImageMapRenderer(player.getName(), newKey, serverIp);
        }catch(WriterException e) {
            player.sendMessage(ChatColor.RED + "Failed to render your 2FA QR code.");
            return;
        }

        ItemStack itemStack = new ItemStack(Material.MAP);
        MapView map = Bukkit.createMap(player.getBase().getWorld());
        itemStack.setDurability(map.getId());
        itemStack.setAmount(0);
        player.getBase().getInventory().setHeldItemSlot(0);
        player.getBase().getInventory().setItem(0, itemStack);

        map.getRenderers().forEach(map::removeRenderer);
        map.addRenderer(imageMapRenderer);
        player.getBase().sendMap(map);
    }

    public boolean isFormat(String s) {
        return properInputPattern.matcher(s).matches();
    }

    private void validateServerTime() {
        // Since 1.0.2
        try {
            String TIME_SERVER = "http://icanhazepoch.com";
            HttpURLConnection timeCheckQuery =
                    (HttpURLConnection) new URL(TIME_SERVER).openConnection();
            timeCheckQuery.setReadTimeout(4000);
            timeCheckQuery.setConnectTimeout(4000);
            timeCheckQuery.connect();
            int responseCode = timeCheckQuery.getResponseCode();
            if (responseCode != 200) {
                assiCore.logE("Could not validate the server's time!");
                return;
            }
            byte[] response = new byte[1024]; // Response should never be over 1kB
            InputStream inputStream = timeCheckQuery.getInputStream();
            int len = inputStream.read(response);
            String rsp = new String(response, 0, len, Charset.defaultCharset()).trim();
            Long unixSeconds = Long.parseLong(rsp);
            long myUnixSeconds = (System.currentTimeMillis() / 1000);
            int diff = (int) (unixSeconds - myUnixSeconds);
            if (Math.abs(diff) > 30) {
                assiCore.logW("The server's Unix time is off by "
                        + Math.abs(diff) + " seconds! 2FA may not work! Please "
                        + "correct this to make sure 2FA works.");
            }
        } catch (IOException | NumberFormatException e) {
            assiCore.logE("Was not able to validate the server's" +
                    " Unix time against an external service: Please ensure the" +
                    " server's time is set correctly or 2FA may not operate right.");
            e.printStackTrace();
        }
    }

    private String getQRUrl(String username, String secret) throws UnsupportedEncodingException {
        if(secret == null) return null;
        return String.format(googleFormat, username, URLEncoder.encode(serverIp, "UTF-8"), secret);
    }

    private String createNewKey() {
        return gAuth.createCredentials().getKey();
    }

    public void playerQuit(Player player) {
        tempSecrets.remove(player.getUniqueId());
    }

}
