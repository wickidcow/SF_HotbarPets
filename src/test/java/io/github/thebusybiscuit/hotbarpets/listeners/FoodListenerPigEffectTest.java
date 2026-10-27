package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPet;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Execute the actual delayed Pig callback, not only its source text. */
class FoodListenerPigEffectTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Consumer<ScheduledTask> scheduledCallback(
            Plugin plugin, EntityScheduler scheduler) {
        ArgumentCaptor<Consumer> callback = ArgumentCaptor.forClass(Consumer.class);
        verify(scheduler).runDelayed(eq(plugin), callback.capture(), isNull(), eq(2L));
        Consumer<ScheduledTask> action = callback.getValue();
        assertNotNull(action);
        return action;
    }

    @Test
    void missingFoodOrRevokedPermissionNeverAppliesDelayedEffect() {
        Player player = mock(Player.class);
        EntityScheduler scheduler = mock(EntityScheduler.class);
        Plugin plugin = mock(Plugin.class);
        HotbarPet pig = mock(HotbarPet.class);
        when(player.getScheduler()).thenReturn(scheduler);
        when(pig.checkAndConsumeFood(player)).thenReturn(false);

        FoodListener.schedulePigEffect(plugin, pig, player);
        verify(pig, never()).checkAndConsumeFood(player); // payment happens only at execution

        scheduledCallback(plugin, scheduler).accept(mock(ScheduledTask.class));
        verify(pig, times(1)).checkAndConsumeFood(player);
        verify(player, never()).getSaturation();
        verify(player, never()).setSaturation(anyFloat());
        verify(player, never()).removePotionEffect(any());
        verify(player, never()).getWorld();
    }

    @Test
    void successfulDelayedDebitAppliesPigEffectExactlyOnce() {
        Player player = mock(Player.class);
        EntityScheduler scheduler = mock(EntityScheduler.class);
        Plugin plugin = mock(Plugin.class);
        HotbarPet pig = mock(HotbarPet.class);
        World world = mock(World.class);
        Location location = mock(Location.class);
        when(player.getScheduler()).thenReturn(scheduler);
        when(pig.checkAndConsumeFood(player)).thenReturn(true);
        when(player.getSaturation()).thenReturn(3.0F);
        when(player.getWorld()).thenReturn(world);
        when(player.getLocation()).thenReturn(location);

        FoodListener.schedulePigEffect(plugin, pig, player);
        scheduledCallback(plugin, scheduler).accept(mock(ScheduledTask.class));

        verify(pig, times(1)).checkAndConsumeFood(player);
        verify(player, times(1)).setSaturation(11.0F);
        verify(player, times(1)).removePotionEffect(PotionEffectType.POISON);
        verify(world, times(1)).playSound(location, Sound.ENTITY_PIG_AMBIENT, 1.0F, 2.0F);
    }
}
