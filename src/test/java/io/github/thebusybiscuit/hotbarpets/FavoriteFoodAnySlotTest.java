package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockbukkit.mockbukkit.MockBukkit;

/**
 * API-level any-slot payment contract. MockBukkit 4.110's InventoryMock
 * throws UnimplementedOperationException for removeItemAnySlot, so use a
 * mocked PlayerInventory with its exact Bukkit return semantics instead.
 * A real Paper offhand gameplay test remains an independent acceptance gate.
 */
class FavoriteFoodAnySlotTest {

    private Player player;
    private PlayerInventory inventory;
    private HotbarPet pet;
    private ItemStack template;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        player = mock(Player.class);
        inventory = mock(PlayerInventory.class);
        pet = mock(HotbarPet.class, CALLS_REAL_METHODS);
        template = new ItemStack(Material.CARROT, 3);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        doReturn(template).when(pet).getFavouriteFood();
        doReturn(true).when(pet).canUse(player);
        HotbarPet.getMessageDelay().put(player.getUniqueId(), Long.MAX_VALUE);
    }

    @AfterEach
    void tearDown() {
        HotbarPet.getMessageDelay().clear();
        MockBukkit.unmock();
    }

    @Test
    void favoriteFoodInOffhandSatisfiesMagmaCarryRequirement() {
        when(inventory.containsAtLeast(template, 1)).thenReturn(false);
        when(inventory.getItemInOffHand()).thenReturn(new ItemStack(Material.CARROT));
        assertTrue(pet.hasFavouriteFood(player));
        verify(inventory, never()).removeItemAnySlot(any(ItemStack.class));
    }

    @Test
    void missingFavoriteFoodDoesNotSatisfyMagmaCarryRequirement() {
        when(inventory.containsAtLeast(template, 1)).thenReturn(false);
        when(inventory.getItemInOffHand()).thenReturn(new ItemStack(Material.STONE));
        assertFalse(pet.hasFavouriteFood(player));
        verify(inventory, never()).removeItemAnySlot(any(ItemStack.class));
    }

    @Test
    void paidAnySlotRemovalMustRequestOneItemWithoutMutatingTemplate() {
        when(inventory.removeItemAnySlot(any(ItemStack.class)))
                .thenReturn(new HashMap<>());

        assertTrue(pet.checkAndConsumeFood(player));
        ArgumentCaptor<ItemStack> charge = ArgumentCaptor.forClass(ItemStack.class);
        verify(inventory, times(1)).removeItemAnySlot(charge.capture());
        assertEquals(Material.CARROT, charge.getValue().getType());
        assertEquals(1, charge.getValue().getAmount());
        assertNotSame(template, charge.getValue());
        assertEquals(3, template.getAmount(), "Shared food template must remain unchanged");
        verify(inventory, never()).removeItem(any(ItemStack.class));
    }

    @Test
    void rejectedAnySlotPaymentDoesNotGrantAFreeAbility() {
        when(inventory.removeItemAnySlot(any(ItemStack.class)))
                .thenReturn(new HashMap<>(Map.of(0, new ItemStack(Material.CARROT))));

        assertFalse(pet.checkAndConsumeFood(player));
        verify(inventory, times(1)).removeItemAnySlot(any(ItemStack.class));
        verify(inventory, never()).removeItem(any(ItemStack.class));
        assertEquals(3, template.getAmount(), "Failed debit cannot alter the prototype");
    }
}
