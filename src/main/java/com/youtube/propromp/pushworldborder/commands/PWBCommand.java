package com.youtube.propromp.pushworldborder.commands;

import com.youtube.propromp.pushworldborder.PushWorldBorder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PWBCommand implements CommandExecutor {
    final static String HELPMESSAGE =
            "----------["+ ChatColor.DARK_RED +"PushWorldBorder"+ChatColor.RESET+"]----------" +
            "/pwb help:ヘルプ表示" +
            "/pwb set [number]:1tickごとにワールドボーダーが広がる大きさを変える" +
            "-------------------------------------";

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        boolean res = false;
        switch (args[0]){
            case "help":
                res = help(sender,command,label,args);// /pwb help:ヘルプ表示
                break;
            case "set":
                res = set(sender,command,label,args);// /pwb set [number]:1tickごとにワールドボーダーが広がる大きさを変える
                break;
        }

        return res;
    }

    private boolean help(CommandSender sender, Command command, String label, String[] args) {// /pwb help:ヘルプ表示
        sender.sendMessage(HELPMESSAGE);
        return true;
    }

    private boolean set(CommandSender sender, Command command, String label, String[] args) {// /pwb set [number]:1tickごとにワールドボーダーが広がる大きさを変える
        PushWorldBorder.config.set("config.speed",Double.valueOf(args[1]));
        sender.sendMessage("["+ ChatColor.DARK_PURPLE+"PushWorldBorder"+ChatColor.RESET+"]ワールドボーダーの軽さを"+args[1]+"に設定しました。");
        return true;
    }
}
