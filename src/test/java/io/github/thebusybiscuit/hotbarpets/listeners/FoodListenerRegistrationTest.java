package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.github.thebusybiscuit.hotbarpets.HotbarPets;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** Catch missing event registration that ordinary server boot tests overlook. */
class FoodListenerRegistrationTest {
    @Test
    void constructionRegistersActualPigAndZombieHandlerExactlyOnce() {
        HotbarPets plugin = mock(HotbarPets.class);
        Server server = mock(Server.class);
        PluginManager manager = mock(PluginManager.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(manager);

        // No real Slimefun registry is needed to test the listener's lifecycle.
        try (MockedStatic<SlimefunItem> sf = mockStatic(SlimefunItem.class)) {
            FoodListener listener = new FoodListener(plugin);
            verify(manager, times(1)).registerEvents(same(listener), same(plugin));
            verifyNoMoreInteractions(manager);
        }
    }

    @Test
    void pluginStartupKeepsCreatingTheRegisteredFoodListener() throws Exception {
        String bootstrap = Files.readString(Path.of(
                "src/main/java/io/github/thebusybiscuit/hotbarpets/HotbarPets.java"));
        assertTrue(bootstrap.contains("new FoodListener(this);"),
                "Pig and Zombie event handling must be installed on plugin startup");
    }
}
