package fr.asvadia.achat.commands;

import fr.asvadia.achat.Main;
import fr.asvadia.achat.utils.File.FileManager;
import fr.asvadia.achat.utils.File.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Spy implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("achat.spy")) {
            Player p = (Player) sender;
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            if (Main.getInstance().spys.contains(p)) {
                Main.getInstance().spys.remove(p);
                p.sendMessage(config.getString("spy.off"));
            } else {
                Main.getInstance().spys.add(p);
                p.sendMessage(config.getString("spy.on"));
            }
        }
        return false;
    }
}
