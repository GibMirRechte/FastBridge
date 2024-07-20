package net.fastbridge.listener;

import net.fastbridge.handler.RoundHandler;
import net.fastbridge.handler.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    private final UserHandler userHandler = UserHandler.getInstance();
    RoundHandler roundHandler = RoundHandler.getInstance();

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        setupPlayer(event);
    }

    private void setupPlayer(PlayerJoinEvent event) {
        userHandler.setupInventory(event.getPlayer());
        roundHandler.addToQueue(event.getPlayer());
        userHandler.setScoreboard(event.getPlayer());
        System.out.println(event.getPlayer().isFlying());
    }
}