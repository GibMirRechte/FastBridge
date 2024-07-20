package net.fastbridge.handler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.fastbridge.main.Main;
import net.fastbridge.utils.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.UUID;

public class PluginMessageReceiver implements PluginMessageListener {

    UserHandler userHandler = UserHandler.getInstance();
    RankHandler rankHandler = RankHandler.getInstance();
    RoundHandler roundHandler = RoundHandler.getInstance();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("proxy:instance")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("Update rank")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String rankString = in.readUTF();
            String displayRankString = in.readUTF();

            if (Bukkit.getPlayer(uuid) != null) {
                userHandler.getProxyPlayer(uuid).updateRank(rankString);
                userHandler.getProxyPlayer(uuid).setDisplayRank(rankHandler.getRank(displayRankString));

                roundHandler.updateTop3List();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    userHandler.setScoreboard(all);
                }

                userHandler.setupInventory(Bukkit.getPlayer(uuid));
            }
        } else if (subChannel.equalsIgnoreCase("Update stats")) {
            UUID uuid = UUID.fromString(in.readUTF());
            if (Bukkit.getPlayer(uuid) == null) return;

            ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(uuid);
            long exp = Long.parseLong(in.readUTF());
            long coins = Long.parseLong(in.readUTF());

            proxyPlayer.setGlobalXP(exp);
            proxyPlayer.setGlobalCoins(coins);

            userHandler.setScoreboard(Bukkit.getPlayer(uuid));
        } else if (subChannel.equalsIgnoreCase("Nick player")) {
            UUID uuid = UUID.fromString(in.readUTF());
            String name = in.readUTF();
            Player target = Bukkit.getPlayer(uuid);

            if (target == null) return;

            ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(uuid);
            if (name.equalsIgnoreCase("null")) {
                proxyPlayer.setNickedName("");

                NickAPI.resetNick(target);
                NickAPI.resetSkin(target);
                NickAPI.resetGameProfileName(target);
                NickAPI.resetUniqueId(target);
                NickAPI.refreshPlayer(target);

                roundHandler.updateTop3List();

                for (Player all : Bukkit.getOnlinePlayers()) {
                    userHandler.setScoreboard(all);
                }
            } else {
                proxyPlayer.setNickedName(name);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        NickAPI.nick(target, name);
                        NickAPI.setSkin(target, name);
                        NickAPI.setGameProfileName(target, name);
                        NickAPI.setUniqueId(target, UUID.randomUUID());
                        NickAPI.refreshPlayer(target);

                        roundHandler.updateTop3List();

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            userHandler.setScoreboard(all);
                        }
                    }
                }.runTaskLater(Main.getInstance(), 1);

            }
        }
    }
}
