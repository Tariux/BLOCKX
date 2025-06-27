package net.opium.blockx.listeners;

import net.opium.blockx.abilities.AbilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerEventListener implements Listener {

    private final AbilityManager abilityManager;

    public PlayerEventListener(AbilityManager abilityManager) {
        this.abilityManager = abilityManager;
    }

    @EventHandler
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            // Player is the damager, pass to AbilityManager to check for custom sword abilities
            abilityManager.handleAttack(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Pass to AbilityManager to check for custom sword abilities on interact
        // The AbilityManager will internally check item and action type (e.g. right-click)
        abilityManager.handleInteract(event);
    }
}
