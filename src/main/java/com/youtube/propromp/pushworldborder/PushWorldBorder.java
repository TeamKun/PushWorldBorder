package com.youtube.propromp.pushworldborder;

import com.youtube.propromp.pushworldborder.events.PWBPlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PushWorldBorder extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Hi!");

        //イベント登録
        new PWBPlayerMoveEvent(this);

        //コマンド登録
//        getCommand("pwb").setExecutor();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
