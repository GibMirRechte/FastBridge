package net.fastbridge.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rank {
    private final String name;
    private final String fullname;
    private final String tabName;
    private final String scoreboardTeam;
    private final String colorCode;
    private final boolean team;
    private final boolean testTeam;
    private final boolean canBan;
    private final boolean canMute;
    private final int maxFriends;
    private final int sort;
    private final List<String> permissions = new ArrayList<>();

    public Rank(int sort, String name, String fullname, String tabName, String scoreboardTeam, String colorCode, boolean team, boolean testTeam, boolean canBan, boolean canMute, int maxFriends, String permissions) {
        this.name = name;
        this.fullname = fullname;
        this.tabName = tabName;
        this.scoreboardTeam = scoreboardTeam;
        this.colorCode = colorCode;
        this.team = team;
        this.testTeam = testTeam;
        this.canBan = canBan;
        this.canMute = canMute;
        this.maxFriends = maxFriends;
        this.sort = sort;

        String[] splitPerms = permissions.split(", ");
        this.permissions.addAll(Arrays.asList(splitPerms));
    }

    public boolean hasPermission(String permission) {
        if (this.permissions.contains("*")) return true;
        return this.permissions.contains(permission);
    }

    public String getName() {
        return name;
    }

    public String getFullname() {
        return fullname;
    }

    public String getTabName() {
        return tabName;
    }

    public String getScoreboardTeam() {
        return scoreboardTeam;
    }

    public String getColorCode() {
        return colorCode;
    }

    public boolean isTeam() {
        return team;
    }

    public boolean isTestTeam() {
        return testTeam;
    }

    public boolean canBan() {
        return canBan;
    }

    public boolean canMute() {
        return canMute;
    }

    public int getMaxFriends() {
        return maxFriends;
    }

    public int getSort() {
        return sort;
    }
}