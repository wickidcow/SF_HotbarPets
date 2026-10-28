package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** A TNT target and its owner may be on different Folia tick regions. */
class TNTListenerOwnerSchedulerTest {

    @SuppressWarnings("unchecked")
    @Test
    void deniedTntDamageNotifiesOwnerOnlyOnTheirEntityScheduler() {
        HotbarPets plugin = mock(HotbarPets.class);
        Player owner = mock(Player.class);
        EntityScheduler scheduler = mock(EntityScheduler.class);
        when(owner.getScheduler()).thenReturn(scheduler);

        TNTListener.notifyOwnerOnEntityScheduler(plugin, owner);

        Component denial = Component.text(
                "You cannot harm Players in here!", NamedTextColor.DARK_RED);
        verify(owner, never()).sendMessage(any(Component.class));
        ArgumentCaptor<Consumer<ScheduledTask>> action =
                ArgumentCaptor.forClass(Consumer.class);
        verify(scheduler, times(1)).run(eq(plugin), action.capture(), isNull());

        action.getValue().accept(mock(ScheduledTask.class));
        verify(owner, times(1)).sendMessage(denial);
    }
}
