package com.youtube.propromp.pushworldborder.events;

import com.youtube.propromp.pushworldborder.PushWorldBorder;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class PWBPlayerMoveEvent implements Listener {

    public PWBPlayerMoveEvent(Plugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e){
        WorldBorder worldborder = e.getPlayer().getWorld().getWorldBorder();
        if(e.getPlayer().getLocation().distance(worldborder.getCenter()) > worldborder.getSize()/2 + 1.0) {
            e.getPlayer().getWorld().getWorldBorder().setSize(e.getPlayer().getWorld().getWorldBorder().getSize() + PushWorldBorder.config.getDouble("speed"));
        }
    }
}
