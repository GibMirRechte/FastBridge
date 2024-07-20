package net.fastbridge.utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerObj {

    private final UUID uuid;
    private String username;
    private boolean buildmode;
    private boolean vanished;
    private List<Location> placedBlocks = new ArrayList<>();
    private long started;
    private long ended;
    private boolean inQueue;
    private long bestRound;
    private Location spawn;

    public PlayerObj(UUID uuid, String username, long bestRound) {
        this.uuid = uuid;
        this.username = username;
        this.buildmode = false;
        this.vanished = false;
        this.bestRound = bestRound;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public void setSpawn(Location location){
        this.spawn = location;
    }

    public boolean isWithinBorder(Location location) {
        double xDifference = Math.abs(location.getX() - this.spawn.getX());
        double zDifference = location.getZ() - this.spawn.getZ();

        boolean isXWithinBounds = xDifference <= 10;
        boolean isZWithinBounds = zDifference <= 50 && zDifference >= -5;

        return isXWithinBounds && isZWithinBounds;
    }

    public Location getSpawn() {
        return spawn;
    }

    public long getBestRound() {
        return bestRound;
    }

    public void setBestRound(long millis){
        this.bestRound = millis;
    }

    public void setStopped(){
        this.ended = System.currentTimeMillis();
    }

    public long getEnded() {
        return ended;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(){
        this.started = System.currentTimeMillis();
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }

    public boolean isBuildmode() {
        return buildmode;
    }

    public void setBuildmode(boolean buildmode) {
        this.buildmode = buildmode;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }
}
