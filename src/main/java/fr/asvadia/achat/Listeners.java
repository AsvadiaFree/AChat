package fr.asvadia.achat;

import fr.asvadia.achat.utils.File.FileManager;
import fr.asvadia.achat.utils.File.Files;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class Listeners implements Listener {
    public YamlConfiguration config = FileManager.getValues().get(Files.Config);
    public YamlConfiguration players = FileManager.getValues().get(Files.Players);

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        for (String s : config.getConfigurationSection("formats").getKeys(false)) {
            if (player.hasPermission("achat." + s) || s.equals("default")) {
                int choose = 1;
                if (players.contains(player.getName().toLowerCase())
                        && players.getInt(player.getName().toLowerCase()) <= config.getConfigurationSection("formats." + s).getKeys(false).size())
                    choose = players.getInt(player.getName().toLowerCase());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Set<String> texts = config.getConfigurationSection("formats." + s + "." + choose + ".texts").getKeys(false);
                    Set<String> tooltips = config.getConfigurationSection("formats." + s + "." + choose + ".hoverTexts").getKeys(false);
                    BaseComponent[] messages = new BaseComponent[texts.size()];
                    int n = 0;
                    for (String s1 : texts) {
                        TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("formats." + s + "." + choose + ".texts." + s1).replaceAll("%message%", event.getMessage())));
                        if (PlaceholderAPI.containsPlaceholders(text.getText()))
                            text.setText(PlaceholderAPI.setPlaceholders(player, text.getText()));
                        if (tooltips.contains(s1)) {
                            String hoverText = config.getString("formats." + s + "." + choose + ".hoverTexts." + s1);
                            if (PlaceholderAPI.containsPlaceholders(hoverText))
                                hoverText = PlaceholderAPI.setPlaceholders(player, hoverText);
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
                        }
                        messages[n++] = text;
                    }
                    p.spigot().sendMessage(messages);
                }
                break;
            }
        }
    }
}
