package net.fastbridge.listener;

import net.fastbridge.handler.RoundHandler;
import net.fastbridge.handler.UserHandler;
import net.fastbridge.main.Main;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnMove implements Listener {

    UserHandler userHandler = UserHandler.getInstance();
    RoundHandler roundHandler = RoundHandler.getInstance();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
        Location spawn = playerObj.getSpawn();

        if (event.getPlayer().getLocation().getY() <= 30) {
            handleDeath(event.getPlayer());
            return;
        }

        if (spawn == null) return;

        if (!playerObj.isWithinBorder(event.getPlayer().getLocation()) && !playerObj.isVanished() && !playerObj.isInQueue()) {
            if (playerObj.isBuildmode()) return;
            event.getPlayer().teleport(playerObj.getSpawn());
            event.getPlayer().sendMessage(Main.PREFIX + "§cPlease dont leave your area.");
            roundHandler.stopRound(event.getPlayer(), false);
        }
    }

    public void handleDeath(Player player) {
        PlayerObj playerObj = userHandler.getPlayer(player.getUniqueId());
        RoundHandler.getInstance().stopRound(player, false);
        if (playerObj.getSpawn() == null) {
            player.teleport(new Location(Bukkit.getWorld("Arena"), 0.5, 80, 25.5));
        }
    }
}
