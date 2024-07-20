package net.fastbridge.handler;

import net.fastbridge.utils.PlayerObj;
import net.fastbridge.utils.ProxyPlayer;
import net.fastbridge.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class UserHandler {

    static HashMap<UUID, PlayerObj> userList = new HashMap<>();
    static HashMap<UUID, ProxyPlayer> proxyUserList = new HashMap<>();
    static UserHandler instance;
    MySQLHandler mySQLHandler = MySQLHandler.getInstance();
    ProxyMySQLHandler proxyMySQLHandler = ProxyMySQLHandler.getInstance();
    public static UserHandler getInstance() {
        if (instance == null) instance = new UserHandler();
        return instance;
    }

    public HashMap<UUID, PlayerObj> getUserList() {
        return userList;
    }

    public PlayerObj getPlayer(UUID uuid) {
        if (userList.containsKey(uuid)) {
            return userList.get(uuid);
        }

        try {
            PreparedStatement preparedStatement = mySQLHandler.getConnection().prepareStatement("SELECT * FROM Players WHERE UUID=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("Username");
                long bestRound = resultSet.getLong("BestRound");

                preparedStatement.close();
                resultSet.close();

                PlayerObj playerObj = new PlayerObj(uuid, username, bestRound);
                userList.put(uuid, playerObj);
                return playerObj;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    public void setupInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ItemStack blocks = new ItemStack(Material.SANDSTONE, 64);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);

        player.getInventory().setItem(0, blocks);
        player.getInventory().setItem(1, pickaxe);
    }

    public PlayerObj createPlayer(UUID uuid, String username) {
        PlayerObj playerObj = new PlayerObj(uuid, username,0);

        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = mySQLHandler.getConnection().prepareStatement("INSERT INTO Players(UUID,Username,BestRound) VALUES (?,?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, username.toString());
                preparedStatement.setLong(3,0);
                preparedStatement.execute();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }).start();
        return playerObj;
    }

    public void updatePlayer(PlayerObj playerObj) {
        new Thread(() -> {
            try {
                PreparedStatement preparedStatement = mySQLHandler.getConnection().prepareStatement("UPDATE Players SET Username=?, BestRound=? WHERE UUID=?");
                preparedStatement.setString(1, playerObj.getUsername());
                preparedStatement.setLong(2, playerObj.getBestRound());
                preparedStatement.setString(3, playerObj.getUuid().toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }).start();
    }

    public ProxyPlayer getProxyPlayer(UUID uuid) {
        if (proxyUserList.containsKey(uuid)) {
            return proxyUserList.get(uuid);
        }

        try {
            PreparedStatement preparedStatement = proxyMySQLHandler.getConnection().prepareStatement("SELECT * FROM ProxyPlayers WHERE UUID=?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("Username");
                String rankString = resultSet.getString("RankString");
                long xp = resultSet.getLong("GlobalXP");
                long coins = resultSet.getLong("GlobalCoins");
                String displayRank = resultSet.getString("DisplayRank");
                boolean autoNick = resultSet.getBoolean("AutoNick");
                boolean autoVanish = resultSet.getBoolean("AutoVanish");

                preparedStatement.close();
                resultSet.close();

                ProxyPlayer proxyPlayer = new ProxyPlayer(uuid, name, rankString, displayRank,xp, coins, autoNick, autoVanish);
                proxyUserList.put(uuid, proxyPlayer);
                return proxyPlayer;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        PlayerObj playerObj = getPlayer(player.getUniqueId());

        for (Rank rank : RankHandler.getInstance().getRanks().values()) {
            if (rank.getTabName().length() == 2) {
                scoreboard.registerNewTeam(rank.getScoreboardTeam()).setPrefix(rank.getColorCode());
                continue;
            }
            scoreboard.registerNewTeam(rank.getScoreboardTeam()).setPrefix(rank.getTabName() + " §8▏ " + rank.getColorCode());
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            Rank rank = getProxyPlayer(all.getUniqueId()).getDisplayRank();
            scoreboard.getTeam(rank.getScoreboardTeam()).addPlayer(all);
            all.setPlayerListName(scoreboard.getTeam(rank.getScoreboardTeam()).getPrefix() + all.getName());

            if (all == player) continue;
            Scoreboard s = all.getScoreboard();
            s.getTeam(getProxyPlayer(player.getUniqueId()).getDisplayRank().getScoreboardTeam()).addPlayer(player);
            all.setScoreboard(s);
        }

        if (player.getScoreboard().equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard()))
            player.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        Objective objective = scoreboard.getObjective(player.getName()) == null ? scoreboard.registerNewObjective(player.getName(), "dummy") : scoreboard.getObjective(player.getName());

        objective.setDisplayName("§f§lFastBridge");
        replaceScore(objective, 11, "§b");
        replaceScore(objective, 10, "§7▎ §fTop 3 (Uptime)§8:");
        replaceScore(objective, 9, "§7▎ §8- §e" + RoundHandler.getInstance().getTop3Strings().get(0));
        replaceScore(objective, 8, "§7▎ §8- §e" + RoundHandler.getInstance().getTop3Strings().get(1));
        replaceScore(objective, 7, "§7▎ §8- §e" + RoundHandler.getInstance().getTop3Strings().get(2));
        replaceScore(objective, 6, "§4");
        replaceScore(objective, 5, "§7▎ §fBest time§8: §e" + String.format("%.2f", playerObj.getBestRound()/1000.0));
        replaceScore(objective, 4, "§1");
        replaceScore(objective, 3, "§7▎ §fPlaced Blocks§8: §b" + playerObj.getPlacedBlocks().size());
        if(RoundHandler.getInstance().isInRound(player.getUniqueId())){
            replaceScore(objective, 2, "§7▎ §fCurrent Time§8: §b" + String.format("%.2f", (System.currentTimeMillis() - playerObj.getStarted()) / 1000.0));
        }else{
            replaceScore(objective, 2, "§7▎ §fCurrent Time§8: §b0,00" );
        }
        replaceScore(objective, 1, "§0");


        if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR)
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    private String getEntryFromScore(Objective o, int score) {
        if (o == null) return null;

        if (!hasScoreTaken(o, score)) return null;
        for (String s : o.getScoreboard().getEntries()) {
            if (o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
        }
        return null;
    }

    private boolean hasScoreTaken(Objective o, int score) {
        for (String s : o.getScoreboard().getEntries()) {
            if (o.getScore(s).getScore() == score) return true;
        }

        return false;
    }

    private void replaceScore(Objective o, int score, String name) {
        if (hasScoreTaken(o, score)) {
            if (getEntryFromScore(o, score).equalsIgnoreCase(name)) return;
            if (!(getEntryFromScore(o, score).equalsIgnoreCase(name)))
                o.getScoreboard().resetScores(getEntryFromScore(o, score));
        }
        o.getScore(name).setScore(score);
    }

    public HashMap<UUID, ProxyPlayer> getProxyUserList() {
        return proxyUserList;
    }
}
