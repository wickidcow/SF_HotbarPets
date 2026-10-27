package io.github.thebusybiscuit.hotbarpets.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

public class PhantomListener implements Listener {

    private final HotbarPet panda;

    public PhantomListener(HotbarPets plugin) {
        panda = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_PANDA");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Another plugin may have already denied targeting this player.
    // Do not spend Panda food or remove entities for cancelled targets.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPhantomSpawn(EntityTargetLivingEntityEvent e) {
        Player player = proposedPhantomTarget(e);
        if (player == null || panda == null) {
            return;
        }
        if (!hasPandaPet(player) || !panda.checkAndConsumeFood(player)) {
            return;
        }

        e.setCancelled(true);
        e.getEntity().remove();
    }

    /**
     * The proposed target in this event may not have been applied to the
     * Phantom entity yet. Never inspect Phantom.getTarget() here.
     */
    static Player proposedPhantomTarget(EntityTargetLivingEntityEvent event) {
        return event.getEntityType() == EntityType.PHANTOM
                && event.getTarget() instanceof Player player ? player : null;
    }

    private boolean hasPandaPet(Player player) {
        for (int i = 0; i < 9; i++) {
            if (panda.isItem(player.getInventory().getItem(i))) {
                return true;
            }
        }

        return false;
    }

}
