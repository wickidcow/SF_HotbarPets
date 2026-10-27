package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockbukkit.mockbukkit.MockBukkit;

/** Preserve one Soul Pie per kill while respecting owner and location regions. */
class SoulPieSingleDropTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private record Fixture(
            HotbarPets plugin, HotbarPet eyamaz, Player player, PlayerInventory inventory,
            EntityScheduler playerScheduler, RegionScheduler regionScheduler,
            World world, Location deathLocation, EntityDeathEvent death) {}

    private Fixture fixture() {
        HotbarPets plugin = mock(HotbarPets.class);
        Server server = mock(Server.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        EntityScheduler playerScheduler = mock(EntityScheduler.class);
        RegionScheduler regionScheduler = mock(RegionScheduler.class);
        when(server.getRegionScheduler()).thenReturn(regionScheduler);

        HotbarPet eyamaz = mock(HotbarPet.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        LivingEntity victim = mock(LivingEntity.class);
        EntityDeathEvent death = mock(EntityDeathEvent.class);
        World world = mock(World.class);
        Location location = new Location(world, 0, 64, 0);
        when(player.getScheduler()).thenReturn(playerScheduler);
        when(player.getInventory()).thenReturn(inventory);
        when(victim.getKiller()).thenReturn(player);
        when(victim.getLocation()).thenReturn(location);
        when(death.getEntity()).thenReturn(victim);

        return new Fixture(plugin, eyamaz, player, inventory, playerScheduler,
                regionScheduler, world, location, death);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void runOwnerCallback(Fixture f) {
        ArgumentCaptor<Consumer> consumer = ArgumentCaptor.forClass(Consumer.class);
        verify(f.playerScheduler, times(1)).run(eq(f.plugin), consumer.capture(), isNull());
        // The killer's inventory can be touched only inside this scheduled callback.
        consumer.getValue().accept(mock(ScheduledTask.class));
    }

    @Test
    void twoEyamazPetsDropOnlyOnePieAndNeverReadKillerOnDeathRegion() {
        Fixture f = fixture();
        SlimefunItemStack petItem = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        when(f.inventory.getItem(0)).thenReturn(first);
        when(f.inventory.getItem(1)).thenReturn(second);
        when(f.eyamaz.canUse(f.player)).thenReturn(true);
        when(f.eyamaz.getItem()).thenReturn(petItem);

        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_EYAMAZ")).thenReturn(f.eyamaz);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, petItem, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, petItem, true)).thenReturn(true);

            new SoulPieListener(f.plugin).onSoulHarvest(f.death);
            verify(f.player, never()).getInventory();
            verify(f.eyamaz, never()).canUse(f.player);
            verify(f.regionScheduler, never()).execute(any(), any(Location.class), any(Runnable.class));
            verify(f.world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));

            runOwnerCallback(f);
            verify(f.eyamaz, times(1)).canUse(f.player);
            verify(f.inventory, times(1)).getItem(0);
            verify(f.inventory, never()).getItem(1);

            ArgumentCaptor<Runnable> drop = ArgumentCaptor.forClass(Runnable.class);
            verify(f.regionScheduler, times(1)).execute(eq(f.plugin), eq(f.deathLocation), drop.capture());
            verify(f.world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));
            drop.getValue().run();
            verify(f.world, times(1)).dropItemNaturally(eq(f.deathLocation),
                    argThat(stack -> stack.getType() == Material.PUMPKIN_PIE));
        }
    }

    @Test
    void revokedPermissionBeforeKillerCallbackCannotSpawnSoulPie() {
        Fixture f = fixture();
        when(f.eyamaz.canUse(f.player)).thenReturn(false);

        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_EYAMAZ")).thenReturn(f.eyamaz);
            new SoulPieListener(f.plugin).onSoulHarvest(f.death);

            verify(f.eyamaz, never()).canUse(f.player);
            runOwnerCallback(f);
            verify(f.eyamaz, times(1)).canUse(f.player);
            verify(f.player, never()).getInventory();
            verify(f.regionScheduler, never()).execute(any(), any(Location.class), any(Runnable.class));
            verify(f.world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));
        }
    }
}
