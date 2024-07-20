package net.fastbridge.listener;

import net.fastbridge.handler.RoundHandler;
import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.PlayerObj;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnBuild implements Listener {

    UserHandler userHandler = UserHandler.getInstance();
    RoundHandler roundHandler = RoundHandler.getInstance();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
        if(playerObj.isBuildmode()) return;
        playerObj.getPlacedBlocks().add(event.getBlock().getLocation());
        if(!roundHandler.isInRound(event.getPlayer().getUniqueId())){
            roundHandler.startRound(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
        if (!playerObj.isBuildmode() && !playerObj.getPlacedBlocks().contains(event.getBlock().getLocation())) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getData().getItemType().equals(Material.WATER_BUCKET) ||
                event.getPlayer().getItemInHand().getData().getItemType().equals(Material.LAVA_BUCKET)) {
            PlayerObj playerObj = userHandler.getPlayer(event.getPlayer().getUniqueId());
            if (!playerObj.isBuildmode()) event.setCancelled(true);
        }
    }

}
