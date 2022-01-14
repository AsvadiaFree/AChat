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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Listeners implements Listener {
    public YamlConfiguration config = FileManager.getValues().get(Files.Config);
    public YamlConfiguration players = FileManager.getValues().get(Files.Players);

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        //Base
        event.setCancelled(true);
        Player player = event.getPlayer();

        //Mention
        String str = event.getMessage().toLowerCase();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player))
                continue;
            if (str.contains(p.getName().toLowerCase())) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("mention.text").replaceAll("%player%", player.getName()))));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 100);
            }
        }

        //Custom chat
        boolean isSend = false;
        for (String s : config.getConfigurationSection("formats").getKeys(false)) {
            if (player.hasPermission("achat." + s) || s.equals("default")) {
                // Format message
                Set<String> texts;
                Set<String> holderTexts;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    try {
                        // Choose chat template
                        int choose = getChoose(s, players, config, p);

                        //Translate message
                        String message = translateMessage(event.getMessage(), players, p);
                        if (player.hasPermission("achat.color"))
                            message = ChatColor.translateAlternateColorCodes('&', message);

                        //Format message
                        texts = config.getConfigurationSection("formats." + s + "." + choose + ".texts").getKeys(false);
                        holderTexts = config.getConfigurationSection("formats." + s + "." + choose + ".hoverTexts").getKeys(false);
                        List<BaseComponent> messages = new ArrayList<>();
                        for (String s1 : texts) {
                            String msg = ChatColor.translateAlternateColorCodes('&', config.getString("formats." + s + "." + choose + ".texts." + s1))
                                    .replaceAll("%message%", message);
                            TextComponent text = new TextComponent(msg);
                            if (PlaceholderAPI.containsPlaceholders(text.getText()))
                                text.setText(PlaceholderAPI.setPlaceholders(player, text.getText()));
                            if (holderTexts.contains(s1)) {
                                String hoverText = config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".text");
                                if (PlaceholderAPI.containsPlaceholders(hoverText))
                                    hoverText = PlaceholderAPI.setPlaceholders(player, hoverText);
                                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
                                text.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".cmd.type")), PlaceholderAPI.setPlaceholders(player, config.getString("formats." + s + "." + choose + ".hoverTexts." + s1 + ".cmd.text"))));
                            }
                            if (!text.getText().replaceAll(" ", "").equals(""))
                                messages.add(text);
                        }
                        p.spigot().sendMessage(messages.toArray(new BaseComponent[0]));
                        if (!isSend) {
                            Bukkit.getConsoleSender().spigot().sendMessage(messages.toArray(new BaseComponent[0]));
                            isSend = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    public int getChoose(String s, YamlConfiguration players, YamlConfiguration config, Player p) {
        int choose = 1;
        if (players.contains(p.getName().toLowerCase() + ".choose")
                && players.getInt(p.getName().toLowerCase() + ".choose") <= config.getConfigurationSection("formats." + s).getKeys(false).size())
            choose = players.getInt(p.getName().toLowerCase() + ".choose");
        return choose;
    }

    public static String translateMessage(String message, YamlConfiguration players, Player p) throws IOException {
        if (players.contains(p.getName().toLowerCase() + ".trad") && players.getBoolean(p.getName().toLowerCase() + ".trad"))
            if (!p.getLocale().equals(GoogleTranslate.getDisplayLanguage(message)))
                message = GoogleTranslate.translate(p.getLocale(), message);
        return message;
    }
}
