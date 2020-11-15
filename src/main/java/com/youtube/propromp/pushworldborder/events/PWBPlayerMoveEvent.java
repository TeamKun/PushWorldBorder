package com.youtube.propromp.pushworldborder.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;

public class PWBPlayerMoveEvent implements Listener {
    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = player.getWorld();
        WorldBorder worldborder = world.getWorldBorder();

        double size = worldborder.getSize() / 2 - .85;
        BoundingBox box = BoundingBox.of(worldborder.getCenter(), size, 0, size);
        if (!box.contains(location.toVector())) {
            player.getWorld().getPlayers().forEach(e -> box.union(e.getLocation()));
            worldborder.setCenter(box.getCenter().toLocation(world));
        }
    }
}
