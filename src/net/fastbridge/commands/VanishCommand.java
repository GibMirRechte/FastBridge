package net.fastbridge.commands;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.main.Main;
import net.fastbridge.utils.PlayerObj;
import net.fastbridge.utils.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishCommand implements CommandExecutor, Listener {

    UserHandler userHandler = UserHandler.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String args[]) {
        if (!(sender instanceof Player player)) return true;

        ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(player.getUniqueId());
        PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());

        if (!proxyPlayer.getRank().isTeam()) {
            player.sendMessage(Main.PREFIX + "§cYou do not have the required permission for this.");
            return true;
        }

        if (args.length == 0 || !proxyPlayer.hasPermission("perm.command.vanish")) {
            playerObj.setVanished(!playerObj.isVanished());
            if (playerObj.isVanished()) {
                player.setAllowFlight(true);
                player.sendMessage(Main.PREFIX + "§aYou enabled the vanishmode.");
            } else {
                player.sendMessage(Main.PREFIX + "§cYou disabled the vanishmode.");
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (playerObj.isVanished()) {
                    all.hidePlayer(player);
                } else {
                    all.showPlayer(player);
                }
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Main.PREFIX + "§cThis player is not online.");
            } else {
                PlayerObj targetPlayer = userHandler.getPlayer(target.getUniqueId());
                ProxyPlayer targetProxy = userHandler.getProxyPlayer(targetPlayer.getUuid());

                targetPlayer.setVanished(!targetPlayer.isVanished());
                if (targetPlayer.isVanished()) {
                    target.sendMessage(Main.PREFIX + "§aYour vanishmode was enabled.");
                    player.sendMessage(Main.PREFIX + "§aYou enabled the vanishmode for " + targetProxy.getDisplayName() + "§a.");
                } else {
                    target.sendMessage(Main.PREFIX + "§cYour vanishmode was disabled.");
                    player.sendMessage(Main.PREFIX + "§cYou disabled the vanishmode for " + targetProxy.getDisplayName() + "§a.");
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (targetPlayer.isVanished()) {
                        all.hidePlayer(target);
                    } else {
                        all.showPlayer(target);
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            PlayerObj target = userHandler.getPlayer(all.getUniqueId());
            if (target.isVanished()) {
                event.getPlayer().hidePlayer(all);
            } else {
                event.getPlayer().showPlayer(all);
            }
        }
    }

}
