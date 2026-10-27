package io.github.thebusybiscuit.hotbarpets.pets;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.hotbarpets.SimpleBasePet;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

public class BedPet extends SimpleBasePet {

    private final HotbarPets plugin;

    public BedPet(HotbarPets plugin, SlimefunItemStack item, ItemStack food, ItemStack[] recipe) {
        super(plugin.getItemGroup(), item, food, recipe);
        this.plugin = plugin;
    }

    @Override
    public void onUseItem(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
        // World time belongs to Folia's global region, not the player region.
        scheduleMorning(plugin, p.getWorld());
    }

    /** World daylight may only be mutated from the global scheduler on Folia. */
    static void scheduleMorning(HotbarPets plugin, World world) {
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> world.setTime(0L));
    }

}
