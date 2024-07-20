package net.fastbridge.listener;

import net.fastbridge.handler.RoundHandler;
import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnQuit implements Listener {

    UserHandler userHandler = UserHandler.getInstance();
    RoundHandler roundHandler = RoundHandler.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        roundHandler.stopRound(event.getPlayer(), false);
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
        playerObj.setBuildmode(false);
        playerObj.setVanished(false);
        playerObj.setInQueue(false);
        roundHandler.getTakenLocations().remove(playerObj.getSpawn());
        playerObj.setSpawn(null);
        roundHandler.checkQueuePlayers();
        userHandler.updatePlayer(playerObj);
        userHandler.getProxyUserList().remove(event.getPlayer().getUniqueId());
    }
}
