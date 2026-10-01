package io.github.thebusybiscuit.hotbarpets.listeners;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.hotbarpets.PetEntityData;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileListener implements Listener {

    private final HotbarPets plugin;

    public ProjectileListener(HotbarPets plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTippedArrowHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow && PetEntityData.isPetProjectile(plugin, e.getEntity())) {
            PetEntityData.clearProjectileMarker(plugin, e.getEntity());
            e.getEntity().remove();
        }
    }
}
