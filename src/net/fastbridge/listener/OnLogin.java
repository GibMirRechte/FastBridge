package net.fastbridge.listener;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class OnLogin implements Listener {

    UserHandler userHandler = UserHandler.getInstance();

    @EventHandler
    public void onPreLogin(PlayerLoginEvent event) {
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());

        if (playerObj == null) {
            userHandler.createPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }
}
