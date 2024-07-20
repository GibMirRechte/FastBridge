package net.fastbridge.utils;

import net.fastbridge.handler.RankHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ProxyPlayer {

    private final UUID uuid;
    private final HashMap<String, Long> ranks;
    RankHandler rankHandler = RankHandler.getInstance();
    private String username;
    private Rank displayRank;
    private long globalXP;
    private long globalCoins;
    private boolean autoNick;
    private boolean autoVanish;
    private String nickedName;

    public ProxyPlayer(UUID uuid, String username, String rankString, String displayRank, long globalXP, long globalCoins, boolean autoNick, boolean autoVanish) {
        this.uuid = uuid;
        this.username = username;
        this.globalXP = globalXP;
        this.globalCoins = globalCoins;
        this.ranks = parseRanks(rankString);
        Rank dRank = rankHandler.getRank(displayRank);
        if (dRank == null) {
            this.displayRank = this.getRank();
        } else {
            this.displayRank = dRank;
        }

        if (!this.getRank().isTeam()) {
            this.autoNick = false;
            this.autoVanish = false;
        } else {
            this.autoNick = autoNick;
            this.autoVanish = autoVanish;
        }

        this.nickedName = "";
    }

    private HashMap<String, Long> parseRanks(String ranksString) {
        HashMap<String, Long> ranks = new HashMap<>();
        String[] rankStringSplitted = ranksString.split(":");
        for (String s : rankStringSplitted) {
            String[] splitted = s.split("=");
            Rank rank = rankHandler.getRank(splitted[0]);
            if (rank != null) {
                long until = Long.parseLong(splitted[1]);
                ranks.put(rank.getName(), until);
            }


        }
        if (ranks.isEmpty()) {
            ranks.put("Default", -1L);
        }
        return ranks;
    }

    public boolean isAutoVanish() {
        return autoVanish;
    }

    public boolean isAutoNick() {
        return autoNick;
    }

    public boolean isNicked() {
        return !this.nickedName.isBlank();
    }

    public String getNickedName() {
        return nickedName;
    }

    public void setNickedName(String nickedName) {
        this.nickedName = nickedName;

        if (nickedName.isBlank()) {
            this.displayRank = this.getRank();
        } else {
            this.displayRank = rankHandler.getRank("default");
        }
    }

    public boolean hasPermission(String permission) {
        return this.ranks.entrySet().stream()
                .anyMatch(entry -> {
                    Rank rank = rankHandler.getRank(entry.getKey());
                    return rank.hasPermission(permission) &&
                            (entry.getValue() == -1 || entry.getValue() > System.currentTimeMillis());
                });
    }

    public Rank getDisplayRank() {
        if (!this.getRank().isTeam() || this.displayRank == null) this.displayRank = this.getRank();
        return this.displayRank;
    }

    public void setDisplayRank(Rank displayRank) {
        this.displayRank = displayRank;
    }

    public String getTabName() {
        String displayName;
        if (this.getDisplayRank().getTabName().length() == 2) {
            displayName = this.getDisplayName();
        } else {
            displayName = this.getDisplayRank().getTabName() + " §8▏ " + this.getDisplayName();
        }
        return displayName;
    }

    public long getGlobalXP() {
        return globalXP;
    }

    public void setGlobalXP(long globalXP) {
        this.globalXP = globalXP;
    }

    public long getGlobalCoins() {
        return globalCoins;
    }

    public void setGlobalCoins(long coins) {
        this.globalCoins = coins;
    }

    public void addGlobalCoins(long coins) {
        this.globalCoins += coins;
    }

    public void addRankDuration(Rank rank, long duration) {
        if (!this.ranks.containsKey(rank.getName())) {
            ranks.put(rank.getName(), (System.currentTimeMillis() + duration));
        } else {
            long oldDuration = this.ranks.get(rank.getName());
            if (oldDuration == -1) return;
            if (oldDuration > System.currentTimeMillis()) {
                this.ranks.put(rank.getName(), (oldDuration + duration));
            } else {
                ranks.put(rank.getName(), (System.currentTimeMillis() + duration));
            }
        }
    }

    public void addGlobalEXP(long exp) {
        this.globalXP += exp;
    }

    public void removeGlobalCoins(long coins) {
        this.globalCoins -= coins;
        if (this.globalCoins < 0) this.globalCoins = 0;
    }

    public String getDisplayName() {
        if (this.isNicked())
            return this.getDisplayRank().getColorCode() + this.nickedName;
        return this.getDisplayRank().getColorCode() + this.username;
    }

    public long getLevel() {
        long accumulatedXP = 0;
        long level = 1;

        while (accumulatedXP <= this.globalXP) {
            accumulatedXP += level * 1000L;
            level++;
        }

        return Math.max(1, level - 1);
    }

    public double getProgressInCurrentLevel() {
        long accumulatedXP = 0;
        long level = 1;

        while (accumulatedXP <= this.globalXP) {
            accumulatedXP += level * 1000L;
            level++;
        }
        level--;

        long xpNeededForCurrentLevel = level * 1000L;
        long xpProgressInCurrentLevel = xpNeededForCurrentLevel - (accumulatedXP - this.globalXP);
        double progress = (double) xpProgressInCurrentLevel / xpNeededForCurrentLevel;

        return Math.round(progress * 10000.0) / 100.0;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Rank getRank() {
        AtomicReference<Rank> rank = new AtomicReference<>(rankHandler.getRank("Default"));
        List<String> ranksCopy = new ArrayList<>(this.ranks.keySet());

        ranksCopy.stream()
                .filter(r -> rankHandler.getRank(r).getSort() < rank.get().getSort())
                .forEach(r -> {
                    if (this.ranks.get(r) > System.currentTimeMillis() || this.ranks.get(r) == -1) {
                        rank.set(rankHandler.getRank(r));
                    } else {
                        this.ranks.remove(r);
                    }
                });
        return rank.get();
    }

    public String getRanksString() {
        StringBuilder s = new StringBuilder();

        for (String r : this.ranks.keySet()) {
            Rank rank = rankHandler.getRank(r);
            if (!s.toString().isBlank()) s.append(":");
            s.append(rank.getName()).append("=").append(this.ranks.get(r));
        }

        return s.toString();
    }

    public void updateRank(String rankString) {
        this.ranks.clear();
        String[] rankStringSplitted = rankString.split(":");
        for (String s : rankStringSplitted) {
            String[] splitted = s.split("=");

            Rank rankk = rankHandler.getRank(splitted[0]);
            long until = Long.parseLong(splitted[1]);

            this.ranks.put(rankk.getName(), until);
        }
    }
}
