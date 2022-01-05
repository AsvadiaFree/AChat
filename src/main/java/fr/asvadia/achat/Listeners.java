package fr.asvadia.achat;

import com.darkprograms.speech.translator.GoogleTranslate;
import fr.asvadia.achat.utils.File.FileManager;
import fr.asvadia.achat.utils.File.Files;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.Set;

public class Listeners implements Listener {
    public YamlConfiguration config = FileManager.getValues().get(Files.Config);
    public YamlConfiguration players = FileManager.getValues().get(Files.Players);

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        //Base
        event.setCancelled(true);
        Player player = event.getPlayer();

        //Anti-Spam
        if (isSpam(event.getMessage())) {
            player.sendMessage(config.getString("spam.text"));
            return;
        }

        //Mention
        String str = event.getMessage().toLowerCase();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(player.getName()))
                continue;
            if (str.contains(p.getName().toLowerCase())) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(config.getString("mention.text").replaceAll("%player%", player.getName())));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 100);
            }
        }

        //Custom chat
        for (String s : config.getConfigurationSection("formats").getKeys(false)) {
            if (player.hasPermission("achat." + s) || s.equals("default")) {
                // Format message
                Set<String> texts;
                Set<String> holderTexts;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    try {
                        // Choose chat template
                        int choose = 1;
                        if (players.contains(p.getName().toLowerCase() + ".choose")
                                && players.getInt(p.getName().toLowerCase() + ".choose") <= config.getConfigurationSection("formats." + s).getKeys(false).size())
                            choose = players.getInt(p.getName().toLowerCase() + ".choose");

                        //Translate message
                        String message = event.getMessage();
                        if (players.contains(p.getName().toLowerCase() + ".trad") && players.getBoolean(p.getName().toLowerCase() + ".trad"))
                            if (!p.getLocale().equals(GoogleTranslate.getDisplayLanguage(event.getMessage())))
                                message = GoogleTranslate.translate(p.getLocale(), message);

                        //Format message
                        texts = config.getConfigurationSection("formats." + s + "." + choose + ".texts").getKeys(false);
                        holderTexts = config.getConfigurationSection("formats." + s + "." + choose + ".hoverTexts").getKeys(false);
                        BaseComponent[] messages = new BaseComponent[texts.size()];
                        int n = 0;
                        for (String s1 : texts) {
                            TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("formats." + s + "." + choose + ".texts." + s1).replaceAll("%message%", message)));
                            if (PlaceholderAPI.containsPlaceholders(text.getText()))
                                text.setText(PlaceholderAPI.setPlaceholders(player, text.getText()));
                            if (holderTexts.contains(s1)) {
                                String hoverText = config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".text");
                                if (PlaceholderAPI.containsPlaceholders(hoverText))
                                    hoverText = PlaceholderAPI.setPlaceholders(player, hoverText);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
                                text.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".cmd.type")), PlaceholderAPI.setPlaceholders(player, config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".cmd.text"))));
                            }
                            if (!StringUtils.isBlank(text.getText()))
                                messages[n++] = text;
                        }
                        p.spigot().sendMessage(messages);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    private boolean isSpam(String str) {
        return false;
    }
}
