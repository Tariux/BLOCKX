package net.opium.blockx.abilities;

import net.opium.blockx.items.CustomItemManager;
import net.opium.blockx.items.CustomSwordType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType; // Needed for Bone Sword ability

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityManager {

    private final JavaPlugin plugin;
    private final CustomItemManager customItemManager;
    private final Map<String, List<SwordAbility>> swordAbilitiesMap = new HashMap<>();

    public AbilityManager(JavaPlugin plugin, CustomItemManager customItemManager) {
        this.plugin = plugin;
        this.customItemManager = customItemManager;
        registerAllAbilities(); // Changed from registerInitialAbilities
    }

    // Renamed and updated to include all new abilities
    private void registerAllAbilities() {
        for (CustomSwordType type : CustomSwordType.values()) {
            List<SwordAbility> abilitiesForType = new ArrayList<>();

            // All swords get simple particles
            abilitiesForType.add(new SimpleParticleAbility(type));

            // Add specific abilities based on sword type
            switch (type) {
                case EBENE_SWORD:
                    abilitiesForType.add(new BonusDamageAbility(type, 2.0)); // Ebene does +2 damage
                    abilitiesForType.add(new SimpleBlockBreakAbility(type)); // Ebene can break weak blocks
                    plugin.getLogger().info("Registered Ebene Sword with Bonus Damage and Simple Block Break.");
                    break;
                case GOLIATH_SWORD:
                    abilitiesForType.add(new BonusDamageAbility(type, 4.0)); // Goliath does +4 damage
                    plugin.getLogger().info("Registered Goliath Sword with Bonus Damage.");
                    break;
                case BONE_SWORD:
                    // Bone sword gets bonus damage and a wither effect on hit
                    abilitiesForType.add(new BonusDamageAbility(type, 1.0, PotionEffectType.WITHER, 100, 0)); // +1 damage, Wither I for 5s (100 ticks)
                    plugin.getLogger().info("Registered Bone Sword with Bonus Damage and Wither effect.");
                    break;
                default:
                    // Fallback for any other sword types that might be added
                    abilitiesForType.add(new BonusDamageAbility(type, 0.5));
                    plugin.getLogger().info("Registered " + type.name() + " with default Bonus Damage.");
                    break;
            }
            swordAbilitiesMap.put(String.valueOf(type.getCustomModelData()), abilitiesForType);
        }
        plugin.getLogger().info("Completed registration of abilities for " + swordAbilitiesMap.size() + " custom sword types.");
    }


    public void handleAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        // Allow victim to be any LivingEntity, not just Player
        if (!(event.getEntity() instanceof LivingEntity victim)) return;


        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon == null || !weapon.hasItemMeta()) return;

        String customItemId = customItemManager.getCustomItemId(weapon);
        if (customItemId == null) return;

        List<SwordAbility> abilities = swordAbilitiesMap.get(customItemId);
        if (abilities != null && !abilities.isEmpty()) {
            for (SwordAbility ability : abilities) {
                ability.onAttack(attacker, victim, weapon, event);
            }
        }
    }

    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || !itemInHand.hasItemMeta()) return;

        String customItemId = customItemManager.getCustomItemId(itemInHand);
        if (customItemId == null) return;

        List<SwordAbility> abilities = swordAbilitiesMap.get(customItemId);
        if (abilities != null && !abilities.isEmpty()) {
            if (event.getAction().name().contains("RIGHT_CLICK")) {
                for (SwordAbility ability : abilities) {
                    ability.onInteract(player, itemInHand, event);
                }
            }
        }
    }
}
