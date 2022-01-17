package eu.asvadia.achat;

import eu.asvadia.achat.listeners.RegisterListeners;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author MrCubee
 * @since 1.6
 * @version 1.1
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        RegisterListeners.registerListeners(this);
    }
}
