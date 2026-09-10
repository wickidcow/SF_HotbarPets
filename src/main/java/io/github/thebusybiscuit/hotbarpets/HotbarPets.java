package io.github.thebusybiscuit.hotbarpets;

import io.github.thebusybiscuit.hotbarpets.groups.BossMobs;
import io.github.thebusybiscuit.hotbarpets.groups.FarmAnimals;
import io.github.thebusybiscuit.hotbarpets.groups.HostileMobs;
import io.github.thebusybiscuit.hotbarpets.groups.PassiveMobs;
import io.github.thebusybiscuit.hotbarpets.groups.PeacefulAnimals;
import io.github.thebusybiscuit.hotbarpets.groups.SpecialPets;
import io.github.thebusybiscuit.hotbarpets.groups.UtilityPets;
import io.github.thebusybiscuit.hotbarpets.listeners.DamageListener;
import io.github.thebusybiscuit.hotbarpets.listeners.FoodListener;
import io.github.thebusybiscuit.hotbarpets.listeners.GeneralListener;
import io.github.thebusybiscuit.hotbarpets.listeners.PhantomListener;
import io.github.thebusybiscuit.hotbarpets.listeners.ProjectileListener;
import io.github.thebusybiscuit.hotbarpets.listeners.SoulPieListener;
import io.github.thebusybiscuit.hotbarpets.listeners.TNTListener;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class HotbarPets extends JavaPlugin implements Listener, SlimefunAddon {

    private ItemGroup itemGroup;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new Metrics(this, 4859);

        ItemStack icon = PetTexture.CATEGORY.getAsItem().clone();
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Hotbar Pets", NamedTextColor.LIGHT_PURPLE));
            meta.lore(List.of(Component.empty(), Component.text("> Click to open", NamedTextColor.GREEN)));
            icon.setItemMeta(meta);
        }
        itemGroup = new ItemGroup(new NamespacedKey(this, "pets"), icon);

        new FarmAnimals(this);
        new PeacefulAnimals(this);
        new PassiveMobs(this);
        new HostileMobs(this);
        new BossMobs(this);
        new UtilityPets(this);
        new SpecialPets(this);

        new DamageListener(this);
        new FoodListener(this);
        new GeneralListener(this);
        new PhantomListener(this);
        new ProjectileListener(this);
        new SoulPieListener(this);
        new TNTListener(this);

        HotbarPetsRunnable petTask = new HotbarPetsRunnable();
        long productionInterval = Math.max(20L, getConfig().getLong("production-interval-ticks", 2000L));
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, ignored -> {
            for (Player player : getServer().getOnlinePlayers()) {
                player.getScheduler().run(this, task -> petTask.tick(player), null);
            }
        }, 1L, productionInterval);
    }

    @Override
    public void onDisable() {
        HotbarPet.getMessageDelay().clear();
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public boolean isModernPetEnabled(String key) {
        return getConfig().getBoolean("modern-pets." + key + ".enabled", true);
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/wickidcow/SF_HotbarPets/issues";
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }
}
