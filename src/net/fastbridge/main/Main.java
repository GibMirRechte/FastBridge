package net.fastbridge.main;

import net.fastbridge.commands.BuildCommand;
import net.fastbridge.commands.VanishCommand;
import net.fastbridge.handler.MySQLHandler;
import net.fastbridge.handler.PluginMessageReceiver;
import net.fastbridge.handler.ProxyMySQLHandler;
import net.fastbridge.handler.RankHandler;
import net.fastbridge.listener.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static String PREFIX = "§8[§fFastBridge§8] §7";
    private static Main instance;

    private static MySQLHandler mySQLHandler = MySQLHandler.getInstance();
    private static ProxyMySQLHandler proxyMySQLHandler = ProxyMySQLHandler.getInstance();
    private static RankHandler rankHandler = RankHandler.getInstance();

    @Override
    public void onEnable() {
        instance = this;
        mySQLHandler.connect();
        proxyMySQLHandler.connect();

        rankHandler.loadRanks();

        registerEvents();
        registerCommands();

        getServer().getMessenger().registerIncomingPluginChannel(this, "proxy:instance", new PluginMessageReceiver());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "spigot:instance");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void registerCommands(){
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("build").setExecutor(new BuildCommand());
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnQuit(), this);
        getServer().getPluginManager().registerEvents(new OnBuild(), this);
        getServer().getPluginManager().registerEvents(new OnMove(), this);
        getServer().getPluginManager().registerEvents(new OnLogin(), this);
        getServer().getPluginManager().registerEvents(new OnDeath(), this);
        getServer().getPluginManager().registerEvents(new OnInteract(), this);
        getServer().getPluginManager().registerEvents(new OnDamage(), this);
        getServer().getPluginManager().registerEvents(new OnItemDrop(), this);
        getServer().getPluginManager().registerEvents(new OnItemPickup(), this);
        getServer().getPluginManager().registerEvents(new OnLeaveDecay(), this);
        getServer().getPluginManager().registerEvents(new OnFoodChange(), this);
        getServer().getPluginManager().registerEvents(new OnChatMessage(), this);
        getServer().getPluginManager().registerEvents(new OnInventoryDrag(), this);
        getServer().getPluginManager().registerEvents(new VanishCommand(), this);
        getServer().getPluginManager().registerEvents(new OnWeatherChange(), this);
    }

    public static Main getInstance() {
        return instance;
    }
}
