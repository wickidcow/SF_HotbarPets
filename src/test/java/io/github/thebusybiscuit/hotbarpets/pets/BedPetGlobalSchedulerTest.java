package io.github.thebusybiscuit.hotbarpets.pets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Server;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Folia owns world time on the global region. Pet player callbacks may run in
 * independent entity regions and must not mutate the clock synchronously.
 */
class BedPetGlobalSchedulerTest {

    @Test
    void bedPetChangesDaylightOnlyFromScheduledGlobalRegionCallback() {
        HotbarPets plugin = mock(HotbarPets.class);
        Server server = mock(Server.class);
        GlobalRegionScheduler scheduler = mock(GlobalRegionScheduler.class);
        World world = mock(World.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getGlobalRegionScheduler()).thenReturn(scheduler);

        BedPet.scheduleMorning(plugin, world);

        ArgumentCaptor<Runnable> clockUpdate = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler, times(1)).execute(eq(plugin), clockUpdate.capture());
        verify(world, never()).setTime(anyLong());
        clockUpdate.getValue().run();
        verify(world, times(1)).setTime(0L);
        verifyNoMoreInteractions(world);
    }
}
