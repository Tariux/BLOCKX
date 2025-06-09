package net.opium.blockx;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
// import org.bukkit.Particle; // Removed as all references are fully qualified
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap; // Explicit import for HashMap
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BarbarianAxeAbility {

    private final JavaPlugin plugin;
    private final CustomItemManager customItemManager; // To check if it's the correct axe

    // Maps to track player states
    private final Map<UUID, Long> playerChargeStartTimes = new HashMap<>();
    private final Map<UUID, Long> playerCooldownEndTimes = new HashMap<>();
    // private final Set<UUID> chargingPlayersParticles = new HashSet<>(); // Replaced by playerParticleTasks
    private final Map<UUID, BukkitTask> playerParticleTasks = new HashMap<>();


    // Configuration
    // private static final long CHARGE_DURATION_MS = 5000; // This is not used as ability triggers on release
    private static final long COOLDOWN_DURATION_MS = 5000; // 5 seconds cooldown
    private static final int MAX_TARGET_DISTANCE = 5; // Max distance for ability
    private static final int BLOCKS_TO_BREAK = 3; // Number of blocks to break (user requested 2-3)
    private static final float EXPLOSION_POWER_TNT_EQUIVALENT = 2.0f; // Power for entity damage/knockback. TNT is 4.0f.

    public BarbarianAxeAbility(JavaPlugin plugin, CustomItemManager customItemManager) {
        this.plugin = plugin;
        this.customItemManager = customItemManager;
    }


    /**
     * Checks if the specified item is the Barbarian Axe using CustomItemManager.
     * @param itemStack The item to check.
     * @return True if it is the Barbarian Axe, false otherwise.
     */
    public boolean isItemBarbarianAxe(ItemStack itemStack) {
        if (this.customItemManager == null) { // Should not happen
            this.plugin.getLogger().warning("CustomItemManager is null in BarbarianAxeAbility.isItemBarbarianAxe");
            return false;
        }
        // Use "12301" as the string ID, and Material.IRON_AXE
        return this.customItemManager.isSpecificCustomItem(itemStack, "12301", Material.IRON_AXE);
    }

    /**
     * Checks if the player is currently charging the axe ability.
     * @param player The player to check.
     * @return True if charging, false otherwise.
     */
    public boolean isCharging(Player player) {
        return playerChargeStartTimes.containsKey(player.getUniqueId());
    }

    /**
     * Checks if the player is currently on cooldown for the axe ability.
     * @param player The player to check.
     * @return True if on cooldown, false otherwise.
     */
    public boolean isOnCooldown(Player player) {
        return playerCooldownEndTimes.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    /**
     * Initiates the charging sequence for the player.
     * Assumes pre-checks (cooldown, item) are done by caller (e.g., Blockx event handler).
     * @param player The player starting to charge.
     */
    public void startCharging(Player player) {
        playerChargeStartTimes.put(player.getUniqueId(), System.currentTimeMillis());

        if (playerParticleTasks.containsKey(player.getUniqueId())) {
            playerParticleTasks.get(player.getUniqueId()).cancel(); // Cancel existing task (defensive)
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !playerChargeStartTimes.containsKey(player.getUniqueId())) {
                    this.cancel(); // Stop task if player logs off or is no longer charging
                    playerParticleTasks.remove(player.getUniqueId());
                    return;
                }

                // Optional: Check if player is still holding the axe. If not, cancel charge.
                // This requires isActuallyBarbarianAxe to be accessible or item passed.
                // if (!isActuallyBarbarianAxe(player.getInventory().getItemInMainHand())) {
                //    cancelCharge(player); // cancelCharge will handle task cancellation.
                //    return;
                // }

                Location particleLoc = player.getLocation().add(0, player.getHeight() * 0.7, 0); // Eye-level approx
                // player.getWorld().spawnParticle(org.bukkit.Particle.CRIT_MAGIC, particleLoc, 5, 0.4, 0.4, 0.4, 0.05);
                player.getWorld().spawnParticle(org.bukkit.Particle.FLAME, particleLoc, 2, 0.3, 0.3, 0.3, 0.02);
            }
        }.runTaskTimer(this.plugin, 0L, 7L); // Particles every 7 ticks

        playerParticleTasks.put(player.getUniqueId(), task);
        // plugin.getLogger().info(player.getName() + " started charging Barbarian Axe."); // Logged in Blockx
    }

    /**
     * Handles the release of the charge, potentially triggering the ability.
     * @param player The player releasing the charge.
     */
    public void releaseCharge(Player player) {
        if (!isCharging(player)) { // Check if player was actually charging
            return;
        }

        // Stop and remove particle task associated with charging
        if (playerParticleTasks.containsKey(player.getUniqueId())) {
            playerParticleTasks.get(player.getUniqueId()).cancel();
            playerParticleTasks.remove(player.getUniqueId());
        }

        long chargeStartTime = playerChargeStartTimes.remove(player.getUniqueId()); // Get and remove start time
        long chargedTimeMillis = System.currentTimeMillis() - chargeStartTime;

        long minHoldTimeForAbilityMs = 100; // Minimum 0.1s hold to trigger ability

        if (chargedTimeMillis >= minHoldTimeForAbilityMs) {
            Location targetLocation = getTargetLocation(player); // Method to get target block's location
            if (targetLocation != null) {
                triggerAbility(player, targetLocation); // triggerAbility will handle effects and cooldown
            } else {
                player.sendMessage(ChatColor.RED + "No target in range for the axe ability!");
                // No cooldown if ability doesn't trigger due to no target
            }
        } else {
            // Player released too soon, no ability trigger, no cooldown.
            // Optional feedback: player.sendMessage(ChatColor.YELLOW + "Axe charge released too soon.");
        }
        // plugin.getLogger().info(player.getName() + " released Barbarian Axe charge."); // Logged in Blockx
    }

    /**
     * Triggers the barbarian axe's special ability.
     * @param player The player using the ability.
     * @param targetLocation The location the ability is aimed at.
     */
    private void triggerAbility(Player player, Location targetLocation) {
        World world = targetLocation.getWorld();
        if (world == null) {
            plugin.getLogger().warning("Failed to trigger Barbarian Axe ability: target location has no world.");
            return;
        }

        plugin.getLogger().info(player.getName() + " used Barbarian Axe ability at " + targetLocation.getBlockX() + "," + targetLocation.getBlockY() + "," + targetLocation.getBlockZ());

        // 1. Visual and Sound Effects
        world.playSound(targetLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.8f); // Slightly adjusted sound
        // world.spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, targetLocation.clone().add(0.5, 0.5, 0.5), 1); // Main explosion particle
        world.spawnParticle(org.bukkit.Particle.LAVA, targetLocation.clone().add(0.5, 0.5, 0.5), 8, 0.5, 0.5, 0.5, 0.1); // Some lava/fire particles
        // world.spawnParticle(org.bukkit.Particle.SMOKE_LARGE, targetLocation.clone().add(0.5, 0.5, 0.5), 10, 0.7, 0.7, 0.7, 0.05); // Smoke

        // 2. Break Blocks
        List<Block> nearbyBlocks = new ArrayList<>();
        int radius = 1; // Search radius for blocks to break (e.g., 3x3x3 area around target)
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) <= radius * 2) {
                        Block currentBlock = targetLocation.clone().add(x, y, z).getBlock();
                        if (currentBlock.getType() != Material.AIR && currentBlock.getType() != Material.BEDROCK) {
                            nearbyBlocks.add(currentBlock);
                        }
                    }
                }
            }
        }
        Collections.shuffle(nearbyBlocks);
        int blocksBroken = 0;
        for (Block blockToBreak : nearbyBlocks) {
            if (blocksBroken >= BLOCKS_TO_BREAK) {
                break;
            }
            // world.spawnParticle(org.bukkit.Particle.BLOCK_CRACK, blockToBreak.getLocation().clone().add(0.5, 0.5, 0.5), 20, blockToBreak.getBlockData());
            blockToBreak.setType(Material.AIR);
            blocksBroken++;
        }

        // 3. Damage Entities and Knockback (using createExplosion for TNT-like entity effects)
        // The 'fire' parameter being false means no fire is started by the explosion.
        // The 'breakBlocks' parameter being false means it won't break additional blocks beyond what we manually did.
        targetLocation.getWorld().createExplosion(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), EXPLOSION_POWER_TNT_EQUIVALENT, false, false);
        // Note: createExplosion has its own sound/particles. Our manual ones above are for enhancement.

        setCooldown(player); // IMPORTANT: Apply cooldown after successful ability trigger
    }

    /**
     * Sets the player on cooldown.
     * @param player The player to set on cooldown.
     */
    private void setCooldown(Player player) {
        playerCooldownEndTimes.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_DURATION_MS);
        // Optional: send cooldown message or visual cue
    }

    /**
     * Gets the target location for the ability.
     * @param player The player.
     * @return The target location, or null if no valid target.
     */
    private Location getTargetLocation(Player player) {
        // Gets the block the player is looking at within MAX_TARGET_DISTANCE.
        Block targetBlock = player.getTargetBlock(null, MAX_TARGET_DISTANCE);

        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            // If no block is targeted or it's air, ability might not trigger or trigger in air.
            // For now, allow targeting air for effects like ground slam, but ensure block is not null.
            if (targetBlock == null) return null;
        }
        return targetBlock.getLocation();
    }

    /**
     * Cancels the charging state for a player without triggering the ability or cooldown.
     * Useful if they switch items, or an external factor interrupts charging.
     * @param player The player.
     */
    public void cancelCharge(Player player) {
        if (!isCharging(player)) { // Only act if player is in a charging state
            return;
        }
        playerChargeStartTimes.remove(player.getUniqueId()); // Remove from charging state
        if (playerParticleTasks.containsKey(player.getUniqueId())) {
            playerParticleTasks.get(player.getUniqueId()).cancel(); // Stop particles
            playerParticleTasks.remove(player.getUniqueId()); // Clean up task map
        }
        // plugin.getLogger().info("Axe charge cancelled for " + player.getName()); // Optional log
    }

    /**
     * Handles player quitting (for cleanup).
     * @param player The player who quit.
     */
    public void handlePlayerQuit(Player player) {
        playerChargeStartTimes.remove(player.getUniqueId()); // Stop charging state
        if (playerParticleTasks.containsKey(player.getUniqueId())) {
            playerParticleTasks.get(player.getUniqueId()).cancel();
            playerParticleTasks.remove(player.getUniqueId()); // Clean up particle task
        }
        // Cooldowns naturally expire.
    }

    // Getters for constants if needed by other classes (e.g., for messages or Blockx)
    public static long getCooldownDurationMs() {
        return COOLDOWN_DURATION_MS;
    }

    // public static int getBlocksToBreak() { // Example if needed externally
    //    return BLOCKS_TO_BREAK;
    // }
}
