package io.github.thebusybiscuit.hotbarpets.pets;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.hotbarpets.PetEntityData;
import io.github.thebusybiscuit.hotbarpets.SimpleBasePet;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShulkerPet extends SimpleBasePet {

    private final HotbarPets plugin;

    public ShulkerPet(HotbarPets plugin, SlimefunItemStack item, ItemStack food, ItemStack[] recipe) {
        super(plugin.getItemGroup(), item, food, recipe);
        this.plugin = plugin;
    }

    @Override
    public void onUseItem(Player p) {
        Arrow arrow = p.launchProjectile(Arrow.class);
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 0), true);
        PetEntityData.markProjectile(plugin, arrow);

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SHULKER_AMBIENT, 1.0F, 2.0F);
    }
}
