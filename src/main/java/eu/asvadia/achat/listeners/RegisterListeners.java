package eu.asvadia.achat.listeners;

import eu.asvadia.achat.Main;
import org.bukkit.plugin.PluginManager;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.0
 */
public class RegisterListeners {

    public static void registerListeners(final Main plugin) {
        final PluginManager pluginManager;

        if (plugin == null)
            return;
        pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new AsyncPlayerChatListener(), plugin);
        pluginManager.registerEvents(new AsyncPlayerMentionListener(), plugin);
    }

}
