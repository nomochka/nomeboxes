package com.nome.nomeboxes.backpack;

import com.nome.nomeboxes.storage.DataStore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class BackpackService {

    private final NamespacedKey typeKey;
    private final NamespacedKey idKey;
    private final DataStore store;

    public BackpackService(JavaPlugin plugin, DataStore store) {
        this.typeKey = new NamespacedKey(plugin, "backpack-type");
        this.idKey = new NamespacedKey(plugin, "backpack-id");
        this.store = store;
    }

    public ItemStack createItem(BackpackType type) {
        ItemStack item = new ItemStack(Material.CHEST);
        MiniMessage mm = MiniMessage.miniMessage();
        String gradient = "<gradient:" + type.gradientFrom() + ":" + type.gradientTo() + ">";
        item.editMeta((ItemMeta meta) -> {
            meta.displayName(mm.deserialize(
                    "<!italic><bold>" + gradient + type.title() + "</gradient></bold>"));
            meta.lore(List.of(
                    mm.deserialize("<!italic><dark_gray><st>                    </st></dark_gray>"),
                    mm.deserialize("<!italic><gray>Вместимость: <white>"
                            + type.slots() + " " + slotsWord(type.slots()) + "</white>"),
                    mm.deserialize("<!italic><gray>Класс: <" + type.rarityColor() + ">"
                            + type.rarity() + "</" + type.rarityColor() + ">"),
                    mm.deserialize("<!italic><dark_gray><st>                    </st></dark_gray>"),
                    mm.deserialize("<!italic><yellow>ПКМ</yellow> <gray>— чтобы открыть")
            ));
            meta.setItemModel(new NamespacedKey("nomeboxes", "backpack_" + type.id()));
            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, type.id());
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        });
        return item;
    }

    public boolean isBackpack(ItemStack item) {
        return item != null && !item.isEmpty()
                && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(typeKey, PersistentDataType.STRING);
    }

    public BackpackType typeOf(ItemStack item) {
        String id = item.getItemMeta().getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
        return BackpackType.byId(id);
    }

    public void open(Player player, ItemStack item) {
        BackpackType type = typeOf(item);
        if (type == null) {
            return;
        }
        UUID id = idOf(item);
        ItemStack[] stored = store.get(id);

        BackpackHolder holder = new BackpackHolder(id);
        MiniMessage mm = MiniMessage.miniMessage();
        Component title = mm.deserialize(
                "<!italic><bold><gradient:" + type.gradientFrom() + ":" + type.gradientTo() + ">"
                        + type.title() + "</gradient></bold> <dark_gray>"
                        + type.slots() + " " + slotsWord(type.slots()));
        Inventory inventory = org.bukkit.Bukkit.createInventory(holder, type.slots(), title);
        holder.inventory(inventory);
        inventory.setContents(Arrays.copyOf(stored, type.slots()));
        player.openInventory(inventory);
    }

    public void save(UUID backpackId, ItemStack[] contents) {
        store.put(backpackId, contents);
    }

    public void flush() {
        store.flush();
    }

    private static String slotsWord(int slots) {
        int mod100 = slots % 100;
        int mod10 = slots % 10;
        if (mod10 == 1 && mod100 != 11) {
            return "слот";
        }
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
            return "слота";
        }
        return "слотов";
    }

    private UUID idOf(ItemStack item) {
        String value = item.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (value != null) {
            try {
                return UUID.fromString(value);
            } catch (IllegalArgumentException ignored) {
            }
        }
        UUID fresh = UUID.randomUUID();
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(idKey, PersistentDataType.STRING, fresh.toString()));
        return fresh;
    }
}
