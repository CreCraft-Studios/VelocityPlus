package com.crecraftstudios.velocityplus;

import com.crecraftstudios.velocityplus.commands.*;
import com.crecraftstudios.velocityplus.events.LoginEvents;
import com.crecraftstudios.velocityplus.events.ServerEvents;
import com.crecraftstudios.velocityplus.json.Bans;
import com.crecraftstudios.velocityplus.json.Config;
import com.crecraftstudios.velocityplus.json.Messages;
import com.crecraftstudios.velocityplus.json.Whitelist;
import com.crecraftstudios.velocityplus.network.HttpServer;
import com.crecraftstudios.velocityplus.utils.ServerUtils;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import com.oneonlybob.docker.Docker;

import java.nio.file.Path;
import java.util.HashSet;

@Plugin(id = "velocityplus", name = "VelocityPlus", version = BuildConstants.VERSION, authors = {"CoolBoy376", "OneOnlyBob"})
public class VelocityPlus {
    private final HashSet<String> onlineServers = new HashSet<>();

    public final ProxyServer proxy;
    public final Logger logger;

    /// Use this to call load/save. If you want the actual config data, call config()
    public final Config config;
    /// Use this to call whitelist functions. If you want the actual whitelist data, call whitelist()
    public final Whitelist whitelist;
    public final Messages messages;
    public final Bans bans;
    public final QueueManager queueManager;

    public final Path directory;
    private final Maintenance maintenance;

    private static VelocityPlus instance;

    @Inject
    public VelocityPlus(ProxyServer proxy, Logger logger, @DataDirectory Path dir) {
        this.proxy =proxy;
        this.logger=logger;

        instance=this;

        this.config = new Config();
        this.directory=dir;

        this.config.load();

        this.whitelist = new Whitelist();
        this.whitelist.load();

        this.messages = new Messages();
        this.messages.load();

        this.maintenance=new Maintenance();

        this.bans = new Bans();

        this.queueManager = new QueueManager();
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        this.proxy.getEventManager().register(this, new LoginEvents());
        //this.proxy.getEventManager().register(this, new ProxyEvents()); //<--planned for removal

        if (config().get("webserver-enabled").getAsBoolean()) {
            HttpServer httpServer = new HttpServer();
            httpServer.start();
        }

        if (config().get("docker-socket-enabled").getAsBoolean()) {
            Docker.init();
            this.proxy.getEventManager().register(this, new ServerEvents());
        }

        ServerUtils.pingAllRegisteredServers();
    }

    public static VelocityPlus get() {
        return instance;
    }

    public boolean isServerOnline(String serverName) {
        return this.onlineServers.contains(serverName);
    }

    public void serverIsOffline(String serverName) {
        this.logger.info("{} is set to offline", serverName);
        this.onlineServers.remove(serverName);
    }

    public void serverIsOnline(String serverName) {
        this.logger.info("{} is set to online", serverName);
        this.onlineServers.add(serverName);
        this.queueManager.moveNeededPlayers(serverName);
    }

    public boolean inMaintenanceMode() {
        return this.maintenance.inMaintenance();
    }

    public void enterMaintenanceMode(int time) {
        this.maintenance.enter(time);
    }

    public void exitMaintenanceMode() {
        this.maintenance.exit();
    }

    public JsonObject config() {
        return this.config.get();
    }

    public JsonObject whitelist() {
        return this.whitelist.get();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.registerCommands();
    }

    private void registerCommands() {
        CommandManager commandManager = this.proxy.getCommandManager();

        this.registerCommand(commandManager, FindCommand.createCommand(this.proxy), "find");
        this.registerCommand(commandManager, AlertCommand.createCommand(this.proxy), "alert");
        this.registerCommand(commandManager, MessageCommand.createCommand(this.proxy), "pmsg");
        this.registerCommand(commandManager, SetLobbyCommand.createCommand(this.proxy), "set-lobby");
        this.registerCommand(commandManager, HubCommand.createCommand(this.proxy), "hub", "lobby", "home");
        this.registerCommand(commandManager, WhitelistCommand.createCommand(this.proxy), "global-whitelist", "gwhitelist", "proxy-whitelist", "pwhitelist");
        this.registerCommand(commandManager, MaintenanceCommand.createCommand(), "maintenance", "main");
        this.registerCommand(commandManager, BanCommand.createCommand(this.proxy), "vban", "proxy-ban", "velocity-ban", "pban");
        this.registerCommand(commandManager, UnbanCommand.createCommand(this.proxy), "vunban", "proxy-unban", "velocity-unban", "punban");
    }

    private void registerCommand(CommandManager manager, BrigadierCommand commandObject, String command, String... aliases) {
        CommandMeta meta = manager.metaBuilder(command).aliases(aliases).plugin(this).build();
        manager.register(meta, commandObject);
    }
}