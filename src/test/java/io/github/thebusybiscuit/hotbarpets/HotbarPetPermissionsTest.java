package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HotbarPetPermissionsTest {

    @Test void theAlbionShopPermissionMatchesTheRealSlimefunItemId() {
        assertEquals("albion.hotbarpet.cow",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_COW", null));
        assertEquals("albion.hotbarpet.ender_man",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_ENDER_MAN", "albion.hotbarpet."));
        assertEquals("albion.hotbarpet.mrcookieslime",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_MRCOOKIESLIME", null));
    }

    @Test void customPrefixesWorkWithOrWithoutTrailingPeriod() {
        assertEquals("myserver.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", "myserver.hotbarpet"));
        assertEquals("myserver.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", "myserver.hotbarpet."));
        assertEquals("albion.hotbarpet.dragon",
                HotbarPetPermissions.permissionFor("HOTBAR_PET_DRAGON", " "));
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
