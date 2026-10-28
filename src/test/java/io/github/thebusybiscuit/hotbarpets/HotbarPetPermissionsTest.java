package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class HotbarPetPermissionsTest {

    @Test void defaultNodesMatchTheFullSlimefunIdSuffix() {
        assertEquals("slimefun.hotbarpet.pig",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_PIG", null));
        assertEquals("slimefun.hotbarpet.cow",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_COW", null));
        assertEquals("slimefun.hotbarpet.ender_man",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_ENDER_MAN", null));
        assertEquals("slimefun.hotbarpet.mrcookieslime",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_MRCOOKIESLIME", null));
    }

    @Test void explicitLegacyAlbionPrefixStillWorksWithoutMigratingPlayerNodes() {
        assertEquals("albion.hotbarpet.pig",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_PIG", "albion.hotbarpet."));
        assertEquals("albion.hotbarpet.ender_man",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_ENDER_MAN", "albion.hotbarpet"));
        assertEquals("albion.hotbarpet.mrcookieslime",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_MRCOOKIESLIME", "albion.hotbarpet."));
    }

    @Test void packagedConfigDefaultsToOptionalGatingAndSlimefunNodes() {
        YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                new File("src/main/resources/config.yml"));
        assertFalse(defaults.getBoolean("permission-gating.enabled", true));
        assertEquals("slimefun.hotbarpet.", defaults.getString("permission-gating.prefix"));
        assertEquals(2000L, defaults.getLong("production-interval-ticks"));
    }

    @Test void customPrefixesWorkWithOrWithoutTrailingPeriod() {
        assertEquals("myserver.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", "myserver.hotbarpet"));
        assertEquals("myserver.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", "myserver.hotbarpet."));
        assertEquals("slimefun.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", " "));
    }

    @Test void hungryMessageAlwaysStartsWithASpaceAndExplainsTheFoodRequirement() {
        assertEquals(" would have helped you, but it's hungry! Keep its favorite food in your inventory.",
                HotbarPet.HUNGRY_MESSAGE);
    }

    @Test void identifiersMustBeRealHotbarPetIds() {
        assertThrows(IllegalArgumentException.class, () ->
                HotbarPetPermissions.permissionFor("MCPETS_DRAGON", null));
        assertThrows(IllegalArgumentException.class, () ->
                HotbarPetPermissions.permissionFor("HOTBAR_PET_", null));
        assertThrows(NullPointerException.class, () ->
                HotbarPetPermissions.permissionFor(null, null));
    }
}
