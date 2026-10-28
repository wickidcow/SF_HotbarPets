package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.junit.jupiter.api.Test;

/**
 * Panda Pet must respect the phantom's proposed EntityTargetLivingEntityEvent
 * target, not the entity's earlier/stale target.
 */
class PhantomTargetContractTest {

    @Test
    void phantomUsesTheProposedEventTargetNotItsPriorEntityTarget() {
        EntityTargetLivingEntityEvent event = mock(EntityTargetLivingEntityEvent.class);
        Player player = mock(Player.class);
        when(event.getEntityType()).thenReturn(EntityType.PHANTOM);
        when(event.getTarget()).thenReturn(player);

        assertSame(player, PhantomListener.proposedPhantomTarget(event));
        verify(event, never()).getEntity();
    }

    @Test
    void unrelatedOrMissingTargetsCannotTriggerPandaFoodDebit() {
        EntityTargetLivingEntityEvent event = mock(EntityTargetLivingEntityEvent.class);
        when(event.getEntityType()).thenReturn(EntityType.PHANTOM);
        assertNull(PhantomListener.proposedPhantomTarget(event));

        when(event.getEntityType()).thenReturn(EntityType.ZOMBIE);
        when(event.getTarget()).thenReturn(mock(Player.class));
        assertNull(PhantomListener.proposedPhantomTarget(event));
    }
}
