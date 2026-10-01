package io.github.thebusybiscuit.hotbarpets.listeners;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.hotbarpets.PetEntityData;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import java.util.Iterator;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TNTListener implements Listener {

    private final HotbarPets plugin;

    public TNTListener(HotbarPets plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTNTDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof TNTPrimed tnt)) {
            return;
        }

        UUID ownerId = PetEntityData.getTntOwner(plugin, tnt);
        if (ownerId == null) {
            return;
        }

        Player attacker = Bukkit.getPlayer(ownerId);
        if (attacker == null) {
            e.setCancelled(true);
        } else if (!Slimefun.getProtectionManager()
                .hasPermission(attacker, e.getEntity().getLocation(), Interaction.ATTACK_PLAYER)) {
            e.setCancelled(true);
            attacker.sendMessage(Component.text("You cannot harm Players in here!", NamedTextColor.DARK_RED));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTNTExplode(EntityExplodeEvent e) {
        if (e.getEntityType() != EntityType.TNT) {
            return;
        }

        UUID ownerId = PetEntityData.getTntOwner(plugin, e.getEntity());
        if (ownerId == null) {
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerId);
        PetEntityData.clearTntOwner(plugin, e.getEntity());

        Iterator<Block> blocks = e.blockList().iterator();
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (!Slimefun.getProtectionManager().hasPermission(player, block, Interaction.BREAK_BLOCK)) {
                blocks.remove();
            }
        }

        if (e.blockList().isEmpty()) {
            e.setCancelled(true);
        }
    }
}
