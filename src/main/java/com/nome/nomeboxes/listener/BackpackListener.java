package com.nome.nomeboxes.listener;

import com.nome.nomeboxes.backpack.BackpackHolder;
import com.nome.nomeboxes.backpack.BackpackService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class BackpackListener implements Listener {

    private final BackpackService service;

    public BackpackListener(BackpackService service) {
        this.service = service;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (!service.isBackpack(item)) {
            return;
        }
        event.setCancelled(true);
        service.open(event.getPlayer(), item);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (!service.isBackpack(item)) {
            return;
        }
        event.setCancelled(true);
        service.open(event.getPlayer(), item);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (service.isBackpack(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BackpackHolder holder)) {
            return;
        }
        service.save(holder.backpackId(), event.getInventory().getContents());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof BackpackHolder)) {
            return;
        }
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && service.isBackpack(event.getCurrentItem())) {
            event.setCancelled(true);
            return;
        }
        boolean clickedBackpack = event.getClickedInventory() != null
                && event.getClickedInventory().equals(top);
        if (clickedBackpack && service.isBackpack(event.getCursor())) {
            event.setCancelled(true);
            return;
        }
        if (clickedBackpack
                && (event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD)
                && event.getHotbarButton() >= 0
                && service.isBackpack(event.getView().getBottomInventory().getItem(event.getHotbarButton()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!(top.getHolder() instanceof BackpackHolder)) {
            return;
        }
        if (!service.isBackpack(event.getOldCursor())) {
            return;
        }
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < top.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
