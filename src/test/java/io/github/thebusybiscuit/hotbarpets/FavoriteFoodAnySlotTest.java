package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

/** Use an actual inventory, not just a stubbed success flag. */
class FavoriteFoodAnySlotTest {

    private PlayerMock player;
    private HotbarPet pet;
    private ItemStack food;

    @BeforeEach
    void setUp() {
        player = MockBukkit.mock().addPlayer();
        pet = mock(HotbarPet.class, CALLS_REAL_METHODS);
        food = new ItemStack(Material.CARROT);
        doReturn(food).when(pet).getFavouriteFood();
        doReturn(true).when(pet).canUse(player);
        // We are testing food inventory state, not a display-name message.
        HotbarPet.getMessageDelay().put(player.getUniqueId(), Long.MAX_VALUE);
    }

    @AfterEach
    void tearDown() {
        HotbarPet.getMessageDelay().clear();
        MockBukkit.unmock();
    }

    @Test
    void offhandFavoriteFoodMustBeDebitedBeforeAnyBenefit() {
        player.getInventory().setItemInOffHand(new ItemStack(Material.CARROT, 2));

        assertTrue(pet.checkAndConsumeFood(player));
        assertEquals(1, player.getInventory().getItemInOffHand().getAmount(),
                "Offhand must pay exactly one carrot");
        assertTrue(pet.checkAndConsumeFood(player));
        assertTrue(player.getInventory().getItemInOffHand().getType().isAir());
        assertFalse(pet.checkAndConsumeFood(player),
                "No third ability without a third carrot");
        assertEquals(1, food.getAmount(), "Shared recipe/template ItemStack must remain immutable");
    }

    @Test
    void storageFavoriteFoodDebitsOnlyOneFromMatchingStack() {
        player.getInventory().setItem(0, new ItemStack(Material.CARROT, 3));
        assertTrue(pet.checkAndConsumeFood(player));
        assertEquals(2, player.getInventory().getItem(0).getAmount());
        assertEquals(1, food.getAmount(), "Do not modify the pet's favorite food prototype");
    }
}
