package net.fastbridge.commands;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.main.Main;
import net.fastbridge.utils.PlayerObj;
import net.fastbridge.utils.ProxyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    UserHandler userHandler = UserHandler.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String args[]) {
        if (!(sender instanceof Player player)) return true;

        ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(player.getUniqueId());

        if (!proxyPlayer.hasPermission("perm.command.build")) {
            player.sendMessage(Main.PREFIX + "§cYou do not have the required permission for this.");
            return true;
        }

        if (args.length != 1) {
            PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());
            playerObj.setBuildmode(!playerObj.isBuildmode());

            if (playerObj.isBuildmode()) {
                player.setGameMode(GameMode.CREATIVE);
                player.getInventory().clear();
                player.sendMessage(Main.PREFIX + "§aYou enabled the buildmode.");
            } else {
                player.setGameMode(GameMode.SURVIVAL);
                userHandler.setupInventory(player);
                player.sendMessage(Main.PREFIX + "§cYou disabled the buildmode.");
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Main.PREFIX + "§cThis player is not online.");
            } else {
                PlayerObj playerObj = userHandler.getPlayer(target.getUniqueId());
                ProxyPlayer targetProxyPlayer = userHandler.getProxyPlayer(target.getUniqueId());

                playerObj.setBuildmode(!playerObj.isBuildmode());

                if (playerObj.isBuildmode()) {
                    target.sendMessage(Main.PREFIX + "§aYour buildmode was enabled.");
                    player.sendMessage(Main.PREFIX + "§aYou enabled the buildmode for " + targetProxyPlayer.getDisplayName() + "§a.");
                } else {
                    target.sendMessage(Main.PREFIX + "§cYour buildmode was disabled.");
                    player.sendMessage(Main.PREFIX + "§cYou disabled the buildmode for " + targetProxyPlayer.getDisplayName() + "§a.");
                }
            }
        }

        return false;
    }

}
