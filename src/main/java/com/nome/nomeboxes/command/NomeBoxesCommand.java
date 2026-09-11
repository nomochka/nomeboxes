package com.nome.nomeboxes.command;

import com.nome.nomeboxes.backpack.BackpackService;
import com.nome.nomeboxes.backpack.BackpackType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class NomeBoxesCommand implements CommandExecutor, TabCompleter {

    private final BackpackService service;

    public NomeBoxesCommand(BackpackService service) {
        this.service = service;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }
        if (!args[0].equalsIgnoreCase("give")) {
            sendHelp(sender, label);
            return true;
        }
        if (!sender.hasPermission("nomeboxes.give")) {
            sender.sendMessage(Component.text("[NomeBoxes] Недостаточно прав.", NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Component.text("[NomeBoxes] Использование: /" + label
                    + " give <small|medium|large|super> [игрок]", NamedTextColor.YELLOW));
            return true;
        }
        BackpackType type = BackpackType.byId(args[1]);
        if (type == null) {
            sender.sendMessage(Component.text("[NomeBoxes] Неизвестный тип рюкзака: " + args[1],
                    NamedTextColor.RED));
            return true;
        }
        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("[NomeBoxes] Игрок не найден: " + args[2],
                        NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(Component.text("[NomeBoxes] Укажите игрока из консоли.",
                    NamedTextColor.RED));
            return true;
        }

        ItemStack item = service.createItem(type);
        Map<Integer, ItemStack> overflow = new HashMap<>();
        overflow.putAll(target.getInventory().addItem(item));
        overflow.values().forEach(rest ->
                target.getWorld().dropItemNaturally(target.getLocation(), rest));

        sender.sendMessage(Component.text("[NomeBoxes] Выдан " + type.title()
                + " игроку " + target.getName(), NamedTextColor.GREEN));
        if (!sender.equals(target)) {
            target.sendMessage(Component.text("[NomeBoxes] Вам выдан " + type.title(),
                    NamedTextColor.GREEN));
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(Component.text("=== NomeBoxes ===", NamedTextColor.GOLD));
        for (BackpackType type : BackpackType.values()) {
            sender.sendMessage(Component.text("/" + label + " give " + type.id()
                            + " [игрок]", NamedTextColor.YELLOW)
                    .append(Component.text(" — " + type.title()
                            + " (" + type.slots() + " слотов)", NamedTextColor.GRAY)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (BackpackType type : BackpackType.values()) {
                if (type.id().startsWith(args[1].toLowerCase(Locale.ROOT))) {
                    result.add(type.id());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT))) {
                    result.add(player.getName());
                }
            }
        }
        return result;
    }
}
