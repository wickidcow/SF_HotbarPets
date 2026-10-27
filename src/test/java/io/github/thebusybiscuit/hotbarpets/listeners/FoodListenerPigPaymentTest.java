package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Exercise HotbarPet.checkAndConsumeFood inside the real delayed Pig callback,
 * including the underlying inventory receipt and Folia-wide message state.
 */
class FoodListenerPigPaymentTest {

    @AfterEach
    void clearCooldowns() {
        HotbarPet.getMessageDelay().clear();
    }

    private record Harness(Player player, PlayerInventory inventory, HotbarPet pig,
                           ItemStack food, Consumer<ScheduledTask> callback) {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Harness scheduleWithRealPayment(boolean permitted, boolean foodPresent) {
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        EntityScheduler scheduler = mock(EntityScheduler.class);
        Plugin plugin = mock(Plugin.class);
        ItemStack food = mock(ItemStack.class);
        ItemStack charge = mock(ItemStack.class);
        when(food.clone()).thenReturn(charge);
        HotbarPet pig = mock(HotbarPet.class, CALLS_REAL_METHODS);

        UUID uuid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getScheduler()).thenReturn(scheduler);
        // No unrelated messaging in the negative tests. The food/payment branch
        // itself is executed by the actual HotbarPet implementation.
        HotbarPet.getMessageDelay().put(uuid, Long.MAX_VALUE);
        doReturn(food).when(pig).getFavouriteFood();
        doReturn(permitted).when(pig).canUse(player);
        when(inventory.removeItemAnySlot(charge)).thenReturn(foodPresent
                ? new HashMap<>()
                : new HashMap<>(Map.of(0, charge)));

        FoodListener.schedulePigEffect(plugin, pig, player);
        ArgumentCaptor<Consumer> scheduled = ArgumentCaptor.forClass(Consumer.class);
        verify(scheduler).runDelayed(eq(plugin), scheduled.capture(), isNull(), eq(2L));
        Consumer<ScheduledTask> callback = scheduled.getValue();
        assertNotNull(callback);
        verify(inventory, never()).removeItemAnySlot(any(ItemStack.class));
        return new Harness(player, inventory, pig, food, callback);
    }

    @Test
    void removingAllFavoriteFoodBeforeCallbackDeniesBenefitAndDoesNotDebit() {
        Harness h = scheduleWithRealPayment(true, false);
        h.callback.accept(mock(ScheduledTask.class));

        verify(h.pig, times(1)).checkAndConsumeFood(h.player);
        verify(h.inventory, times(1)).removeItemAnySlot(any(ItemStack.class));
        verify(h.inventory, never()).removeItem(any(ItemStack.class));
        verify(h.player, never()).setSaturation(anyFloat());
        verify(h.player, never()).removePotionEffect(any());
    }

    @Test
    void revokingPermissionBeforeCallbackSkipsInventoryAndEffects() {
        Harness h = scheduleWithRealPayment(false, false);
        h.callback.accept(mock(ScheduledTask.class));

        verify(h.pig, times(1)).checkAndConsumeFood(h.player);
        verify(h.pig, times(1)).canUse(h.player);
        verify(h.inventory, never()).removeItemAnySlot(any(ItemStack.class));
        verify(h.inventory, never()).removeItem(any(ItemStack.class));
        verify(h.player, never()).setSaturation(anyFloat());
    }

    @Test
    void paidFavoriteFoodAppliesBenefitsExactlyOnce() {
        Harness h = scheduleWithRealPayment(true, true);
        World world = mock(World.class);
        Location location = mock(Location.class);
        when(h.player.getWorld()).thenReturn(world);
        when(h.player.getLocation()).thenReturn(location);
        when(h.player.getSaturation()).thenReturn(3.0F);

        h.callback.accept(mock(ScheduledTask.class));

        verify(h.pig, times(1)).checkAndConsumeFood(h.player);
        verify(h.inventory, times(1)).removeItemAnySlot(any(ItemStack.class));
        verify(h.inventory, never()).removeItem(any(ItemStack.class));
        verify(h.player, times(1)).setSaturation(11.0F);
        verify(h.player, times(1)).removePotionEffect(PotionEffectType.POISON);
        verify(world, times(1)).playSound(location, Sound.ENTITY_PIG_AMBIENT, 1.0F, 2.0F);
    }

    @Test
    void sharedMessageCooldownsMustBeSafeAcrossFoliaPlayerRegions() {
        assertInstanceOf(ConcurrentMap.class, HotbarPet.getMessageDelay());
    }
}
