package net.blockx.commands; // Updated package

import net.blockx.items.CustomItem; // Updated import
import net.blockx.items.CustomItemManager; // Updated import
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler implements CommandExecutor {

    private final CustomItem customItemProvider;
    // No longer need to store plugin or customItemManager if CustomItem is fully responsible
    // for its dependencies once constructed. Blockx.java will pass the CIM to this constructor.

    // Constructor updated to receive CustomItemManager directly
    public CommandHandler(JavaPlugin plugin, CustomItemManager customItemManager) {
        // CustomItem constructor takes plugin and CIM
        this.customItemProvider = new CustomItem(plugin, customItemManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("xget")) {
            return false;
        }

        if (args.length == 0) {
            if (sender instanceof Player player) { // Use pattern matching
                customItemProvider.showHelp(player);
            } else {
                sender.sendMessage("Use /xget <item_name> or see help in-game for available items.");
            }
            return true;
        }

        customItemProvider.handleItemCommand(args, sender);
        return true;
    }
}
