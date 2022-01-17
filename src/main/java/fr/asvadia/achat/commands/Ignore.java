package fr.asvadia.achat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ignore implements CommandExecutor {
    public static HashMap<Player, List<Player>> ignores = new HashMap<>();
    public static List<Player> ignoresAll = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                p.sendMessage("§6§lChat §f» §r§7Merci de spécifier un joueur ! §f(/ignore all pour tout les joueurs)");
                return false;
            }

            if (args[0].equalsIgnoreCase("all")) {
                if (ignoresAll.contains(p)) {

                } else {
                    ignores.remove(p);
                }
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if ()
            }
        }
        return false;
    }
}
