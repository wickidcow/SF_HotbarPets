package io.github.thebusybiscuit.hotbarpets;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

/** No producer food cost may result in silently discarded item output. */
class ProducerOverflowConservationTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private record Harness(Player player, PlayerInventory inventory,
                           World world, Location location) {}

    private static Harness player() {
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        World world = mock(World.class);
        Location location = new Location(world, 0, 64, 0);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(location);
        return new Harness(player, inventory, world, location);
    }

    @Test
    void acceptedProducerOutputNeverSpawnsDuplicates() {
        Harness h = player();
        ItemStack egg = new ItemStack(Material.EGG);
        when(h.inventory.addItem(egg)).thenReturn(new HashMap<>());

        HotbarPetsRunnable.deliverProducedItem(h.player, egg);

        verify(h.inventory, times(1)).addItem(egg);
        verify(h.player, never()).getWorld();
        verifyNoInteractions(h.world);
    }

    @Test
    void inventoryOverflowIsDroppedOnceRatherThanDeleted() {
        Harness h = player();
        ItemStack output = new ItemStack(Material.COOKED_COD);
        ItemStack leftover = output.clone();
        when(h.inventory.addItem(output))
                .thenReturn(new HashMap<>(Map.of(0, leftover)));

        HotbarPetsRunnable.deliverProducedItem(h.player, output);

        verify(h.inventory, times(1)).addItem(output);
        verify(h.world, times(1)).dropItemNaturally(h.location, leftover);
    }

    @Test
    void splitInsertionOnlyReturnsTheUnacceptedRemainder() {
        Harness h = player();
        ItemStack output = new ItemStack(Material.GOLD_INGOT, 2);
        ItemStack remaining = new ItemStack(Material.GOLD_INGOT, 1);
        when(h.inventory.addItem(output))
                .thenReturn(new HashMap<>(Map.of(0, remaining)));

        HotbarPetsRunnable.deliverProducedItem(h.player, output);

        verify(h.inventory, times(1)).addItem(output);
        verify(h.world, times(1)).dropItemNaturally(h.location, remaining);
    }
}
