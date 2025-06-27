package net.opium.blockx.abilities;

import net.opium.blockx.items.CustomSwordType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Interface for defining abilities associated with custom swords.
 */
public interface SwordAbility {

    /**
     * Called when a player attacks an entity with a sword having this ability.
     *
     * @param attacker The player attacking.
     * @param victim   The entity being attacked.
     * @param sword    The ItemStack of the sword used.
     * @param event    The original EntityDamageByEntityEvent.
     */
    void onAttack(Player attacker, LivingEntity victim, ItemStack sword, EntityDamageByEntityEvent event);

    /**
     * Called when a player right-clicks with a sword having this ability.
     *
     * @param player The player interacting.
     * @param sword  The ItemStack of the sword used.
     * @param event  The original PlayerInteractEvent.
     */
    void onInteract(Player player, ItemStack sword, PlayerInteractEvent event);

    /**
     * Gets the specific sword type this ability is for.
     * This might be used by an AbilityManager to register abilities.
     * Alternatively, abilities could be more generic and applicable to multiple sword types.
     * For now, let's assume a direct link or that the manager handles mapping.
     *
     * @return The CustomSwordType this ability is associated with.
     *         Returning null might imply it's a generic ability component.
     */
    CustomSwordType getAssociatedSwordType(); // Optional: Could be handled by AbilityManager's registration logic

    // Ideas for other common ability methods:
    // void onEquip(Player player, ItemStack sword);
    // void onUnequip(Player player, ItemStack sword);
    // void passiveTick(Player player, ItemStack sword); // For effects like particle trails
}
