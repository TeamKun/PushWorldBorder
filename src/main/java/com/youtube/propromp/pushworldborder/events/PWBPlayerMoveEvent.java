package com.youtube.propromp.pushworldborder.events;

import net.minecraft.server.v1_15_R1.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import net.minecraft.server.v1_15_R1.EntityPlayer;
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
            e.getPlayer().getWorld().getWorldBorder().setSize(e.getPlayer().getWorld().getWorldBorder().getSize() + 0.1);
        }
    }
}
