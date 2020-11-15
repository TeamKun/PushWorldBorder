package com.youtube.propromp.pushworldborder.commands;

import com.youtube.propromp.pushworldborder.BorderBehaviour;
import com.youtube.propromp.pushworldborder.PushWorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PWBCommand implements CommandExecutor, TabCompleter {
    final static String[] HELP_MESSAGE = {
            "----------[" + ChatColor.DARK_RED + "PushWorldBorder" + ChatColor.RESET + "]----------",
            "/pwb help: ヘルプ表示",
            "/pwb type: 挙動を設定",
            "/pwb leader: リーダーを設定",
            "-------------------------------------",
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                    // /pwb help:ヘルプ表示
                    Stream.of(HELP_MESSAGE).forEach(sender::sendMessage);
                    return true;
                case "type":
                    if (args.length <= 1)
                        return false;
                    // /pwb type <type>
                    PushWorldBorder.behaviour = BorderBehaviour.from(args[1]);
                    sender.sendMessage("[" + ChatColor.DARK_PURPLE + "PushWorldBorder" + ChatColor.RESET + "]種類を" + PushWorldBorder.behaviour + "に設定しました。");
                    return true;
                case "leader":
                    if (args.length <= 1)
                        return false;
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage("プレイヤーが見つかりません");
                        return true;
                    }
                    PushWorldBorder.leader = player;
                    sender.sendMessage("[" + ChatColor.DARK_PURPLE + "PushWorldBorder" + ChatColor.RESET + "]リーダーを" + PushWorldBorder.leader.getName() + "に設定しました。");
                    return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return Stream.of("help", "type", "leader")
                        .filter(e -> e.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:
                switch (args[0]) {
                    case "type":
                        return Arrays.stream(BorderBehaviour.values())
                                .map(e -> e.name().toLowerCase())
                                .filter(e -> e.startsWith(args[1]))
                                .collect(Collectors.toList());
                    case "leader":
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(e -> e.startsWith(args[1]))
                                .collect(Collectors.toList());
                }
        }
        return Collections.emptyList();
    }
}
