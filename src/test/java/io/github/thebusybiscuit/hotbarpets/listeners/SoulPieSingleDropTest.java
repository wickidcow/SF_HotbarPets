package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
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
import org.mockito.MockedStatic;
import org.mockbukkit.mockbukkit.MockBukkit;

/** Each mob death can yield at most one Soul Pie even with duplicate Eyamaz pets. */
class SoulPieSingleDropTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void twoEyamazPetsGiveOneSoulPieForOneKill() {
        HotbarPets plugin = mock(HotbarPets.class);
        Server server = mock(Server.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));

        HotbarPet eyamaz = mock(HotbarPet.class);
        SlimefunItemStack petItem = mock(SlimefunItemStack.class);
        ItemStack first = mock(ItemStack.class), second = mock(ItemStack.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        LivingEntity killed = mock(LivingEntity.class);
        EntityDeathEvent event = mock(EntityDeathEvent.class);
        World world = mock(World.class);
        Location deathLocation = new Location(world, 0, 64, 0);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItem(0)).thenReturn(first);
        when(inventory.getItem(1)).thenReturn(second);
        when(killed.getKiller()).thenReturn(player);
        when(killed.getLocation()).thenReturn(deathLocation);
        when(event.getEntity()).thenReturn(killed);
        when(eyamaz.canUse(player)).thenReturn(true);
        when(eyamaz.getItem()).thenReturn(petItem);

        try (MockedStatic<SlimefunItem> ids = mockStatic(SlimefunItem.class);
             MockedStatic<SlimefunUtils> similar = mockStatic(SlimefunUtils.class)) {
            ids.when(() -> SlimefunItem.getById("HOTBAR_PET_EYAMAZ")).thenReturn(eyamaz);
            similar.when(() -> SlimefunUtils.isItemSimilar(first, petItem, true)).thenReturn(true);
            similar.when(() -> SlimefunUtils.isItemSimilar(second, petItem, true)).thenReturn(true);
            new SoulPieListener(plugin).onSoulHarvest(event);
            verify(world, times(1)).dropItemNaturally(eq(deathLocation),
                    argThat(stack -> stack.getType() == Material.PUMPKIN_PIE));
        }
    }
}
