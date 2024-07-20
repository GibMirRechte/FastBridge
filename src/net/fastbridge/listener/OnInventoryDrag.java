package net.fastbridge.listener;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

public class OnInventoryDrag implements Listener {

    UserHandler userHandler = UserHandler.getInstance();

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        PlayerObj playerObj = userHandler.getPlayer((event.getWhoClicked()).getUniqueId());
        if (!playerObj.isBuildmode()) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.CRAFTING) ||
                event.getInventory().getType().equals(InventoryType.CREATIVE)) {
            PlayerObj playerObj = userHandler.getPlayer(event.getWhoClicked().getUniqueId());
            if (!playerObj.isBuildmode()) event.setCancelled(true);
        }
    }
}
