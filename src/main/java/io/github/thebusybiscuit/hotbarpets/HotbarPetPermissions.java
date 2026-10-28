package io.github.thebusybiscuit.hotbarpets;

import java.util.Locale;
import java.util.Objects;

/**
 * Optional, addon-owned ability entitlement for HotbarPets.
 * Does not affect Slimefun recipes, inventories or item IDs.
 */
public final class HotbarPetPermissions {

    public static final String DEFAULT_PREFIX = "slimefun.hotbarpet.";
    private static final String ITEM_PREFIX = "HOTBAR_PET_";

    private HotbarPetPermissions() {}

    public static String permissionFor(String itemId, String configuredPrefix) {
        Objects.requireNonNull(itemId, "itemId");
        if (!itemId.startsWith(ITEM_PREFIX) || itemId.length() == ITEM_PREFIX.length()) {
            throw new IllegalArgumentException("Not a registered HotbarPet identifier: " + itemId);
        }

        String prefix = configuredPrefix == null || configuredPrefix.isBlank()
                ? DEFAULT_PREFIX : configuredPrefix.trim();
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }

        return prefix + itemId.substring(ITEM_PREFIX.length()).toLowerCase(Locale.ROOT);
    }
}
