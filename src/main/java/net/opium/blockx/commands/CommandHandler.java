package net.opium.blockx.commands;

import net.opium.blockx.items.CustomItem;
import net.opium.blockx.items.CustomItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler implements CommandExecutor {

    private final CustomItem customItemProvider; // Renamed for clarity

    // Constructor updated to receive CustomItemManager for CustomItem instantiation
    public CommandHandler(JavaPlugin plugin, CustomItemManager customItemManager) {
        this.customItemProvider = new CustomItem(plugin, customItemManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("xget")) {
            return false; // Should not happen if mapped correctly in plugin.yml
        }

        if (args.length == 0) {
            // If sender is a player, show help. Otherwise, maybe a console message.
            if (sender instanceof Player) {
                customItemProvider.showHelp((Player) sender);
            } else {
                sender.sendMessage("Use /xget <item_name> or see help in-game.");
            }
            return true;
        }

        // Delegate to CustomItem class to handle item retrieval or specific actions
        customItemProvider.handleItemCommand(args, sender);
        return true;
    }
}
