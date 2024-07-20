package net.fastbridge.handler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.fastbridge.main.Main;
import net.fastbridge.utils.PlayerObj;
import net.fastbridge.utils.ProxyPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class RoundHandler {

    UserHandler userHandler = UserHandler.getInstance();
    private List<UUID> playersInRound = new ArrayList<>();
    private HashMap<UUID, Long> bestRounds = new HashMap<>();
    private List<String> top3List = new ArrayList<>();
    private List<Location> takenLocations = new ArrayList<>();
    private BukkitRunnable runnable;

    private static RoundHandler instance;

    public void startRound(Player player) {
        if (player == null) return;

        PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());
        playerObj.setStarted();
        playersInRound.add(player.getUniqueId());
        if (playersInRound.size() == 1) startRunnable();
    }

    public void stopRound(Player player, boolean finished) {
        playersInRound.remove(player.getUniqueId());

        if (playersInRound.isEmpty() && runnable != null) {
            runnable.cancel();
        }

        PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());
        if (playerObj.getSpawn() != null) {
            player.teleport(playerObj.getSpawn());
        }
        userHandler.setupInventory(player);
        playerObj.setStopped();
        long duration = System.currentTimeMillis() - playerObj.getStarted();
        for (Location blockLoc : playerObj.getPlacedBlocks()) {
            blockLoc.getWorld().getBlockAt(blockLoc).setType(Material.AIR);
        }
        playerObj.getPlacedBlocks().clear();

        if (isValid(duration) && finished) {
            ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(player.getUniqueId());
            long xp = 10;
            long coins = 5;
            proxyPlayer.addGlobalEXP(xp);
            proxyPlayer.addGlobalCoins(coins);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 3, 2);
            player.sendMessage(Main.PREFIX + "§aYou have successfully completed the try. Your time: §e" + String.format("%.2f", duration / 1000.0) + " §8(§b+" + xp + "EXP §6+" + coins + " Coins§8)");
            if (playerObj.getBestRound() > duration || playerObj.getBestRound() == 0) {
                playerObj.setBestRound(duration);
                player.sendMessage(Main.PREFIX + "§aYou have set a new personal record: §e" + String.format("%.2f", duration / 1000.0));
            }

            if (bestRounds.containsKey(player.getUniqueId())) {
                if (bestRounds.get(player.getUniqueId()) > duration) {
                    bestRounds.put(player.getUniqueId(), duration);
                }
            } else {
                bestRounds.put(player.getUniqueId(), duration);
            }
            updateTop3List();

            new Thread(() -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Update stats");
                out.writeUTF(player.getUniqueId().toString());
                out.writeUTF(proxyPlayer.getGlobalXP() + "");
                out.writeUTF(proxyPlayer.getGlobalCoins() + "");
                player.sendPluginMessage(Main.getInstance(), "spigot:instance", out.toByteArray());
            }).start();
        } else {
            player.playSound(player.getLocation(), Sound.BAT_DEATH, 3, 2);
        }

        userHandler.setScoreboard(player);
    }

    public boolean isInRound(UUID uuid) {
        return playersInRound.contains(uuid);
    }

    private void startRunnable() {
        if (runnable != null) runnable.cancel();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : playersInRound) {
                    userHandler.setScoreboard(Bukkit.getPlayer(uuid));
                }
            }
        };
        runnable.runTaskTimer(Main.getInstance(), 0, 1);
    }

    public void addToQueue(Player player) {
        PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());
        Location location = getRandomSpawnLocation();
        if (location == null) {
            playerObj.setInQueue(true);

            for (Player all : Bukkit.getOnlinePlayers()) {
                ProxyPlayer allProxy = userHandler.getProxyPlayer(player.getUniqueId());
                if (playerObj.isVanished() && !allProxy.hasPermission("perm.team")) {
                    all.hidePlayer(player);
                } else {
                    all.showPlayer(player);
                }
            }

            player.teleport(new Location(Bukkit.getWorld("Arena"), 11.5, 97, 25.5));
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->{
                player.setGameMode(GameMode.SPECTATOR);
            }, 5);
            player.sendMessage(Main.PREFIX + "§cThere are currently not enough places available. You are in the queue and will be teleported automatically...");
        } else {
            playerObj.setSpawn(location);
            player.teleport(playerObj.getSpawn());
            player.setAllowFlight(false);
            player.setGameMode(GameMode.SURVIVAL);
            takenLocations.add(location);
        }
    }

    public void checkQueuePlayers() {
        for (PlayerObj playerObj : userHandler.getUserList().values()) {
            if (!playerObj.isInQueue()) continue;
            Player player = Bukkit.getPlayer(playerObj.getUuid());
            if (player == null) continue;
            Location loc = getRandomSpawnLocation();
            if (loc == null) break;
            playerObj.setSpawn(loc);
            player.teleport(playerObj.getSpawn());
            player.setGameMode(GameMode.SURVIVAL);
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 3, 2);
            takenLocations.add(loc);
            player.sendMessage(Main.PREFIX + "§aA seat has become available. You are now ready to go!");
        }
    }

    public Location getRandomSpawnLocation() {
        double startX = 550.5;
        double y = 67;
        double z = 0.5;
        double endX = -527.5;

        double spawnDistance = 22.0;

        double totalDistance = Math.abs(endX - startX);
        int numberOfSpawns = (int) (totalDistance / spawnDistance) + 1;

        if (takenLocations.size() >= numberOfSpawns) return null;
        Location loc = null;

        while (loc == null || takenLocations.contains(loc)) {
            Random random = new Random();
            int spawnIndex = random.nextInt(numberOfSpawns);
            double randomX = startX - spawnIndex * spawnDistance;
            loc = new Location(Bukkit.getWorld("Arena"), randomX, y, z);
        }
        return loc;
    }

    public List<Location> getTakenLocations() {
        return takenLocations;
    }

    public static RoundHandler getInstance() {
        if (instance == null) instance = new RoundHandler();
        return instance;
    }

    public void updateTop3List() {
        List<String> result = bestRounds.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .map(entry -> {
                    UUID uuid = entry.getKey();
                    Long value = entry.getValue();
                    String displayName = userHandler.getProxyPlayer(uuid).getDisplayRank().getColorCode() + userHandler.getProxyPlayer(uuid).getUsername();
                    return displayName + " §7- §e" + String.format("%.2f", value / 1000.0);
                })
                .collect(Collectors.toList());

        while (result.size() < 3) {
            result.add("---");
        }

        top3List = result;
    }

    public List<String> getTop3Strings() {
        if (top3List.isEmpty()) updateTop3List();
        return top3List;
    }

    public boolean isValid(long millis) {
        return millis > 4500;
    }
}
