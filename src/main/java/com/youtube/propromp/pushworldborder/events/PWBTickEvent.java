package com.youtube.propromp.pushworldborder.events;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.youtube.propromp.pushworldborder.*;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.youtube.propromp.pushworldborder.VectorUtils.toVector;

public class PWBTickEvent extends BukkitRunnable {

    public void run() {
        if (PushWorldBorder.behaviour == BorderBehaviour.NONE)
            return;

        WorldBorderApi borderApi = BorderAPI.getApi();

        TeamBorder.getTeamBorders().forEach(border -> {
            IWorldBorder playerBorder = borderApi.getWorldBorder(border.leader);
            World world = border.leader.getWorld();

            ChatColor c = border.team.getColor();
            Particle.DustOptions dust = new Particle.DustOptions(ChatColorToColor.getColor(c), 1);

            double minY = border.players.stream().map(e -> e.getLocation().getY()).min(Comparator.naturalOrder()).orElseGet(() -> border.leader.getLocation().getY());
            double maxY = border.players.stream().map(e -> e.getLocation().getY()).max(Comparator.naturalOrder()).orElseGet(() -> border.leader.getLocation().getY());

            BoundingBox box = BoundingBox.of(toVector(playerBorder.getCenter()), playerBorder.getSize() / 2, 0, playerBorder.getSize() / 2);

            IntStream.range(0, 8).mapToDouble(i -> i / 7.0)
                    .boxed()
                    .flatMap(f -> {
                        double y = VectorUtils.lerp(f, minY, maxY);
                        return Stream.of(
                                new AbstractMap.SimpleEntry<>(new Vector(box.getMinX(), y, box.getMinZ()), new Vector(box.getMaxX(), y, box.getMinZ())),
                                new AbstractMap.SimpleEntry<>(new Vector(box.getMaxX(), y, box.getMinZ()), new Vector(box.getMaxX(), y, box.getMaxZ())),
                                new AbstractMap.SimpleEntry<>(new Vector(box.getMaxX(), y, box.getMaxZ()), new Vector(box.getMinX(), y, box.getMaxZ())),
                                new AbstractMap.SimpleEntry<>(new Vector(box.getMinX(), y, box.getMaxZ()), new Vector(box.getMinX(), y, box.getMinZ()))
                        )
                                .flatMap(p -> IntStream.range(0, 0 < f && f < 1 ? 1 : 8)
                                        .mapToDouble(i -> i / 8.0)
                                        .mapToObj(x -> VectorUtils.lerp(x, p.getKey(), p.getValue())));
                    })
                    .map(v -> v.toLocation(world))
                    .forEach(v -> world.spawnParticle(Particle.REDSTONE, v, 0, 0, 0, 0, dust));
        });
    }

}
