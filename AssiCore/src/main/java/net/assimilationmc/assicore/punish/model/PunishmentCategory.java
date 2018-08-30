package net.assimilationmc.assicore.punish.model;

import net.assimilationmc.assicore.AssiPlugin;
import net.assimilationmc.assicore.player.AssiPlayer;
import net.assimilationmc.assicore.punish.PunishProfile;
import net.assimilationmc.assicore.punish.PunishmentManager;
import net.assimilationmc.assicore.rank.Rank;
import net.assimilationmc.assicore.redis.pubsub.PubSubRecipient;
import net.assimilationmc.assicore.redis.pubsub.RedisPubSubMessage;
import net.assimilationmc.assicore.staff.StaffChatManager;
import net.assimilationmc.assicore.util.C;
import net.assimilationmc.assicore.util.Domain;
import net.assimilationmc.assicore.util.UtilPlayer;
import net.assimilationmc.assicore.util.UtilTime;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public enum PunishmentCategory {

    // Game-[lay
    CLIENT("Hacking/Client", 2, "Using a hacked client", Material.DIAMOND_SWORD, "Obvious b-hopping or something"),
    BAN_EVADE("Ban evasion", 2, "Evading a ban", Material.COAL_BLOCK, "Joining on an alt account to a banned account"),

    // Chat
    LV1_ADVERTISING("Advertising", 3, "Sending an unauthorised link in chat", Material.BONE, "\"join my server at 123.456.983.12:6969\""),
    LV2_ADVERTISING("Mass Advertising", 1, "Spamming a server with advertisements", Material.ARROW, "The advertisement example just maybe over several accounts"),
    TOXICITY("Toxicity", 4, "Being rude or disrespectful in chat", Material.FIREBALL, "\"Bob123 u succ dicc\""),
    SPAMMING("Spamming", 4, "Spamming chat same or similar messages", Material.BREAD, "Spamming the same chat message or similar over and over"),
    THREATS("Serious Threats or doxing", 2, "Threatening players or the network", Material.CACTUS, "\"lol bob's IP is 78.324.13823\""),

    // UHC
    BAD_UHC_TEAM_NAME("Offensive Team Name", 4, "Having an inappropriate UHC team-name", Material.ITEM_FRAME, "Having a team called like \"Nazis\" etc"),

    // Other
    MALICIOUS_ACTIONS("Malicious actions towards Network/Players", 1, "Causing malicious actions towards the network or a player", Material.BARRIER,
            "DDoS'ing server or hacking it and stuff"),
    CHARGE_BACK("Donation charge-back", 1, "Charging back a payment from the donation store", Material.RED_MUSHROOM, "Charging back a payment from the donation store");

    public static final int TIME_INAPPLICABLE = -1;
    public static final int TIME_PERM = -2;

    private String display, baseReason, example;
    private int maxOffenses;
    private Material displayMaterial;

    /**
     * An enum of punishments, they represent all the possible reasons a player could be punished on AssimilationMC.
     *
     * @param display         the display name of the punishment.
     * @param maxOffenses     The max offences it is possible to get in this category.
     * @param baseReason      The default reason to supply if a custom reason is not supplied.
     * @param displayMaterial The display material to represent this punishment type.
     * @param example         An example of where this punishment category would be applicable.
     */
    PunishmentCategory(String display, int maxOffenses, String baseReason, Material displayMaterial, String example) {
        this.display = display;
        this.maxOffenses = maxOffenses;
        this.baseReason = baseReason;
        this.displayMaterial = displayMaterial;
        this.example = example;
    }

    /**
     * @return the display name of the punishment type, primarily to show on UIs.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @return The possible max offences it is possible to hit with this category.
     */
    public int getMaxOffenses() {
        return maxOffenses;
    }

    /**
     * @return the default reason for someone being punished for this. If a custom reason is null, this will be used.
     */
    public String getBaseReason() {
        return baseReason;
    }

    /**
     * @return The material to represent this on a punish UI.
     */
    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    /**
     * @return an example of a case where the punishment would be applicable.
     */
    public String getExample() {
        return example;
    }

    /**
     * A method to simply carry out a punishment, but doesn't actually modify player request.
     * If the player is not online, it will send over Redis to try and find the player (may never be found).
     *
     * @param plugin   The plugin instance.
     * @param uuid     The uuid of the player who has been punished.
     * @param severity The severity of the punishment.
     * @param reason   The reason for punishment (can be null).
     */
    public void carryOut(AssiPlugin plugin, UUID uuid, int severity, String reason) {
        if (reason == null) reason = getBaseReason();

        Player player = UtilPlayer.get(uuid);
        if (player == null) {
            plugin.getRedisManager().sendPubSubMessage(PunishmentManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.SPIGOT,
                    plugin.getServerData().getId(), "CARRY_PUNISH", new String[]{uuid.toString(), this.name(), String.valueOf(severity), reason}));
            return;
        }

        Pair<Long, PunishmentOutcome> punishTime = getPunishmentTime(severity);
        PunishmentOutcome punishmentOutcome = punishTime.getRight();

        player.sendMessage(C.C);

        switch (punishmentOutcome) {
            case WARN:
                player.sendMessage(PunishmentManager.PREFIX + "You have been " + C.V + "warned" + C.II + " for " + C.V + reason + C.II + ".");
                break;
            case KICK:
                String finalReason = reason;
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(C.II + "Kicked" + C.C + "\n\n" +
                        "Reason: " + C.V + finalReason));
                return;
            case TEMP_MUTE:
            case PERM_MUTE:
                boolean perm = punishmentOutcome == PunishmentOutcome.PERM_MUTE;
                player.sendMessage(PunishmentManager.PREFIX + "You have been " + C.V + (perm ? "perm-" : "temp-") +
                        "muted" + C.II + " for " + C.V + reason + C.C + ".");
                player.sendMessage(C.II + "It will expire in " + C.V + (perm ? "never :(" : punishTime.getLeft() + " days") + C.II + ".");
                break;
            case TEMP_BAN:
            case PERM_BAN:
            case IP_BAN_TEMP:
            case IP_BAN_PERM:
                perm = punishmentOutcome == PunishmentOutcome.PERM_BAN || punishmentOutcome == PunishmentOutcome.IP_BAN_PERM;

                String message = C.II + (perm ? "Perm" : "Temp") + "-banned" + C.C + "\n\n" +
                        "Punish Category: " + C.V + this.getDisplay() + C.C + "\n" +
                        "Reason: " + C.V + reason + C.C + "\n\n" +
                        "Expires in: " + C.V + (perm ? "never :(" : punishTime.getLeft() + " days") + C.C + "\n\n" +
                        "Disagree? Appeal on our forum: " + C.V + Domain.PROT_FORUM;

                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(message));

                if (punishmentOutcome.isIPPunish()) {
                    String ip = UtilPlayer.getIP(player);
                    plugin.getRedisManager().sendPubSubMessage("INTERNAL", new RedisPubSubMessage(PubSubRecipient.PROXY,
                            plugin.getServerData().getId(), "IP_KICK", new String[]{ip, message}));
                }

                return;
            case UHC_TEAM_BLACKLIST:
                player.sendMessage(PunishmentManager.PREFIX + "You have been " + C.V + "blacklisted from UHC Team Games" + C.C + ". It will be effective until " +
                        "you any appeal you make for it is accepted.");

                plugin.getRedisManager().sendPubSubMessage("UHC", new RedisPubSubMessage(PubSubRecipient.SPIGOT, plugin.getServerData().getId(),
                        "BLACKLIST", new String[]{player.getUniqueId().toString()}));
                break;
        }

        if (!punishmentOutcome.isBan()) {
            player.sendMessage(C.C + "Disagree with your punishment? Appeal on our forum: " + C.V + Domain.PROT_FORUM);
        }

        player.sendMessage(C.C);

    }

    /**
     * A method tp effective punish a player. Please do not use this for punishing directly. Go through
     * {@link PunishmentManager#punish(AssiPlayer, AssiPlayer, PunishmentCategory, String)} instead of this direcly.
     *
     * @param plugin       The plugin instance.
     * @param target       The player to append punishments to.
     * @param punisher     The punisher.
     * @param severity     The severity of the punishment.
     * @param customReason The custom reason they are being punished (Can be null)
     */
    public void punish(AssiPlugin plugin, AssiPlayer target, AssiPlayer punisher, int severity, String customReason) {
        PunishProfile punishProfile = target.getPunishmentProfile(false);
        if (punishProfile == null) return;

        Pair<Long, PunishmentOutcome> punishTime = getPunishmentTime(severity);
        long time = punishTime.getKey();

        if (punisher.getRank() == Rank.HELPER && time == TIME_PERM) {
            punisher.sendMessage(C.II + "You cannot perform this action. The next punishment is permanent and as a Helper you do not have access to punish for that." +
                    " Please contact a " + Rank.MOD.getPrefix());
            return;
        }

        if (punishProfile.getEffectiveBan() != null && punishTime.getRight().isBan()) {
            punisher.sendMessage(C.V + target.getName() + C.II + " already has an active ban, you must remove their current ban to add the new one.");
            return;
        }

        if (punishProfile.getEffectiveMute() != null && punishTime.getRight().isMute()) {
            punisher.sendMessage(C.V + punishProfile.getName() + C.II + " already has an active mute, you must remove their current mute to add the new one.");
            return;
        }

        PunishmentData data = new PunishmentData(punishProfile.getPunishmentCounter(), punisher.getUuid(), punishProfile.getUuid(), punisher.getDisplayName(),
                this, punishTime.getValue(), severity, System.currentTimeMillis(),
                (time == TIME_PERM || time == TIME_INAPPLICABLE ? time : TimeUnit.DAYS.toMillis(time)));
        data.setCustomReason(customReason);
        if (data.getPunishmentType().isIPPunish()) {
            data.setIp(target.lastIP());
        }

        punishProfile.addPunishment(this, data);

        // push
        plugin.getPunishmentManager().pushPunishmentRedis(data);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPunishmentManager().pushNewPunishmentSQL(data));

        // msg staff
        plugin.getRedisManager().sendPubSubMessage(StaffChatManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL,
                plugin.getServerData().getId(), "PUNISH", new String[]{punisher.getDisplayName(), data.getPunishmentType().name(),
                punishProfile.getName(), data.getPunishmentCategory().name(), data.getReason()}));

        plugin.getStaffChatManager().msgPunishUpdate(punisher.getDisplayName(), data.getPunishmentType().toString(), punishProfile.getName(),
                data.getPunishmentCategory().name(), data.getReason());

        plugin.getDiscordCommunicator().messageChannel("PUNISH_LOG", "[" + UtilTime.getTime() + "] `" + punishProfile.getName() + "` " +
                "was " + data.getPunishmentType().toString() + " by " + punisher.getName() +
                " for `(" + this.getDisplay() + ") " + data.getReason() + "` " + (data.isPerm() ? "for forever" : (
                data.getPunishExpiry() == TIME_INAPPLICABLE ? "" : "for " + TimeUnit.MILLISECONDS.toDays(data.getPunishLength()) + "days")));

        carryOut(plugin, punishProfile.getUuid(), severity, data.getReason());
    }

    /**
     * Method to unpunish a player. Do not use this method to directly, instead go through {@link PunishmentManager#unpunish(AssiPlayer, AssiPlayer, int, String)}.
     *
     * @param plugin         The plugin instance.
     * @param unpunisher     The unpunisher.
     * @param unpunished     The person who has been unpunished.
     * @param punishmentData The punishment request that has been modified.
     */
    public void unPunish(AssiPlugin plugin, AssiPlayer unpunisher, UUID unpunishedUuid, String unpunished, PunishmentData punishmentData) {
        if (punishmentData.getUnpunishData() == null) return;

        plugin.getPunishmentManager().pushPunishmentRedis(punishmentData);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPunishmentManager().pushExistPunishment(punishmentData));

        plugin.getStaffChatManager().msgPunishUpdate(unpunisher.getDisplayName(), "un-" + punishmentData.getPunishmentType().toString(),
                unpunished, punishmentData.getPunishmentCategory().name(), punishmentData.getUnpunishData().getUnpunishReason());


        plugin.getRedisManager().sendPubSubMessage(StaffChatManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL,
                plugin.getServerData().getId(), "PUNISH", new String[]{unpunisher.getDisplayName(), "un-" + punishmentData.getPunishmentType().toString(),
                unpunished, punishmentData.getPunishmentCategory().name(), punishmentData.getUnpunishData().getUnpunishReason()}));

        Player unpunishedPlayer = UtilPlayer.get(unpunishedUuid);

        plugin.getRedisManager().sendPubSubMessage(PunishmentManager.REDIS_CHANNEL, new RedisPubSubMessage(PubSubRecipient.ALL,
                plugin.getServerData().getId(), "UNPUNISH", new String[]{unpunisher.getDisplayName(), unpunishedUuid.toString(),
                String.valueOf(punishmentData.getId()), String.valueOf(punishmentData.getUnpunishData().getUnpunishTime()), String.valueOf(unpunishedPlayer == null),
                punishmentData.getUnpunishData().getUnpunishReason()}));

        plugin.getDiscordCommunicator().messageChannel("PUNISH_LOG", "[" + UtilTime.getTime() + "] `" + unpunished + "` " +
                "was un-" + punishmentData.getPunishmentType().toString() + " by " + unpunisher.getName() + " for `" + punishmentData.getUnpunishData().getUnpunishReason() + "`");

        if (unpunishedPlayer != null) {
            unpunishedPlayer.sendMessage(PunishmentManager.PREFIX + "Its your lucky day! " + C.C + "You have been unpunished for " + C.V +
                    punishmentData.getUnpunishData().getUnpunishReason());
        }

    }

    /**
     * Get the punishment time depending on the severity.
     *
     * @param severity the severity of the punishment.
     * @return A tuple of how long the punishment will be in days and the outcome of the punishment.
     * See {@link PunishmentOutcome}
     */
    public Pair<Long, PunishmentOutcome> getPunishmentTime(int severity) {

        long time = TIME_INAPPLICABLE;
        PunishmentOutcome punishmentOutcome = PunishmentOutcome.WARN;

        switch (this) {
            case CLIENT:
                time = severity == 1 ? 30 : TIME_PERM;
                punishmentOutcome = severity == 1 ? PunishmentOutcome.TEMP_BAN : PunishmentOutcome.PERM_BAN;
                break;
            case BAN_EVADE:
                time = severity == 1 ? 14 : TIME_PERM;
                punishmentOutcome = severity == 1 ? PunishmentOutcome.IP_BAN_TEMP : PunishmentOutcome.IP_BAN_PERM;
                break;
            case LV1_ADVERTISING:
                switch (severity) {
                    case 1:
                        time = 1;
                        punishmentOutcome = PunishmentOutcome.TEMP_MUTE;
                        break;
                    case 2:
                        time = 30;
                        punishmentOutcome = PunishmentOutcome.TEMP_MUTE;
                        break;
                    default: // 3
                        time = TIME_PERM;
                        punishmentOutcome = PunishmentOutcome.PERM_MUTE;
                        break;
                }
                break;
            case LV2_ADVERTISING:
                time = TIME_PERM;
                punishmentOutcome = PunishmentOutcome.PERM_MUTE;
                break;
            case TOXICITY:
            case SPAMMING:
                switch (severity) {
                    case 1:
                        break;
                    case 2:
                        time = 1;
                        punishmentOutcome = PunishmentOutcome.TEMP_MUTE;
                        break;
                    case 3:
                        time = 30;
                        punishmentOutcome = PunishmentOutcome.TEMP_MUTE;
                        break;
                    default: // 4
                        time = TIME_PERM;
                        punishmentOutcome = PunishmentOutcome.PERM_MUTE;
                        break;
                }
                break;
            case THREATS:
                time = severity == 1 ? 7 : TIME_PERM;
                punishmentOutcome = severity == 1 ? PunishmentOutcome.TEMP_BAN : PunishmentOutcome.PERM_BAN;
                break;
            case BAD_UHC_TEAM_NAME:
                switch (severity) {
                    case 1:
                        break;
                    case 2:
                        time = 7;
                        punishmentOutcome = PunishmentOutcome.UHC_TEAM_BLACKLIST;
                        break;
                    case 3:
                        time = 10;
                        punishmentOutcome = PunishmentOutcome.TEMP_BAN;
                        break;
                    default:
                        time = TIME_PERM;
                        punishmentOutcome = PunishmentOutcome.PERM_BAN;
                        break;
                }
                break;
            case MALICIOUS_ACTIONS:
                time = TIME_PERM;
                punishmentOutcome = PunishmentOutcome.IP_BAN_PERM;
                break;
            case CHARGE_BACK:
                time = TIME_PERM;
                punishmentOutcome = PunishmentOutcome.PERM_BAN;
                break;
        }


        return new ImmutablePair<>(time, punishmentOutcome);
    }

}
