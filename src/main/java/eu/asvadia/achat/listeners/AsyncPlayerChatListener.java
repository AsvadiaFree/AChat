package eu.asvadia.achat.listeners;

import eu.asvadia.achat.mention.MentionedPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.0
 */
public class AsyncPlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(final AsyncPlayerChatEvent event) {
        event.setMessage(MentionedPlayer.parseMention(event.getPlayer(), event.getMessage()));
    }
}
