package eu.asvadia.achat.listeners;

import eu.asvadia.achat.event.AsyncPlayerMentionEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.0
 */
public class AsyncPlayerMentionListener implements Listener {

    @EventHandler
    public void event(AsyncPlayerMentionEvent event) {
        final Player mentionedPlayer = event.getMentionedPlayer();

        event.setNewMentionedName(ChatColor.AQUA.toString() + ChatColor.BOLD + "@" + event.getMentionedName() + ChatColor.RESET);
        mentionedPlayer.playSound(mentionedPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 0);
    }

}
