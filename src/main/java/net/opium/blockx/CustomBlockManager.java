package net.opium.blockx;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockManager {

    private final JavaPlugin plugin;

    public CustomBlockManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Places a custom block at the given location with specific attributes.
     *
     * @param player The player placing the block, can be null for server placement.
     * @param location The location to place the custom block.
     * @param blockId The ID of the block for resource pack mapping.
     * @param blockName The display name for the custom block.
     * @param attributes The attributes or properties of the block.
     * @param blockMaterial The material base of the block.
     */
    public void placeBlock(Player player, Location location, int blockId, String blockName, BlockAttributes attributes, Material blockMaterial) {
        if (blockMaterial == null) {
            blockMaterial = Material.STONE; // Default block material
        }

        Block block = location.getBlock();
        block.setType(blockMaterial);

        BlockState state = block.getState();
        BlockData blockData = block.getBlockData();

        // Optionally, you can use blockData to apply custom properties if needed.

        // Example: Adding metadata for resource pack
        if (state instanceof org.bukkit.block.BlockState) {
            // Bukkit does not support setCustomModelData or setCustomName on blocks directly.
            // These should be interpreted and handled by a resource pack.

            // Placeholder for setting additional data or properties if needed
            // No direct equivalent to "applyAttributes" for blocks, so skipped.
        }

        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "You placed a custom block: " + blockName);
        }
    }

    /**
     * Generates an item representation of the block for inventory purposes.
     *
     * @param blockId The ID of the block for resource pack mapping.
     * @param blockName The display name for the custom block.
     * @param blockMaterial The material base of the block.
     * @return An ItemStack representing the custom block.
     */
    public ItemStack generateBlockItem(int blockId, String blockName, Material blockMaterial) {
        if (blockMaterial == null) {
            blockMaterial = Material.STONE; // Default block material
        }

        ItemStack blockItem = new ItemStack(blockMaterial);
        ItemMeta meta = blockItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + blockName);
            meta.setCustomModelData(blockId); // Assign a unique ID for this block/item

            blockItem.setItemMeta(meta);
        }

        return blockItem;
    }
}