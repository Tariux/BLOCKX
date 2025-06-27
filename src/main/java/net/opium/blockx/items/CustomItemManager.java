package net.opium.blockx.items;

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
    private final NamespacedKey customItemIdKey;

    public CustomItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.customItemIdKey = new NamespacedKey(plugin, "custom_item_id");
    }

    /**
     * Generates a custom item with specified properties.
     *
     * @param player       The player to give the item to (can be null if item is not given directly).
     * @param customModelData The CustomModelData ID for the resource pack.
     * @param itemName     The display name of the item.
     * @param attributes   The ItemAttributes object containing lore and Bukkit attributes.
     * @param itemMaterial The base material for the item. If null, defaults to Material.STICK.
     * @return The generated ItemStack.
     */
    public ItemStack generateItem(Player player, int customModelData, String itemName, ItemAttributes attributes, Material itemMaterial) {
        if (itemMaterial == null) {
            itemMaterial = Material.STICK; // Default material
            plugin.getLogger().warning("generateItem called with null material for " + itemName + ", defaulting to STICK.");
        }

        ItemStack customItem = new ItemStack(itemMaterial);
        ItemMeta meta = customItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName)); // Allow color codes in name

            if (attributes != null) {
                List<String> lore = new ArrayList<>();
                // Process lore with color codes
                for (String line : attributes.getLore()) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                meta.setLore(lore);
                attributes.applyAttributes(meta);
            }

            // Set CustomModelData if it's a positive value (or any value, depending on needs)
            // Minecraft typically uses positive integers.
            if (customModelData > 0) {
                 meta.setCustomModelData(customModelData);
            } else {
                // Not setting custom model data, or logging if it's an unexpected value
                // plugin.getLogger().info("No CustomModelData set for " + itemName + " (ID: " + customModelData + ")");
            }


            // Add PDC tag for identification
            meta.getPersistentDataContainer().set(customItemIdKey, PersistentDataType.STRING, String.valueOf(customModelData));
            customItem.setItemMeta(meta);

            if (player != null) {
                player.getInventory().addItem(customItem);
                player.sendMessage(ChatColor.GREEN + "You received: " + itemName);
            }
        } else {
            plugin.getLogger().severe("Could not get ItemMeta for " + itemMaterial + " when creating " + itemName);
        }
        return customItem;
    }

    /**
     * Checks if the given ItemStack is a specific custom item.
     *
     * @param itemStack           The ItemStack to check.
     * @param expectedCustomModelData The expected CustomModelData ID as a string.
     * @param expectedMaterial    The expected Material of the item.
     * @return True if the item matches all criteria, false otherwise.
     */
    public boolean isSpecificCustomItem(ItemStack itemStack, String expectedCustomModelData, Material expectedMaterial) {
        if (itemStack == null || itemStack.getType() != expectedMaterial) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }

        // Check Custom Model Data
        if (!meta.hasCustomModelData()) {
            // If we expect CMD 0 (or no CMD), this check might need adjustment.
            // For now, assume custom items always have CMD.
            return false;
        }
        try {
            int expectedCmd = Integer.parseInt(expectedCustomModelData);
            if (meta.getCustomModelData() != expectedCmd) {
                return false;
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid expectedCustomModelData format: " + expectedCustomModelData);
            return false; // expectedCustomModelData is not a valid integer for custom model data check
        }

        // Check PDC tag
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(customItemIdKey, PersistentDataType.STRING)) {
            return false;
        }
        String actualItemIdStr = container.get(customItemIdKey, PersistentDataType.STRING);
        return expectedCustomModelData.equals(actualItemIdStr);
    }

    /**
     * Retrieves the custom item ID (CustomModelData) from an ItemStack's PDC.
     * @param itemStack The item to check.
     * @return The CustomModelData as a string if present, otherwise null.
     */
    public String getCustomItemId(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(customItemIdKey, PersistentDataType.STRING)) {
            return container.get(customItemIdKey, PersistentDataType.STRING);
        }
        return null;
    }
}
