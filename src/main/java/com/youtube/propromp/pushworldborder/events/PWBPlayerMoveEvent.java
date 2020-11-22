package com.youtube.propromp.pushworldborder.events;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.youtube.propromp.pushworldborder.BorderBehaviour;
import com.youtube.propromp.pushworldborder.PushWorldBorder;
import com.youtube.propromp.pushworldborder.TeamBorder;
import com.youtube.propromp.pushworldborder.VectorUtils;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PWBPlayerMoveEvent implements Listener {
    private static final WorldBorderApi borderApi = BorderAPI.getApi();
    public static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        if (PushWorldBorder.behaviour == BorderBehaviour.NONE)
            return;

        Player player = event.getPlayer();
        World world = player.getWorld();
        IWorldBorder worldBorder = borderApi.getWorldBorder(world);
        IWorldBorder playerBorder = borderApi.getWorldBorder(player);

        List<Player> teamPlayers = TeamBorder.getSameTeamPlayers(player).collect(Collectors.toList());
        Optional<Player> leaderOptional = TeamBorder.getTeamLeader(teamPlayers.stream()).findFirst();
        if (!leaderOptional.isPresent())
            return;
        Player leader = leaderOptional.get();

        Location location = player.getLocation();
        Vector vector = location.toVector();

        double worldBorderSize = worldBorder.getSize();
        if (worldBorderSize != playerBorder.getSize())
            teamPlayers.forEach(p -> borderApi.setBorder(p, worldBorderSize, VectorUtils.toVector(worldBorder.getCenter()).toLocation(world)));

        double size = playerBorder.getSize() / 2 - .85;
        BoundingBox box = BoundingBox.of(VectorUtils.toVector(playerBorder.getCenter()), size, Float.MAX_VALUE, size);
        if (!box.contains(vector)) {
            switch (PushWorldBorder.behaviour) {
                case EVERYONE_IN_BORDER: {
                    teamPlayers.forEach(e -> box.union(e.getLocation()));
                    teamPlayers.forEach(e -> borderApi.setBorder(e, worldBorderSize, box.getCenter().toLocation(world)));
                }
                break;

                case USE_MOVING: {
                    teamPlayers.forEach(e -> box.union(e.getLocation()));
                    teamPlayers.forEach(e -> borderApi.setBorder(e, worldBorderSize, box.getCenter().toLocation(world)));

                    moveOtherPlayers(teamPlayers.stream().filter(p -> !p.equals(player)), world, size, box, 0);
                }
                break;

                case MAJOR_HALF: {
                    int cx = VectorUtils.compareRange(vector.getX(), box.getMinX(), box.getMaxX());
                    int cz = VectorUtils.compareRange(vector.getZ(), box.getMinZ(), box.getMaxZ());

                    List<Player> pushing = teamPlayers.stream()
                            .filter(e -> {
                                Vector v = e.getLocation().toVector();
                                if (box.contains(v))
                                    return false;
                                int vcx = VectorUtils.compareRange(v.getX(), box.getMinX(), box.getMaxX());
                                int vcz = VectorUtils.compareRange(v.getZ(), box.getMinZ(), box.getMaxZ());
                                int sx = Math.abs(vcx - cx);
                                int sz = Math.abs(vcz - cz);
                                return sx <= 0 && sz <= 1 || sx <= 1 && sz <= 0;
                            })
                            .collect(Collectors.toList());
                    if (pushing.size() > teamPlayers.size() / 2) {
                        box.union(location);
                        teamPlayers.forEach(e -> borderApi.setBorder(e, worldBorderSize, box.getCenter().toLocation(world)));

                        moveOtherPlayers(teamPlayers.stream().filter(p -> !pushing.contains(p)), world, size, box, 2);
                    }
                }
                break;

                case ONLY_LEADER: {
                    if (leader == null)
                        return;
                    if (!leader.isOnline())
                        return;
                    if (!leader.getUniqueId().equals(player.getUniqueId()))
                        return;
                    box.union(location);
                    teamPlayers.forEach(e -> borderApi.setBorder(e, worldBorderSize, box.getCenter().toLocation(world)));

                    moveOtherPlayers(teamPlayers.stream().filter(p -> !p.equals(player)), world, size, box, 2);
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
                        Vector closest = VectorUtils.getClosestPoint(boxIn, pVector);
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

}
