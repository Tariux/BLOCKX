package net.opium.blockx.abilities;

import net.opium.blockx.items.CustomSwordType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BonusDamageAbility implements SwordAbility {

    private final CustomSwordType associatedType;
    private final double extraDamage;
    private final PotionEffectType effectOnHit; // Optional: Apply a potion effect
    private final int effectDuration; // Ticks
    private final int effectAmplifier;

    public BonusDamageAbility(CustomSwordType associatedType, double extraDamage) {
        this(associatedType, extraDamage, null, 0, 0);
    }

    public BonusDamageAbility(CustomSwordType associatedType, double extraDamage, PotionEffectType effectOnHit, int effectDuration, int effectAmplifier) {
        this.associatedType = associatedType;
        this.extraDamage = extraDamage;
        this.effectOnHit = effectOnHit;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack sword, EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() + extraDamage);
        // Attacker.sendMessage(ChatColor.GREEN + "Bonus damage dealt!"); // For debugging

        if (effectOnHit != null && effectDuration > 0) {
            victim.addPotionEffect(new PotionEffect(effectOnHit, effectDuration, effectAmplifier));
        }
    }

    @Override
    public void onInteract(Player player, ItemStack sword, PlayerInteractEvent event) {
        // This ability does not have a right-click interaction
    }

    @Override
    public CustomSwordType getAssociatedSwordType() {
        return associatedType;
    }
}
