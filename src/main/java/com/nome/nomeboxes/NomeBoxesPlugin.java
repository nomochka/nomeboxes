package com.nome.nomeboxes;

import com.nome.nomeboxes.backpack.BackpackService;
import com.nome.nomeboxes.command.NomeBoxesCommand;
import com.nome.nomeboxes.listener.BackpackListener;
import com.nome.nomeboxes.storage.DataStore;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NomeBoxesPlugin extends JavaPlugin {

    private BackpackService service;

    @Override
    public void onEnable() {
        DataStore store = new DataStore(getDataFolder());
        service = new BackpackService(this, store);

        getServer().getPluginManager().registerEvents(new BackpackListener(service), this);

        PluginCommand command = getCommand("nomeboxes");
        NomeBoxesCommand executor = new NomeBoxesCommand(service);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        store.startAutoSave(this);
        getLogger().info("NomeBoxes enabled (author: nome)");
    }

    @Override
    public void onDisable() {
        if (service != null) {
            service.flush();
        }
    }
}
