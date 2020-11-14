package com.youtube.propromp.pushworldborder;

import com.youtube.propromp.pushworldborder.commands.PWBCommand;
import com.youtube.propromp.pushworldborder.events.PWBPlayerMoveEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PushWorldBorder extends JavaPlugin {
    public static FileConfiguration config;
    @Override
    public void onEnable() {
        this.getLogger().info("Hi!");

        //イベント登録
        new PWBPlayerMoveEvent(this);

        //コマンド登録
        try {
            this.getCommand("pwb").setExecutor(new PWBCommand());
        } catch(NullPointerException e){
            e.printStackTrace();
        }

        //コンフィグ読み込み
        saveDefaultConfig();
        config = getConfig();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
