package net.opium.blockx.abilities;

import net.opium.blockx.items.CustomSwordType;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A simple ability that adds particle effects on attack.
 * This serves as a basic implementation of SwordAbility.
 */
public class SimpleParticleAbility implements SwordAbility {

    private final CustomSwordType associatedType; // To know which sword this is for, if needed

    public SimpleParticleAbility(CustomSwordType associatedType) {
        this.associatedType = associatedType;
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack sword, EntityDamageByEntityEvent event) {
        // Example: Spawn particles at the victim's location
        victim.getWorld().spawnParticle(Particle.CRIT_MAGIC, victim.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        if (associatedType != null) {
            // Attacker.sendMessage("Particle ability triggered for " + associatedType.getDisplayName());
        }
    }

    @Override
    public void onInteract(Player player, ItemStack sword, PlayerInteractEvent event) {
        // This ability might not do anything on right-click, or it could have a different effect.
        // For example, a small burst of particles around the player.
        if (event.getAction().name().contains("RIGHT_CLICK")) {
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.2);
            if (associatedType != null) {
                // player.sendMessage("Interact particle ability triggered for " + associatedType.getDisplayName());
            }
        }
    }

    @Override
    public CustomSwordType getAssociatedSwordType() {
        return associatedType; // Can be null if this ability is generic
    }
}
