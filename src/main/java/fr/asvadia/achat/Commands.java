package fr.asvadia.achat;

import fr.asvadia.achat.utils.File.FileManager;
import fr.asvadia.achat.utils.File.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            YamlConfiguration players = FileManager.getValues().get(Files.Players);
            if (args.length == 1) {
                boolean value = true;
                if (players.contains(args[0].toLowerCase() + ".trad"))
                    value = !players.getBoolean(args[0].toLowerCase() + ".trad");
                players.set(args[0].toLowerCase() + ".trad", value);
            } else {
                if (args.length < 2)
                    return false;
                players.set(args[0].toLowerCase() + ".choose", Integer.parseInt(args[1]));
            }
            FileManager.save(Files.Players);
            Main.getInstance().getListeners().players = FileManager.getValues().get(Files.Players);
        }
        return false;
    }
}
