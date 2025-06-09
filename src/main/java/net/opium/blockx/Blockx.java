package net.opium.blockx;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class Blockx extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Blockx Plugin Enabled");

        getServer().getPluginManager().registerEvents(this, this);
        createUltraCraftingTableRecipe();
    }

    @Override
    public void onDisable() {
        getLogger().info("Blockx Plugin Disabled");
    }

    private ItemStack getUltraCraftingItem() {
        ItemStack ultraItem = new ItemStack(Material.STONE); // Base material is stone
        ItemMeta meta = ultraItem.getItemMeta();
        meta.setDisplayName("Ultra Crafting Table");
        meta.setCustomModelData(1001); // Custom model ID for the resource pack
        NamespacedKey key = new NamespacedKey(this, "ultra_crafting_block");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "ultra_crafting_table"); // Add unique tag
        ultraItem.setItemMeta(meta);
        return ultraItem;
    }

    private void createUltraCraftingTableRecipe() {
        ItemStack ultraCraftingTable = getUltraCraftingItem();

        NamespacedKey key = new NamespacedKey(this, "ultra_crafting_table");
        ShapedRecipe recipe = new ShapedRecipe(key, ultraCraftingTable);
        recipe.shape("GGG", "GCG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack craftedItem = event.getRecipe().getResult();

        if (craftedItem != null) {
            ItemMeta meta = craftedItem.getItemMeta();
            if (meta != null && "Ultra Crafting Table".equals(meta.getDisplayName())) {
                meta.setLore(List.of("An ultra-powerful crafting table!"));
                craftedItem.setItemMeta(meta);
                event.getInventory().setResult(craftedItem);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta != null) {
            NamespacedKey key = new NamespacedKey(this, "ultra_crafting_block");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                Block block = event.getBlockPlaced();
                block.setType(Material.STONE); // Ensure the block is stone to match the resource pack

                getLogger().info("Ultra Crafting Table placed with custom texture.");
            }
        }
    }
}
