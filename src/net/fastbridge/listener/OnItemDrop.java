package net.fastbridge.listener;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OnItemDrop implements Listener {

    UserHandler userHandler = UserHandler.getInstance();

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
        if (!playerObj.isBuildmode()) {
            event.setCancelled(true);
        }
    }
}
