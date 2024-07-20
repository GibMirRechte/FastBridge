package net.fastbridge.listener;

import net.fastbridge.handler.UserHandler;
import net.fastbridge.utils.ProxyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnChatMessage implements Listener {

    UserHandler userHandler = UserHandler.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ProxyPlayer proxyPlayer = userHandler.getProxyPlayer(event.getPlayer().getUniqueId());
        String msg = event.getMessage();
        if (proxyPlayer.getDisplayRank().isTeam()) msg = "§f" + msg;

        event.setFormat("§8[§7" + proxyPlayer.getLevel() + "✰§8] " + proxyPlayer.getTabName() + " §8» §7" + msg.replace("%", "%%"));
    }
}
