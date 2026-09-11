package com.nome.nomeboxes.storage;

import com.nome.nomeboxes.backpack.Serializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DataStore {

    private final File file;
    private final Object lock = new Object();
    private final Map<UUID, String> cache = new HashMap<>();
    private boolean dirty;

    public DataStore(File folder) {
        this.file = new File(folder, "backpacks.yml");
        folder.mkdirs();
        load();
    }

    private void load() {
        if (!file.exists()) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("backpacks");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            String data = section.getString(key);
            if (data == null) {
                continue;
            }
            try {
                cache.put(UUID.fromString(key), data);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public ItemStack[] get(UUID id) {
        synchronized (lock) {
            String data = cache.get(id);
            if (data == null) {
                return new ItemStack[0];
            }
            try {
                return Serializer.fromString(data);
            } catch (IOException e) {
                return new ItemStack[0];
            }
        }
    }

    public void put(UUID id, ItemStack[] items) {
        synchronized (lock) {
            try {
                cache.put(id, Serializer.toString(items));
                dirty = true;
            } catch (IOException ignored) {
            }
        }
    }

    public void startAutoSave(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::flushIfDirty, 200L, 200L);
    }

    public void flushIfDirty() {
        synchronized (lock) {
            if (!dirty) {
                return;
            }
            save();
        }
    }

    public void flush() {
        synchronized (lock) {
            save();
        }
    }

    private void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : cache.entrySet()) {
            yaml.set("backpacks." + entry.getKey(), entry.getValue());
        }
        try {
            yaml.save(file);
            dirty = false;
        } catch (IOException ignored) {
        }
    }
}
