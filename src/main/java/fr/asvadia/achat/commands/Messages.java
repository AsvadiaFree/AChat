package fr.asvadia.achat.commands;

import fr.asvadia.achat.Listeners;
import fr.asvadia.achat.utils.File.FileManager;
import fr.asvadia.achat.utils.File.Files;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class Messages implements CommandExecutor {
    private final HashMap<UUID, UUID> LAST_REPLY = new HashMap<>();
    private final HashMap<UUID, Long> LAST_REPLY_TIME = new HashMap<>();
    private final long TIME_NO_REPLY = 60000L;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            YamlConfiguration config = FileManager.getValues().get(Files.Config);
            YamlConfiguration players = FileManager.getValues().get(Files.Players);
            Player[] player = new Player[2];
            player[0] = (Player) sender;
            switch (cmd.getName().toLowerCase()) {
                case "message":
                    if (args.length > 1) {
                        // add new player
                        player[1] = Bukkit.getPlayer(args[0]);
                        if (player[1] == null) {
                            player[0].sendMessage(Objects.requireNonNull(config.getString("private.messages.playerNotFound")));
                            return false;
                        }

                        // Compile messages
                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; i++)
                            message.append(args[i]).append(" ");

                        sendMessage(message.toString(), config, players, player);
                    }
                    break;
                case "reply":
                    if (LAST_REPLY.containsKey(player[0].getUniqueId())) {
                        if ((System.currentTimeMillis() - LAST_REPLY_TIME.get(player[0].getUniqueId())) > TIME_NO_REPLY) {
                            LAST_REPLY_TIME.remove(player[0].getUniqueId());
                            LAST_REPLY.remove(player[0].getUniqueId());
                            player[0].sendMessage(Objects.requireNonNull(config.getString("private.messages.noReply")));
                            return false;
                        }

                        player[1] = Bukkit.getPlayer(LAST_REPLY.get(player[0].getUniqueId()));
                        if (player[1] == null) {
                            player[0].sendMessage(Objects.requireNonNull(config.getString("private.messages.playerNotFound")));
                            return false;
                        }

                        // Compile messages
                        StringBuilder message = new StringBuilder();
                        for (String arg : args)
                            message.append(arg).append(" ");

                        sendMessage(message.toString(), config, players, player);
                    } else {
                        LAST_REPLY_TIME.remove(player[0].getUniqueId());
                        player[0].sendMessage(Objects.requireNonNull(config.getString("private.messages.noReply")));
                    }
                    break;
                default:
                    throw new IllegalStateException("Command is not good !");
            }
        }
        return false;
    }

    private void sendMessage(@NotNull String message, YamlConfiguration config, YamlConfiguration players, Player[] player) {
        Set<String> texts;
        Set<String> holderTexts;
        Set<Player> ps = new HashSet<>(Spy.spys);
        Collections.addAll(ps, player);
        ps.removeIf(Objects::isNull);

        for (Player p : ps) {
            try {
                String choose;
                if (p.getUniqueId().equals(player[1].getUniqueId()))
                    choose = "receiver";
                else
                    choose = "sender";

                // Translate message
                String msg = Listeners.translateMessage(message, players, p);

                //Register reply
                Player other = player[0];
                if (player.length == 2 && !p.getUniqueId().equals(player[1].getUniqueId()))
                    other = player[1];

                //Format message
                texts = config.getConfigurationSection("private.formats." + choose + ".texts").getKeys(false);
                holderTexts = config.getConfigurationSection("private.formats." + choose + ".hoverTexts").getKeys(false);
                List<BaseComponent> messages = new ArrayList<>();
                for (String s1 : texts) {
                    TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("private.formats." + choose + ".texts." + s1).replaceAll("%message%", msg)));
                    if (PlaceholderAPI.containsPlaceholders(text.getText()))
                        text.setText(PlaceholderAPI.setPlaceholders(other, text.getText()));
                    if (holderTexts.contains(s1)) {
                        String hoverText = config.getString("private.formats." + choose + ".hoverTexts." + s1 + ".text");
                        if (PlaceholderAPI.containsPlaceholders(hoverText))
                            hoverText = PlaceholderAPI.setPlaceholders(other, hoverText);
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(config.getString("private.formats." + choose + ".hoverTexts." + s1 + ".cmd.type")), PlaceholderAPI.setPlaceholders(other, config.getString("private.formats." + choose + ".hoverTexts." + s1 + ".cmd.text"))));
                    }
                    if (!StringUtils.isBlank(text.getText()))
                        messages.add(text);
                }
                if (Spy.spys.contains(p))
                    messages.add(0, new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getString("spy.text").replaceAll("%sender%", player[0].getName()))));
                p.spigot().sendMessage(messages.toArray(new BaseComponent[0]));

                LAST_REPLY.put(p.getUniqueId(), other.getUniqueId());
                LAST_REPLY_TIME.put(p.getUniqueId(), System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
