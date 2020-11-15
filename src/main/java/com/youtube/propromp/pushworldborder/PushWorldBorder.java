package com.youtube.propromp.pushworldborder;

import com.youtube.propromp.pushworldborder.commands.PWBCommand;
import com.youtube.propromp.pushworldborder.events.PWBPlayerMoveEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.logging.Logger;

public final class PushWorldBorder extends JavaPlugin {
    public static Plugin plugin;
    public static Logger logger;

    public static BorderBehaviour behaviour = BorderBehaviour.NONE;
    public static Player leader;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        logger.info("Hi!");

        //イベント登録
        getServer().getPluginManager().registerEvents(new PWBPlayerMoveEvent(), this);

        //コマンド登録
        Optional.ofNullable(this.getCommand("pwb")).ifPresent(e -> e.setExecutor(new PWBCommand()));
    }
}
