package net.assimilationmc.assicore.auth;

import com.google.common.collect.Maps;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.util.C;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class RFC6238 {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private final String googleFormat = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=" +
            "otpauth://totp/%s@%s%%3Fsecret%%3D%s";
    private final Pattern properInputPattern = Pattern.compile("[0-9][0-9][0-9] ?[0-9][0-9][0-9]");

    private final Map<UUID, String> tempSecrets = Maps.newHashMap();
    private final AssiPlugin plugin;

    public RFC6238(AssiPlugin plugin) {
        this.plugin = plugin;
        validateServerTime();
    }

    public boolean authenticate(AssiPlayer player, AuthUser user, String input) {
        if (input.length() < 6) return false;

        if (input.charAt(3) == ' ')
            input = input.substring(0, 3) + input.substring(4);

        Integer code;
        try {
            code = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            player.sendMessage(C.II + "Invalid code!");
            return false;
        }

        String authSecret = tempSecrets.get(player.getUuid());
        boolean temp = authSecret != null;
        if (!temp) {
            authSecret = user.getSecret();
        }
        if (authSecret == null) {
            firstTimePlayer(player, user);
            return false;
        }

        boolean result = gAuth.authorize(authSecret, code);
        if (temp && result) {
            tempSecrets.remove(player.getUuid());
            user.setSecret(authSecret);
        }
        return result;
    }

    public void firstTimePlayer(AssiPlayer player, AuthUser user) {
        String newKey = createNewKey();
        tempSecrets.put(user.getUuid(), newKey);

        String message = C.II + "Please click this link " + C.V;

        try {
            message = message + getQRUrl(player.getName(), newKey);
        } catch (UnsupportedEncodingException ignore) {
        }

        player.getBase().sendMessage(message);
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
                plugin.getLogger().log(Level.SEVERE, "Could not validate the server's time!");
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
                plugin.getLogger().warning("The server's Unix time is off by "
                        + Math.abs(diff) + " seconds! 2FA may not work! Please "
                        + "correct this to make sure 2FA works.");
            }
        } catch (IOException | NumberFormatException e) {
            plugin.getLogger().severe("Was not able to validate the server's" +
                    " Unix time against an external service: Please ensure the" +
                    " server's time is set correctly or 2FA may not operate right.");
            e.printStackTrace();
        }
    }

    private String getQRUrl(String username, String secret) throws UnsupportedEncodingException {
        if (secret == null) return null;
        return String.format(googleFormat, username, URLEncoder.encode("AssimilationMC", "UTF-8"), secret);
    }

    private String createNewKey() {
        return gAuth.createCredentials().getKey();
    }

    public void playerQuit(Player player) {
        tempSecrets.remove(player.getUniqueId());
    }

}
