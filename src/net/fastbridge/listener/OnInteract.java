package net.fastbridge.listener;

import net.fastbridge.handler.RoundHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnInteract implements Listener {

    RoundHandler roundHandler = RoundHandler.getInstance();

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction().equals(Action.PHYSICAL) && roundHandler.isInRound(event.getPlayer().getUniqueId())){
            roundHandler.stopRound(event.getPlayer(), true);
        }
    }
}
