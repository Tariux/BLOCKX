package net.opium.blockx.items;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot; // Required for slot-specific attributes
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class to manage lore and Bukkit attributes for custom items.
 */
public class ItemAttributes {
    private final List<String> lore = new ArrayList<>();
    // Store AttributeData which includes slot information
    private final List<AttributeData> attributeDataList = new ArrayList<>();

    /**
     * Adds a line of lore to the item.
     * Color codes using '&' will be translated by CustomItemManager.
     * @param line The lore line to add.
     */
    public void addLore(String line) {
        lore.add(line);
    }

    /**
     * Adds an attribute modifier to the item.
     * By default, this attribute will apply when the item is in the main hand.
     * @param attribute The Bukkit Attribute to modify.
     * @param value The value of the modification.
     * @param operation The operation to apply.
     */
    public void addAttribute(Attribute attribute, double value, AttributeModifier.Operation operation) {
        // Default to MAINHAND if no slot is specified
        addAttribute(attribute, value, operation, EquipmentSlot.HAND);
    }

    /**
     * Adds an attribute modifier to the item for a specific equipment slot.
     * @param attribute The Bukkit Attribute to modify.
     * @param value The value of the modification.
     * @param operation The operation to apply.
     * @param slot The EquipmentSlot this attribute is active in. Can be null for general attributes.
     */
    public void addAttribute(Attribute attribute, double value, AttributeModifier.Operation operation, EquipmentSlot slot) {
        // Using the attribute's name for the modifier name can be problematic if multiple modifiers for the same attribute.
        // A unique name is better, often derived from the plugin or item.
        String modifierName = "blockx." + attribute.name().toLowerCase().replace("generic.", "") + "." + UUID.randomUUID().toString().substring(0, 4);
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), modifierName, value, operation, slot);
        attributeDataList.add(new AttributeData(attribute, modifier));
    }


    public List<String> getLore() {
        return new ArrayList<>(lore); // Return a copy to prevent external modification
    }

    /**
     * Applies all configured attributes to the given ItemMeta.
     * @param meta The ItemMeta to apply attributes to.
     */
    public void applyAttributes(ItemMeta meta) {
        if (meta == null) return;
        for (AttributeData data : attributeDataList) {
            meta.addAttributeModifier(data.attribute, data.modifier);
        }
    }

    // Inner class to hold attribute and its modifier, including slot if needed
    private static class AttributeData {
        Attribute attribute;
        AttributeModifier modifier;

        AttributeData(Attribute attribute, AttributeModifier modifier) {
            this.attribute = attribute;
            this.modifier = modifier;
        }
    }
}
