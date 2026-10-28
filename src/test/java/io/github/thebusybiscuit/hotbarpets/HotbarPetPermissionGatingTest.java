package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** Exercise the actual permission gate used by active/passive Hotbar Pets. */
class HotbarPetPermissionGatingTest {

    private static HotbarPet pig() {
        HotbarPet item = mock(HotbarPet.class, CALLS_REAL_METHODS);
        doReturn("HOTBAR_PET_PIG").when(item).getId();
        return item;
    }

    private static HotbarPets plugin(YamlConfiguration config) {
        HotbarPets plugin = mock(HotbarPets.class);
        when(plugin.getConfig()).thenReturn(config);
        return plugin;
    }

    @Test
    void disabledGatingAllowsOwnedPetsWithoutCheckingPermission() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("permission-gating.enabled", false);
        Player player = mock(Player.class);
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            plugins.when(() -> JavaPlugin.getPlugin(HotbarPets.class)).thenReturn(plugin(cfg));
            assertTrue(pig().canUse(player));
            verify(player, never()).hasPermission(anyString());
        }
    }

    @Test
    void enabledGatingDeniesUnpermittedPigAndAllowsExactNewNode() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("permission-gating.enabled", true);
        cfg.set("permission-gating.prefix", "slimefun.hotbarpet.");
        Player player = mock(Player.class);
        HotbarPet pig = pig();
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            plugins.when(() -> JavaPlugin.getPlugin(HotbarPets.class)).thenReturn(plugin(cfg));
            assertFalse(pig.canUse(player), "Possession alone cannot grant the pet's ability");
            when(player.hasPermission("slimefun.hotbarpet.pig")).thenReturn(true);
            assertTrue(pig.canUse(player));
            verify(player, times(2)).hasPermission("slimefun.hotbarpet.pig");
        }
    }

    @Test
    void savedAlbionPrefixRequiresAlbionNodeNotSlimefunNode() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("permission-gating.enabled", true);
        cfg.set("permission-gating.prefix", "albion.hotbarpet.");
        Player player = mock(Player.class);
        when(player.hasPermission("slimefun.hotbarpet.pig")).thenReturn(true);
        HotbarPet pig = pig();
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            plugins.when(() -> JavaPlugin.getPlugin(HotbarPets.class)).thenReturn(plugin(cfg));
            assertFalse(pig.canUse(player));
            when(player.hasPermission("albion.hotbarpet.pig")).thenReturn(true);
            assertTrue(pig.canUse(player));
            verify(player, never()).hasPermission("slimefun.hotbarpet.pig");
        }
    }

    @Test
    void gatingWithNoConfiguredPrefixUsesNewSlimefunDefault() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("permission-gating.enabled", true);
        Player player = mock(Player.class);
        when(player.hasPermission("albion.hotbarpet.pig")).thenReturn(true);
        HotbarPet pig = pig();
        try (MockedStatic<JavaPlugin> plugins = mockStatic(JavaPlugin.class)) {
            plugins.when(() -> JavaPlugin.getPlugin(HotbarPets.class)).thenReturn(plugin(cfg));
            assertFalse(pig.canUse(player));
            when(player.hasPermission("slimefun.hotbarpet.pig")).thenReturn(true);
            assertTrue(pig.canUse(player));
            verify(player, never()).hasPermission("albion.hotbarpet.pig");
        }
    }
}
