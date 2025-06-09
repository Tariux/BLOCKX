package net.opium.blockx;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;



public class CustomItemManager {

    private final JavaPlugin plugin;

    public CustomItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public ItemStack generateItem(Player player, int itemId, String itemName, ItemAttributes attributes, Material itemMaterial) {
        if (itemMaterial == null) {
            itemMaterial = Material.STICK; 
        }

        ItemStack customItem = new ItemStack(itemMaterial); 

        
        ItemMeta meta = customItem.getItemMeta();

        if (meta != null) {
            
            meta.setDisplayName(ChatColor.RESET + itemName);

            
            List<String> lore = new ArrayList<>();
            lore.addAll(attributes.getLore()); 
            meta.setLore(lore);

            
            attributes.applyAttributes(meta);

            
            try {
                meta.setCustomModelData(itemId);
            } catch (NoSuchMethodError ignored) {
                
            }

            
            customItem.setItemMeta(meta);

            
            if (player != null) {
                player.getInventory().addItem(customItem);
                player.sendMessage(ChatColor.GREEN + "You received a custom item: " + itemName);
            }
        }

        
        return customItem;
    }

    
    
    
    
}
