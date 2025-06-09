package net.opium.blockx;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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

            // Add PDC tag for identification
            NamespacedKey key = new NamespacedKey(plugin, "custom_item_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.valueOf(itemId)); // Store itemId as string
            
            customItem.setItemMeta(meta);

            
            if (player != null) {
                player.getInventory().addItem(customItem);
                player.sendMessage(ChatColor.GREEN + "You received a custom item: " + itemName);
            }
        }

        
        return customItem;
    }

    
    
    public boolean isSpecificCustomItem(ItemStack itemStack, String expectedItemIdStr, Material expectedMaterial) {
        if (itemStack == null || itemStack.getType() != expectedMaterial) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        // Check Custom Model Data
        if (!meta.hasCustomModelData()) {
            return false;
        }
        try {
            int expectedCustomModelData = Integer.parseInt(expectedItemIdStr);
            if (meta.getCustomModelData() != expectedCustomModelData) {
                return false;
            }
        } catch (NumberFormatException e) {
            // expectedItemIdStr is not a valid integer for custom model data check
            return false;
        }

        // Check PDC tag
        NamespacedKey key = new NamespacedKey(plugin, "custom_item_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(key, PersistentDataType.STRING)) {
            return false;
        }
        String actualItemIdStr = container.get(key, PersistentDataType.STRING);
        return expectedItemIdStr.equals(actualItemIdStr);
    }
    
}
