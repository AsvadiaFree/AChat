package fr.asvadia.achat;

import fr.asvadia.achat.commands.Commands;
import fr.asvadia.achat.commands.Messages;
import fr.asvadia.achat.utils.File.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main instance;
    private Listeners listeners;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        try {
            FileManager.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        listeners = new Listeners();
        getServer().getPluginManager().registerEvents(listeners, this);
        getCommand("achat").setExecutor(new Commands());
        Messages messages = new Messages();
        getCommand("message").setExecutor(messages);
        getCommand("reply").setExecutor(messages);
    }

    public static Main getInstance() {
        return instance;
    }

    public Listeners getListeners() {
        return listeners;
    }
}
