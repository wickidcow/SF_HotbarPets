package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** No duplicate pet item may cause multiple debits for a single event. */
class SinglePetEventEffectTest {

    private static HotbarPets plugin() {
        HotbarPets plugin = mock(HotbarPets.class);
        Server server = mock(Server.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        return plugin;
    }

    private static Player twoItems(ItemStack first, ItemStack second) {
        Player player = mock(Player.class);
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);
        when(inv.getItem(0)).thenReturn(first);
        when(inv.getItem(1)).thenReturn(second);
        return player;
    }

    @Test
    void duplicatePigPetsScheduleOnlyOneCallback() {
        HotbarPet pig = mock(HotbarPet.class);
        SlimefunItemStack item = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        Player p = twoItems(first, second);
        EntityScheduler scheduler = mock(EntityScheduler.class);
        when(p.getScheduler()).thenReturn(scheduler);
        when(pig.getItem()).thenReturn(item);
        when(pig.canUse(p)).thenReturn(true);
        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_PIG")).thenReturn(pig);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, item, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, item, true)).thenReturn(true);
            HotbarPets plugin = plugin();
            FoodListener handler = new FoodListener(plugin);
            PlayerItemConsumeEvent event = mock(PlayerItemConsumeEvent.class);
            when(event.getPlayer()).thenReturn(p);
            handler.onEat(event);
            verify(scheduler, times(1)).runDelayed(eq(plugin), any(Consumer.class), isNull(), eq(2L));
            verify(pig, times(1)).canUse(p);
        }
    }

    @Test
    void twoCreeperPetsDoNotChargeTwiceForOneExplosion() {
        HotbarPet creeper = mock(HotbarPet.class);
        SlimefunItemStack petItem = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        Player p = twoItems(first, second);
        World world = mock(World.class);
        Location loc = mock(Location.class);
        when(p.getWorld()).thenReturn(world);
        when(p.getLocation()).thenReturn(loc);
        when(creeper.getItem()).thenReturn(petItem);
        when(creeper.checkAndConsumeFood(p)).thenReturn(true);
        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_CREEPER")).thenReturn(creeper);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, petItem, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, petItem, true)).thenReturn(true);
            DamageListener handler = new DamageListener(plugin());
            EntityDamageEvent event = mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(p);
            when(event.getCause()).thenReturn(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
            handler.onDamage(event);
            verify(creeper, times(1)).checkAndConsumeFood(p);
            verify(event, times(1)).setCancelled(true);
            verify(world, times(1)).playSound(loc, Sound.ENTITY_CREEPER_PRIMED, 1.0F, 2.0F);
        }
    }

    @Test
    void hungrySlimeDoesNotBlockAnEquippedCookieSlimeFromProtecting() {
        HotbarPet slime = mock(HotbarPet.class), cookie = mock(HotbarPet.class);
        SlimefunItemStack slimeItem = mock(SlimefunItemStack.class);
        SlimefunItemStack cookieItem = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        Player p = twoItems(first, second);
        when(p.getWorld()).thenReturn(mock(World.class));
        when(p.getLocation()).thenReturn(mock(Location.class));
        when(slime.getItem()).thenReturn(slimeItem);
        when(cookie.getItem()).thenReturn(cookieItem);
        when(slime.checkAndConsumeFood(p)).thenReturn(false);
        when(cookie.checkAndConsumeFood(p)).thenReturn(true);
        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_SLIME")).thenReturn(slime);
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_MRCOOKIESLIME")).thenReturn(cookie);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, slimeItem, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, cookieItem, true)).thenReturn(true);
            DamageListener handler = new DamageListener(plugin());
            EntityDamageEvent event = mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(p);
            when(event.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);
            handler.onDamage(event);
            verify(slime, times(1)).checkAndConsumeFood(p);
            verify(cookie, times(1)).checkAndConsumeFood(p);
            verify(event, times(1)).setCancelled(true);
        }
    }

    @Test
    void slimeAndCookieSlimeCannotBothChargeForOneFall() {
        HotbarPet slime = mock(HotbarPet.class), cookie = mock(HotbarPet.class);
        SlimefunItemStack slimeItem = mock(SlimefunItemStack.class);
        SlimefunItemStack cookieItem = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        Player p = twoItems(first, second);
        // A successful damage mitigation plays a sound in the player's world.
        when(p.getWorld()).thenReturn(mock(World.class));
        when(p.getLocation()).thenReturn(mock(Location.class));
        when(slime.getItem()).thenReturn(slimeItem);
        when(cookie.getItem()).thenReturn(cookieItem);
        when(slime.checkAndConsumeFood(p)).thenReturn(true);
        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_SLIME")).thenReturn(slime);
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_MRCOOKIESLIME")).thenReturn(cookie);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, slimeItem, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, cookieItem, true)).thenReturn(true);
            DamageListener handler = new DamageListener(plugin());
            EntityDamageEvent event = mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(p);
            when(event.getCause()).thenReturn(EntityDamageEvent.DamageCause.FALL);
            handler.onDamage(event);
            verify(slime, times(1)).checkAndConsumeFood(p);
            verify(cookie, never()).checkAndConsumeFood(p);
            verify(event, times(1)).setCancelled(true);
        }
    }
}
