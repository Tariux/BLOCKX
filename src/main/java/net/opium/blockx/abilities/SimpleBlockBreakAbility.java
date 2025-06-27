package net.opium.blockx.abilities;

import net.opium.blockx.items.CustomSwordType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class SimpleBlockBreakAbility implements SwordAbility {

    private final CustomSwordType associatedType;
    private final Set<Material> breakableBlocks = new HashSet<>();

    public SimpleBlockBreakAbility(CustomSwordType associatedType) {
        this.associatedType = associatedType;
        // Define some default weak/decorative blocks this ability can break
        breakableBlocks.add(Material.COBWEB);
        breakableBlocks.add(Material.OAK_LEAVES);
        breakableBlocks.add(Material.SPRUCE_LEAVES);
        breakableBlocks.add(Material.BIRCH_LEAVES);
        breakableBlocks.add(Material.JUNGLE_LEAVES);
        breakableBlocks.add(Material.ACACIA_LEAVES);
        breakableBlocks.add(Material.DARK_OAK_LEAVES);
        breakableBlocks.add(Material.AZALEA_LEAVES);
        breakableBlocks.add(Material.FLOWERING_AZALEA_LEAVES);
        breakableBlocks.add(Material.GLASS_PANE);
        breakableBlocks.add(Material.GLASS);
        breakableBlocks.add(Material.VINE);
        breakableBlocks.add(Material.GRASS);
        breakableBlocks.add(Material.TALL_GRASS);

    }

    public SimpleBlockBreakAbility(CustomSwordType associatedType, Set<Material> specificBreakableBlocks) {
        this.associatedType = associatedType;
        if (specificBreakableBlocks != null && !specificBreakableBlocks.isEmpty()) {
            this.breakableBlocks.addAll(specificBreakableBlocks);
        } else {
            // Fallback to defaults if null or empty set passed
            this.breakableBlocks.add(Material.COBWEB);
            this.breakableBlocks.add(Material.OAK_LEAVES);
        }
    }


    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack sword, EntityDamageByEntityEvent event) {
        // This ability does not trigger on attack
    }

    @Override
    public void onInteract(Player player, ItemStack sword, PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && breakableBlocks.contains(clickedBlock.getType())) {
                // Optional: check for game mode, permissions, etc.
                // Optional: add a small delay or cost (durability, hunger, cooldown)
                clickedBlock.breakNaturally(sword); // Break the block and drop items as if broken by the sword
                event.setCancelled(true); // Prevent normal right-click action on the block
                // player.sendMessage(ChatColor.GREEN + "Shattered " + clickedBlock.getType().toString().toLowerCase() + "!");
            }
        }
    }

    @Override
    public CustomSwordType getAssociatedSwordType() {
        return associatedType;
    }
}
