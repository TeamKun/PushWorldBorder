package com.youtube.propromp.pushworldborder.events;

import com.youtube.propromp.pushworldborder.PushWorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PWBPlayerMoveEvent implements Listener {
    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Vector vector = location.toVector();
        World world = player.getWorld();
        WorldBorder worldborder = world.getWorldBorder();

        switch (PushWorldBorder.behaviour) {
            case EVERYONE_IN_BORDER: {
                double size = worldborder.getSize() / 2 - .85;
                BoundingBox box = BoundingBox.of(worldborder.getCenter(), size, Float.MAX_VALUE, size);
                if (!box.contains(vector)) {
                    player.getWorld().getPlayers().forEach(e -> box.union(e.getLocation()));
                    worldborder.setCenter(box.getCenter().toLocation(world));
                }
            }
            break;

            case USE_MOVING: {
                double size = worldborder.getSize() / 2 - .85;
                BoundingBox box = BoundingBox.of(worldborder.getCenter(), size, Float.MAX_VALUE, size);
                if (!box.contains(vector)) {
                    player.getWorld().getPlayers().forEach(e -> box.union(e.getLocation()));
                    worldborder.setCenter(box.getCenter().toLocation(world));

                    moveOtherPlayers(Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)), world, size, box, 0);
                }
            }
            break;

            case MAJOR_HALF: {
                double size = worldborder.getSize() / 2 - .85;
                BoundingBox box = BoundingBox.of(worldborder.getCenter(), size, Float.MAX_VALUE, size);
                if (!box.contains(vector)) {
                    int cx = compareRange(vector.getX(), box.getMinX(), box.getMaxX());
                    int cz = compareRange(vector.getZ(), box.getMinZ(), box.getMaxZ());

                    List<Player> players = player.getWorld().getPlayers();
                    List<Player> pushing = players.stream()
                            .filter(e -> {
                                Vector v = e.getLocation().toVector();
                                if (box.contains(v))
                                    return false;
                                int vcx = compareRange(v.getX(), box.getMinX(), box.getMaxX());
                                int vcz = compareRange(v.getZ(), box.getMinZ(), box.getMaxZ());
                                int sx = Math.abs(vcx - cx);
                                int sz = Math.abs(vcz - cz);
                                return sx <= 0 && sz <= 1 || sx <= 1 && sz <= 0;
                            })
                            .collect(Collectors.toList());
                    if (pushing.size() > players.size() / 2) {
                        box.union(location);
                        worldborder.setCenter(box.getCenter().toLocation(world));

                        moveOtherPlayers(players.stream().filter(p -> !pushing.contains(p)), world, size, box, 2);
                    }
                }
            }
            break;

            case ONLY_LEADER: {
                if (PushWorldBorder.leader == null)
                    return;
                if (!PushWorldBorder.leader.isOnline())
                    return;
                if (!PushWorldBorder.leader.getUniqueId().equals(player.getUniqueId()))
                    return;
                double size = worldborder.getSize() / 2 - .85;
                BoundingBox box = BoundingBox.of(worldborder.getCenter(), size, Float.MAX_VALUE, size);
                if (!box.contains(vector)) {
                    box.union(location);
                    worldborder.setCenter(box.getCenter().toLocation(world));

                    moveOtherPlayers(player.getWorld().getPlayers().stream().filter(p -> !p.equals(player)), world, size, box, 2);
                }
            }
            break;

        }
    }

    private void moveOtherPlayers(Stream<? extends Player> stream, World world, double size, BoundingBox box, double tpThreshold) {
        BoundingBox boxIn = BoundingBox.of(box.getCenter(), size, Float.MAX_VALUE, size);
        BoundingBox boxOut = boxIn.clone().expand(tpThreshold);
        stream
                .filter(p -> p.getWorld().equals(world))
                .forEach(p -> {
                    Location pLocation = p.getLocation();
                    Vector pVector = pLocation.toVector();
                    if (!boxIn.contains(pVector)) {
                        Vector closest = getClosestPoint(boxIn, pVector);
                        closest.setY(pLocation.getY());
                        if (!boxOut.contains(pVector)) {
                            p.teleport(pLocation.set(closest.getX(), closest.getY(), closest.getZ()));
                        } else {
                            Vector vel = closest.subtract(pVector);
                            vel.setY(p.getVelocity().getY());
                            p.setVelocity(vel);
                        }
                    }
                });
    }

    private static int compareRange(double x, double min, double max) {
        if (x < min)
            return -1;
        if (x > max)
            return 1;
        return 0;
    }

    private static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(x, max));
    }

    private static Vector getClosestPoint(BoundingBox box, Vector point) {
        return new Vector(
                clamp(point.getX(), box.getMinX(), box.getMaxX()),
                clamp(point.getY(), box.getMinY(), box.getMaxY()),
                clamp(point.getZ(), box.getMinZ(), box.getMaxZ())
        );
    }
}
