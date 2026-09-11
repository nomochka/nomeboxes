package com.nome.nomeboxes.backpack;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public final class BackpackHolder implements InventoryHolder {

    private final UUID backpackId;
    private Inventory inventory;

    public BackpackHolder(UUID backpackId) {
        this.backpackId = backpackId;
    }

    public UUID backpackId() {
        return backpackId;
    }

    void inventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
