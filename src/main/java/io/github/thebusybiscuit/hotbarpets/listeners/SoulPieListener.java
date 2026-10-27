package io.github.thebusybiscuit.hotbarpets.listeners;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SoulPieListener implements Listener {

    private final HotbarPets plugin;
    private final HotbarPet eyamaz;

    public SoulPieListener(HotbarPets plugin) {
        this.plugin = plugin;
        eyamaz = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_EYAMAZ");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSoulHarvest(EntityDeathEvent event) {
        if (eyamaz == null) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        // The victim's death event may tick in a different Folia region from
        // its killer. Capture only the death location while on the victim's
        // region; do not read the killer's inventory or permissions here.
        Location deathLocation = event.getEntity().getLocation().clone();
        killer.getScheduler().run(plugin, task -> {
            if (!eyamaz.canUse(killer)) {
                return;
            }

            for (int slot = 0; slot < 9; slot++) {
                ItemStack item = killer.getInventory().getItem(slot);
                if (SlimefunUtils.isItemSimilar(item, eyamaz.getItem(), true)) {
                    // Reward only once per kill, even with duplicate pets.
                    // World item spawns belong to the death location's region,
                    // not to the killer's possibly distant player region.
                    plugin.getServer().getRegionScheduler().execute(plugin, deathLocation,
                            () -> deathLocation.getWorld().dropItemNaturally(deathLocation,
                                    new CustomItemStack(new ItemStack(Material.PUMPKIN_PIE), "&bSoul Pie")));
                    break;
                }
            }
        }, null);
    }
}
