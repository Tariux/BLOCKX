package net.opium.blockx.core;

import net.opium.blockx.abilities.AbilityManager;
import net.opium.blockx.abilities.BarbarianAxeAbility;
import net.opium.blockx.commands.CommandHandler;
import net.opium.blockx.items.CustomItemManager;
import net.opium.blockx.listeners.PlayerEventListener; // Import the new listener
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class Blockx extends JavaPlugin implements Listener {

    private CustomItemManager customItemManager;
    private BarbarianAxeAbility barbarianAxeAbility;
    private AbilityManager abilityManager; // Manager for general sword abilities

    @Override
    public void onEnable() {
        getLogger().info("Blockx Plugin Enabled");

        // Initialize managers
        this.customItemManager = new CustomItemManager(this);
        this.barbarianAxeAbility = new BarbarianAxeAbility(this, this.customItemManager);
        this.abilityManager = new AbilityManager(this, this.customItemManager); // Initialize AbilityManager

        // Register command executor
        this.getCommand("xget").setExecutor(new CommandHandler(this, this.customItemManager));

        // Register events
        getServer().getPluginManager().registerEvents(this, this); // For Blockx's own @EventHandlers
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this.abilityManager), this); // Register new PlayerEventListener

        createUltraCraftingTableRecipe();

        getLogger().info("Blockx Plugin Systems Initialized.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Blockx Plugin Disabled");
    }

    private ItemStack getUltraCraftingItem() {
        ItemStack ultraItem = new ItemStack(Material.STONE);
        ItemMeta meta = ultraItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Ultra Crafting Table");
            meta.setCustomModelData(1001);
            NamespacedKey key = new NamespacedKey(this, "ultra_crafting_block");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "ultra_crafting_table");
            ultraItem.setItemMeta(meta);
        }
        return ultraItem;
    }

    private void createUltraCraftingTableRecipe() {
        ItemStack ultraCraftingTable = getUltraCraftingItem();
        if (ultraCraftingTable.getItemMeta() == null) return;

        NamespacedKey key = new NamespacedKey(this, "ultra_crafting_table_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, ultraCraftingTable);
        recipe.shape("GGG", "GCG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        if (Bukkit.getRecipe(key) == null) {
            Bukkit.addRecipe(recipe);
        }
    }

    // Event Handlers specific to Blockx main class (e.g. Ultra Crafting Table)
    // Barbarian Axe and general sword abilities are now handled by their respective managers/listeners

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack craftedItem = event.getRecipe().getResult();
        if (craftedItem == null || craftedItem.getItemMeta() == null) return;

        ItemMeta meta = craftedItem.getItemMeta();
        if ("Ultra Crafting Table".equals(meta.getDisplayName())) {
            NamespacedKey key = new NamespacedKey(this, "ultra_crafting_block");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                ItemStack newResult = craftedItem.clone();
                ItemMeta newMeta = newResult.getItemMeta();
                if (newMeta != null) {
                    newMeta.setLore(List.of("An ultra-powerful crafting table!"));
                    newResult.setItemMeta(newMeta);
                    event.getInventory().setResult(newResult);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        if (itemInHand == null || itemInHand.getItemMeta() == null) return;

        ItemMeta meta = itemInHand.getItemMeta();
        NamespacedKey key = new NamespacedKey(this, "ultra_crafting_block");

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String tagValue = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if ("ultra_crafting_table".equals(tagValue)) {
                getLogger().info("Ultra Crafting Table placed with custom texture.");
            }
        }
    }

    // Barbarian Axe specific events are still here for now, but could be moved
    // to a dedicated BarbarianAxeListener if its logic becomes more complex
    // or if PlayerEventListener becomes too crowded.
    // For now, PlayerEventListener handles general sword abilities.
    // Blockx's onPlayerInteract is for BarbarianAxe a d PlayerEventListener's onPlayerInteract is for general swords.
    // This might lead to double processing if not careful.
    // Let's ensure PlayerEventListener handles general swords and BarbarianAxeAbility handles its own interact.
    // The current PlayerEventListener calls abilityManager.handleInteract for *all* interactions.
    // The BarbarianAxeAbility's onInteract is NOT CALLED by PlayerEventListener.
    // It's called by the onBarbarianAxeInteract handler below. This is fine.

    @EventHandler
    public void onBarbarianAxeInteract(PlayerInteractEvent event) { // This is for Barbarian Axe only
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (barbarianAxeAbility.isItemBarbarianAxe(itemInHand)) {
            // Let BarbarianAxeAbility handle its own interaction logic fully
            // including checking for right click etc.
            // barbarianAxeAbility.handleInteract(event); // if it had such a method
             if (event.getAction().name().contains("RIGHT_CLICK")) {
                if (barbarianAxeAbility.isCharging(player)) {
                    barbarianAxeAbility.releaseCharge(player);
                } else if (!barbarianAxeAbility.isOnCooldown(player)) {
                    barbarianAxeAbility.startCharging(player);
                }
                 // Potentially cancel the event if the axe interaction should be exclusive
                 // event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        barbarianAxeAbility.handlePlayerQuit(event.getPlayer());
        // If general AbilityManager needs cleanup on player quit, call it here too
        // e.g. abilityManager.handlePlayerQuit(event.getPlayer());
    }
}
