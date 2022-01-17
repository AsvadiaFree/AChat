package eu.asvadia.achat.event;

import eu.asvadia.achat.mention.MentionedPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.0
 */
public class AsyncPlayerMentionEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final MentionedPlayer mention;
    private String newMentionedName;

    public AsyncPlayerMentionEvent(final Player player, final MentionedPlayer mention) {
        super(player);
        this.mention = mention;
        this.newMentionedName = null;
    }

    public Player getMentionedPlayer() {
        return this.mention.mentionedPlayer;
    }

    public String getMentionedName() {
        return this.mention.mentionedName;
    }

    public String getNewMentionedName() {
        return this.newMentionedName;
    }

    public void setNewMentionedName(String newMentionedName) {
        this.newMentionedName = newMentionedName;
    }

    public void setNewMentionedName(BaseComponent... newMentionedName) {
        this.newMentionedName = BaseComponent.toLegacyText(newMentionedName);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
