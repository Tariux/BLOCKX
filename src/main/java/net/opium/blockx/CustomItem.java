package net.opium.blockx;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import org.bukkit.Material;

import org.bukkit.Location;
import org.bukkit.Particle;

public class CustomItem {

    private final CustomItemManager itemManager;
    private final JavaPlugin plugin;

    public CustomItem(JavaPlugin plugin) {
        this.itemManager = new CustomItemManager(plugin);
        this.plugin = plugin;
    }

    public void addItems(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "help" -> recommendItems(sender);
            case "point" -> executePointEmote(player);
            case "barbarian_axe" -> itemManager.generateItem(player, 12301, "Barbarian Axe", getBarbarianAxeAttributes(), null);
            case "goliath_sword" -> itemManager.generateItem(player, 12302, "Goliath Sword", getGoliathSwordAttributes(), null);
            case "ebene_sword" -> itemManager.generateItem(player, 12303, "Ebene Sword", getEbeneSwordAttributes(), null);
            case "bone_sword" -> itemManager.generateItem(player, 12304, "Bone Sword", getBoneSwordAttributes(), null);
            case "shield" -> itemManager.generateItem(player, 12305, "Shield", getMockAttributes(), Material.SHIELD);
            default -> player.sendMessage(ChatColor.RED + "Unknown item! Use 'help' for recommendations.");
        }
    }

    public void recommendItems(CommandSender sender) {
        if (sender instanceof Player player) {
            List<String> recommendations = List.of(
                    "Barbarian Axe - A mighty axe for battle enthusiasts. /xget barbarian_axe",
                    "Goliath Sword - For players who love raw power. /xget goliath_sword",
                    "Ebene Sword - A swift and elegant blade. /xget ebene_sword",
                    "Bone Sword - Spooky and unique for collectors. /xget bone_sword",
                    "Shield - defend yourself. /xget shield"
            );

            player.sendMessage(ChatColor.GREEN + "We recommend these items for you:");
            for (String recommendation : recommendations) {
                player.sendMessage(ChatColor.AQUA + "- " + recommendation);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This feature is only available to players.");
        }
    }
    private void executePointEmote(Player player) {
        
        Location eyeLocation = player.getEyeLocation();

        
        Location targetLocation = eyeLocation.clone().add(eyeLocation.getDirection().multiply(10)); 

        
        player.getWorld().spawnParticle(
                Particle.WHITE_ASH, 
                targetLocation,          
                20,                      
                0.2, 0.2, 0.2,           
                0.05                     
        );

        
        player.getWorld().playSound(
                player.getLocation(),             
                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 
                1.0f,                             
                1.0f                              
        );

        
        player.sendMessage("§aYou pointed to the location ahead!");
    }



    private ItemAttributes getMockAttributes() {
        ItemAttributes attributes = new ItemAttributes();
        return attributes;
    }

    private ItemAttributes getBarbarianAxeAttributes() {
        ItemAttributes attributes = new ItemAttributes();
        attributes.addLore(ChatColor.YELLOW + "A brutal axe forged for fierce warriors.");
        attributes.addLore(ChatColor.GRAY + "Attack Speed: Moderate");
        attributes.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 11.0, AttributeModifier.Operation.ADD_NUMBER);
        attributes.addAttribute(Attribute.GENERIC_ATTACK_SPEED, .5, AttributeModifier.Operation.ADD_SCALAR);
        return attributes;
    }

    private ItemAttributes getGoliathSwordAttributes() {
        ItemAttributes attributes = new ItemAttributes();
        attributes.addLore(ChatColor.YELLOW + "A colossal sword capable of splitting mountains.");
        attributes.addLore(ChatColor.GRAY + "Attack Speed: Slow");
        attributes.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 20.0, AttributeModifier.Operation.ADD_NUMBER);
        attributes.addAttribute(Attribute.GENERIC_ATTACK_SPEED, .1, AttributeModifier.Operation.ADD_SCALAR);
        return attributes;
    }

    private ItemAttributes getEbeneSwordAttributes() {
        ItemAttributes attributes = new ItemAttributes();
        attributes.addLore(ChatColor.YELLOW + "A blade that moves as fast as the wind.");
        attributes.addLore(ChatColor.GRAY + "Attack Speed: Fast");
        attributes.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 4.0, AttributeModifier.Operation.ADD_NUMBER);
        attributes.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 2.5, AttributeModifier.Operation.ADD_SCALAR);
        return attributes;
    }

    private ItemAttributes getBoneSwordAttributes() {
        ItemAttributes attributes = new ItemAttributes();
        attributes.addLore(ChatColor.YELLOW + "A sinister sword carved from the bones of the ancient.");
        attributes.addLore(ChatColor.GRAY + "Attack Speed: Moderate");
        attributes.addAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 3.0, AttributeModifier.Operation.ADD_NUMBER);
        attributes.addAttribute(Attribute.GENERIC_ATTACK_SPEED, 1.0, AttributeModifier.Operation.ADD_SCALAR);
        return attributes;
    }
}
