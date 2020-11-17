package com.youtube.propromp.pushworldborder.events;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.youtube.propromp.pushworldborder.BorderBehaviour;
import com.youtube.propromp.pushworldborder.PushWorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PWBPlayerMoveEvent implements Listener {
    private final WorldBorderApi borderApi = BorderAPI.getApi();
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    private List<Player> getTeamPlayers(Player player) {
        return Optional.ofNullable(scoreboard.getEntryTeam(player.getName()))
                .map(e -> e.getEntries().stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .filter(p -> Objects.equals(p.getWorld(), player.getWorld()))
                        .collect(Collectors.toList())
                )
                .orElseGet(() -> new ArrayList<>(player.getWorld().getPlayers()));
    }

    private static Vector toVector(Position position) {
        return new Vector(position.getX(), 0, position.getZ());
    }

    private static Position toPosition(Vector position) {
        return new Position(position.getX(), position.getZ());
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        if (PushWorldBorder.behaviour == BorderBehaviour.NONE)
            return;

        Player player = event.getPlayer();
        IWorldBorder worldborder = borderApi.getWorldBorder(player);
        List<Player> members = getTeamPlayers(player);

        Location location = player.getLocation();
        Vector vector = location.toVector();
        World world = player.getWorld();

        double size = worldborder.getSize() / 2 - .85;
        BoundingBox box = BoundingBox.of(toVector(worldborder.getCenter()), size, Float.MAX_VALUE, size);
        if (!box.contains(vector)) {
            switch (PushWorldBorder.behaviour) {
                case EVERYONE_IN_BORDER: {
                    members.forEach(e -> box.union(e.getLocation()));
                    worldborder.setCenter(toPosition(box.getCenter()));
                }
                break;

                case USE_MOVING: {
                    members.forEach(e -> box.union(e.getLocation()));
                    worldborder.setCenter(toPosition(box.getCenter()));

                    moveOtherPlayers(Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)), world, size, box, 0);
                }
                break;

                case MAJOR_HALF: {
                    int cx = compareRange(vector.getX(), box.getMinX(), box.getMaxX());
                    int cz = compareRange(vector.getZ(), box.getMinZ(), box.getMaxZ());

                    List<Player> players = members;
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
                        worldborder.setCenter(toPosition(box.getCenter()));

                        moveOtherPlayers(players.stream().filter(p -> !pushing.contains(p)), world, size, box, 2);
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
                    box.union(location);
                    worldborder.setCenter(toPosition(box.getCenter()));

                    moveOtherPlayers(members.stream().filter(p -> !p.equals(player)), world, size, box, 2);
                }
                break;
            }
        }
    }

    private void moveOtherPlayers(Stream<? extends Player> stream, World world, double size, BoundingBox box,
                                  double tpThreshold) {
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
