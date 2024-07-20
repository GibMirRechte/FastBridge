package net.fastbridge.handler;

import net.fastbridge.utils.Rank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class RankHandler {

    private static RankHandler instance;
    private final HashMap<String, Rank> ranks = new HashMap<>();
    ProxyMySQLHandler proxyMySQLHandler = ProxyMySQLHandler.getInstance();

    public static RankHandler getInstance() {
        if (instance == null) instance = new RankHandler();
        return instance;
    }

    public void loadRanks() {
        ranks.clear();
        try {
            PreparedStatement statement = proxyMySQLHandler.getConnection().prepareStatement("SELECT * FROM Ranks ORDER BY Sort ASC");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String fullname = resultSet.getString("Fullname");
                String tabName = resultSet.getString("TabName");
                String scoreboardTeam = resultSet.getString("ScoreboardTeam");
                String colorCode = resultSet.getString("ColorCode");
                boolean team = resultSet.getBoolean("Team");
                boolean testTeam = resultSet.getBoolean("TestTeam");
                boolean canBan = resultSet.getBoolean("CanBan");
                boolean canMute = resultSet.getBoolean("CanMute");
                int maxFriends = resultSet.getInt("MaxFriends");
                int sort = resultSet.getInt("Sort");
                String permissions = resultSet.getString("Permissions");

                Rank rang = new Rank(sort, name, fullname, tabName, scoreboardTeam, colorCode, team, testTeam, canBan, canMute, maxFriends, permissions);
                ranks.put(name, rang);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Rank> getRanks() {
        return ranks;
    }

    public Rank getRank(String name) {
        for (String r : ranks.keySet()) {
            if (r.equalsIgnoreCase(name)) {
                return ranks.get(r);
            }
        }
        return null;
    }
}