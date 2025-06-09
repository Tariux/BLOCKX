package net.opium.blockx;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemAttributes {
    private final List<String> lore = new ArrayList<>();
    private final Map<Attribute, AttributeModifier> attributeModifiers = new HashMap<>();

    
    public void addLore(String line) {
        lore.add(line);
    }

    
    public void addAttribute(Attribute attribute, double value, AttributeModifier.Operation operation) {
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.name(), value, operation);
        attributeModifiers.put(attribute, modifier);
    }

    public List<String> getLore() {
        return lore;
    }

    
    public void applyAttributes(ItemMeta meta) {
        for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers.entrySet()) {
            meta.addAttributeModifier(entry.getKey(), entry.getValue());
        }
    }
}
