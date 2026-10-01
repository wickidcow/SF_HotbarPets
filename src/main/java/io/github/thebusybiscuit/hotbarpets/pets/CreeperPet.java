package io.github.thebusybiscuit.hotbarpets.pets;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.hotbarpets.PetEntityData;
import io.github.thebusybiscuit.hotbarpets.SimpleBasePet;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

public class CreeperPet extends SimpleBasePet {

    private final HotbarPets plugin;

    public CreeperPet(HotbarPets plugin, SlimefunItemStack item, ItemStack food, ItemStack[] recipe) {
        super(plugin.getItemGroup(), item, food, recipe);
        this.plugin = plugin;
    }

    @Override
    public void onUseItem(Player p) {
        TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(p.getLocation(), EntityType.TNT);
        PetEntityData.setTntOwner(plugin, tnt, p.getUniqueId());
        tnt.setFuseTicks(0);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0F, 2.0F);
    }
}
