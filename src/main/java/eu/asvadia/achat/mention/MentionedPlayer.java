package eu.asvadia.achat.mention;

import eu.asvadia.achat.event.AsyncPlayerMentionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.0
 */
public class MentionedPlayer {

    private static final StringBuilder STRING_BUILDER = new StringBuilder();

    public final Player mentionedPlayer;
    public final String mentionedName;
    protected final Set<Integer> mentionIndex;

    public MentionedPlayer(final Player mentionedPlayer, final String mentionedName) {
        this.mentionedPlayer = mentionedPlayer;
        this.mentionedName = mentionedName;
        this.mentionIndex = new HashSet<Integer>();
    }

    public Set<Integer> getMentionIndex() {
        return Collections.unmodifiableSet(this.mentionIndex);
    }

    public void replaceMention(final String[] words, final String newMentionedName) {
        if (words == null || newMentionedName == null || this.mentionedName == null || newMentionedName.equals(this.mentionedName))
            return;
        for (int index : this.mentionIndex)
            words[index] = newMentionedName;
    }

    private static List<MentionedPlayer> getMentionedPlayers(String... words) {
        final Map<String, MentionedPlayer> mentionedPlayers;
        Player searchedPlayer;
        MentionedPlayer mentionedPlayer;

        if (words == null)
            return null;
        mentionedPlayers = new HashMap<String, MentionedPlayer>();
        for (int i = 0; i < words.length; i++) {
            searchedPlayer = Bukkit.getPlayerExact(words[i]);
            if (searchedPlayer != null) {
                mentionedPlayer = mentionedPlayers.get(words[i]);
                if (mentionedPlayer == null) {
                    mentionedPlayer = new MentionedPlayer(searchedPlayer, words[i]);
                    mentionedPlayers.put(words[i], mentionedPlayer);
                }
                mentionedPlayer.mentionIndex.add(i);
            }
        }
        return new ArrayList<MentionedPlayer>(mentionedPlayers.values());
    }

    public static String parseMention(final Player playerSender, final String message) {
        final PluginManager pluginManager;
        final String[] words;
        final List<MentionedPlayer> mentionedPlayers;
        AsyncPlayerMentionEvent mentionEvent;

        if (message == null)
            return null;
        pluginManager = Bukkit.getPluginManager();
        words = message.split(" ");
        mentionedPlayers = getMentionedPlayers(words);
        for (MentionedPlayer mentionedPlayer : mentionedPlayers) {
            mentionEvent = new AsyncPlayerMentionEvent(playerSender, mentionedPlayer);
            pluginManager.callEvent(mentionEvent);
            mentionedPlayer.replaceMention(words, mentionEvent.getNewMentionedName());
        }
        STRING_BUILDER.setLength(0);
        for (String word : words) {
            if (STRING_BUILDER.length() != 0)
                STRING_BUILDER.append(' ');
            STRING_BUILDER.append(word);
        }
        return STRING_BUILDER.toString();
    }
}
