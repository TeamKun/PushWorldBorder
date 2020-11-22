package com.youtube.propromp.pushworldborder;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamBorder {
    public static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    public final Team team;
    public final List<Player> players;
    public final Player leader;

    public TeamBorder(Team team, List<Player> players, Player leader) {
        this.team = team;
        this.players = players;
        this.leader = leader;
    }

    public static Stream<TeamBorder> getTeamBorders() {
        return scoreboard.getTeams().stream()
                .map(team -> {
                    List<Player> list = getTeamPlayers(team).collect(Collectors.toList());
                    if (list.isEmpty())
                        return null;
                    Player player = getTeamLeader(list.stream())
                            .findFirst()
                            .orElseGet(() -> list.get(0));
                    List<Player> members = list.stream()
                            .filter(e -> e.getWorld().equals(player.getWorld()))
                            .collect(Collectors.toList());
                    return new TeamBorder(team, members, player);
                })
                .filter(Objects::nonNull);
    }

    public static Stream<TeamBorder> getNonTeamBorders() {
        return Bukkit.getWorlds().stream()
                .map(world -> {
                    List<Player> list = getNonTeamPlayers(world).collect(Collectors.toList());
                    if (list.isEmpty())
                        return null;
                    Player player = getTeamLeader(list.stream())
                            .findFirst()
                            .orElseGet(() -> list.get(0));
                    List<Player> members = list.stream()
                            .filter(e -> e.getWorld().equals(world))
                            .collect(Collectors.toList());
                    return new TeamBorder(null, members, player);
                })
                .filter(Objects::nonNull);
    }

    public static Stream<Player> getTeamLeader(Stream<Player> stream) {
        Objective objective = getObjective("teamleader", "チームリーダー");
        return stream.filter(p -> {
            Score score = objective.getScore(p.getName());
            return score.isScoreSet() && score.getScore() > 0;
        });
    }

    public static Stream<Player> getSameTeamPlayers(Player player) {
        return Optional.ofNullable(scoreboard.getEntryTeam(player.getName()))
                .map(TeamBorder::getTeamPlayers)
                .orElseGet(() -> getNonTeamPlayers(player.getWorld()));
    }

    public static Stream<Player> getTeamPlayers(Team team) {
        return team.getEntries().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull);
    }

    public static Stream<Player> getNonTeamPlayers(World world) {
        return world.getPlayers().stream()
                .filter(e -> scoreboard.getTeam(e.getName()) == null);
    }

    private static Objective getObjective(String name, String title) {
        Objective objective = scoreboard.getObjective(name);
        if (objective == null)
            objective = scoreboard.registerNewObjective(name, "dummy", title);
        return objective;
    }
}
