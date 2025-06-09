package net.opium.blockx;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler implements CommandExecutor {

    private final CustomItem customItems;

    public CommandHandler(JavaPlugin plugin) {
        this.customItems = new CustomItem(plugin);

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        customItems.addItems(args, sender);
        return true;
    }

    private void showHelp(CommandSender sender) {
        customItems.recommendItems(sender);
    }

    private void onExactCommand(CommandSender sender, String arg, String match, Runnable action) {
        if (arg.equalsIgnoreCase(match)) {
            action.run();
        }
    }
}
