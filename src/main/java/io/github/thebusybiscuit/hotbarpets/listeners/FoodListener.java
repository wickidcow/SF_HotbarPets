package io.github.thebusybiscuit.hotbarpets.listeners;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

public class FoodListener implements Listener {

    private final HotbarPets plugin;
    private final HotbarPet pig;
    private final HotbarPet zombie;

    public FoodListener(HotbarPets plugin) {
        this.plugin = plugin;

        pig = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_PIG");
        zombie = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_ZOMBIE");

        // All other pet listeners self-register. Without this line the Pig
        // and Zombie PlayerItemConsumeEvent abilities never receive events.
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Runs the Pig reward only when its two-tick-later food debit and optional
     * entitlement check actually succeed. Keep mutations on the player's
     * owning entity scheduler for Paper and Folia.
     */
    static void schedulePigEffect(Plugin plugin, HotbarPet pig, Player p) {
        p.getScheduler().runDelayed(plugin, task -> {
            if (!pig.checkAndConsumeFood(p)) {
                return;
            }
            p.setSaturation(p.getSaturation() + 8.0F);
            p.removePotionEffect(PotionEffectType.POISON);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PIG_AMBIENT, 1.0F, 2.0F);
        }, null, 2L);
    }

    // Observe the final consumption outcome after higher-priority protection
    // plugins, without changing the original vanilla item-consumption event.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        boolean pigQueued = false;
        boolean zombieQueued = false;
        // Several copies of one pet must not queue duplicate payouts for
        // one food event. Pig and Zombie remain independent abilities.
        for (int i = 0; i < 9; ++i) {
            ItemStack item = p.getInventory().getItem(i);

            if (!pigQueued && pig != null && pig.canUse(p)
                    && SlimefunUtils.isItemSimilar(item, pig.getItem(), true)) {
                // Payment is verified when the entity-owned callback executes.
                // A storage-only precheck would incorrectly reject offhand food.
                schedulePigEffect(plugin, pig, p);
                pigQueued = true;
            } else if (!zombieQueued && zombie != null && zombie.canUse(p)
                    && SlimefunUtils.isItemSimilar(e.getItem(), new ItemStack(Material.ROTTEN_FLESH), true)
                    && SlimefunUtils.isItemSimilar(item, zombie.getItem(), true)) {
                p.getScheduler().runDelayed(plugin, task -> {
                    if (!zombie.canUse(p)) {
                        return;
                    }
                    p.removePotionEffect(PotionEffectType.HUNGER);
                }, null, 2L);
                zombieQueued = true;
            }
            if (pigQueued && zombieQueued) {
                break;
            }
        }
    }
}
