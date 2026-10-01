package io.github.thebusybiscuit.hotbarpets;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;

public final class PetEntityData {

    private static final String PROJECTILE_MARKER = "hotbarpets_projectile";
    private static final String TNT_OWNER = "hotbarpets_player";

    private PetEntityData() {}

    public static void markProjectile(HotbarPets plugin, Entity entity) {
        entity.getPersistentDataContainer().set(key(plugin, PROJECTILE_MARKER), PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isPetProjectile(HotbarPets plugin, Entity entity) {
        return entity.getPersistentDataContainer().has(key(plugin, PROJECTILE_MARKER), PersistentDataType.BYTE)
            || hasLegacyMetadata(entity, PROJECTILE_MARKER);
    }

    public static void clearProjectileMarker(HotbarPets plugin, Entity entity) {
        entity.getPersistentDataContainer().remove(key(plugin, PROJECTILE_MARKER));
    }

    public static void setTntOwner(HotbarPets plugin, Entity entity, UUID owner) {
        entity.getPersistentDataContainer().set(key(plugin, TNT_OWNER), PersistentDataType.STRING, owner.toString());
    }

    @Nullable
    public static UUID getTntOwner(HotbarPets plugin, Entity entity) {
        String stored = entity.getPersistentDataContainer().get(key(plugin, TNT_OWNER), PersistentDataType.STRING);
        if (stored != null) {
            try {
                return UUID.fromString(stored);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return legacyUuidMetadata(entity, TNT_OWNER);
    }

    public static void clearTntOwner(HotbarPets plugin, Entity entity) {
        entity.getPersistentDataContainer().remove(key(plugin, TNT_OWNER));
    }

    private static NamespacedKey key(HotbarPets plugin, String value) {
        return new NamespacedKey(plugin, value);
    }

    @SuppressWarnings("deprecation")
    private static boolean hasLegacyMetadata(Entity entity, String metadataKey) {
        return entity.hasMetadata(metadataKey);
    }

    @Nullable
    @SuppressWarnings("deprecation")
    private static UUID legacyUuidMetadata(Entity entity, String metadataKey) {
        List<MetadataValue> values = entity.getMetadata(metadataKey);
        if (values.isEmpty()) {
            return null;
        }

        Object value = values.get(0).value();
        if (value instanceof UUID uuid) {
            return uuid;
        }
        if (value instanceof String string) {
            try {
                return UUID.fromString(string);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}
