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
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HotbarPets extends JavaPlugin implements Listener, SlimefunAddon {

    private ItemGroup itemGroup;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new Metrics(this, 4859);

        itemGroup = new ItemGroup(
            new NamespacedKey(this, "pets"),
            new CustomItemStack(PetTexture.CATEGORY.getAsItem(), "&dHotbar Pets", "", "&a> Click to open")
        );

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
        getServer().getGlobalRegionScheduler().runAtFixedRate(this, ignored -> {
            for (Player player : getServer().getOnlinePlayers()) {
                player.getScheduler().run(this, task -> petTask.tick(player), null);
            }
        }, 1L, 2000L);
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
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
