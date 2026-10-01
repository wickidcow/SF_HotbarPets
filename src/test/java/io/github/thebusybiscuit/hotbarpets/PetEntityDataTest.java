package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

class PetEntityDataTest {
    private HotbarPets plugin;
    private Entity entity;
    private NamespacedKey marker;
    private NamespacedKey ownerKey;
    private final UUID owner = UUID.fromString("fedabcde-1234-4abc-8123-123456789abc");

    @BeforeEach
    void setUp() {
        var server = MockBukkit.mock();
        plugin = mock(HotbarPets.class);
        when(plugin.getName()).thenReturn("HotbarPets");
        when(plugin.namespace()).thenReturn("hotbarpets");
        entity = server.addPlayer();
        marker = new NamespacedKey(plugin, "hotbarpets_projectile");
        ownerKey = new NamespacedKey(plugin, "hotbarpets_player");
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test void unmarkedEntitiesAreNotPetProjectiles() {
        assertFalse(PetEntityData.isPetProjectile(plugin, entity));
        assertNull(PetEntityData.getTntOwner(plugin, entity));
    }

    @Test void projectileMarkerKeepsItsExistingNameAndByteType() {
        PetEntityData.markProjectile(plugin, entity);
        assertTrue(PetEntityData.isPetProjectile(plugin, entity));
        assertEquals((byte) 1, entity.getPersistentDataContainer().get(marker, PersistentDataType.BYTE));
    }

    @Test void markerPresenceSemanticsAreUnchanged() {
        entity.getPersistentDataContainer().set(marker, PersistentDataType.BYTE, (byte) 0);
        assertTrue(PetEntityData.isPetProjectile(plugin, entity));
    }

    @Test void clearingCurrentMarkerDoesNotEraseUnrelatedData() {
        PetEntityData.markProjectile(plugin, entity);
        entity.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, owner.toString());
        PetEntityData.clearProjectileMarker(plugin, entity);
        assertFalse(PetEntityData.isPetProjectile(plugin, entity));
        assertEquals(owner, PetEntityData.getTntOwner(plugin, entity));
    }

    @Test void currentOwnerIsStoredAsExactUuidText() {
        PetEntityData.setTntOwner(plugin, entity, owner);
        assertEquals(owner.toString(), entity.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING));
        assertEquals(owner, PetEntityData.getTntOwner(plugin, entity));
    }

    @Test void clearingOwnerRetainsProjectileMarker() {
        PetEntityData.markProjectile(plugin, entity);
        PetEntityData.setTntOwner(plugin, entity, owner);
        PetEntityData.clearTntOwner(plugin, entity);
        assertNull(PetEntityData.getTntOwner(plugin, entity));
        assertTrue(PetEntityData.isPetProjectile(plugin, entity));
    }

    @Test void malformedCurrentOwnerIsNotRewritten() {
        entity.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, "unreadable-owner");
        assertNull(PetEntityData.getTntOwner(plugin, entity));
        assertEquals("unreadable-owner", entity.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING));
    }

    @SuppressWarnings("deprecation") // Reproduce legacy in-memory metadata; production still needs its reader.
    @Test void legacyUuidOwnerRemainsReadableWithoutConversion() {
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, owner));
        assertEquals(owner, PetEntityData.getTntOwner(plugin, entity));
        assertFalse(entity.getPersistentDataContainer().has(ownerKey));
    }

    @SuppressWarnings("deprecation")
    @Test void legacyStringOwnerRemainsReadableWithoutConversion() {
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, owner.toString()));
        assertEquals(owner, PetEntityData.getTntOwner(plugin, entity));
        assertFalse(entity.getPersistentDataContainer().has(ownerKey));
    }

    @SuppressWarnings("deprecation")
    @Test void legacyProjectileMarkerRemainsReadable() {
        entity.setMetadata("hotbarpets_projectile", new FixedMetadataValue(plugin, true));
        assertTrue(PetEntityData.isPetProjectile(plugin, entity));
        assertFalse(entity.getPersistentDataContainer().has(marker));
    }

    @SuppressWarnings("deprecation")
    @Test void currentOwnerTakesPrecedenceOverLegacyMetadata() {
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, UUID.randomUUID()));
        PetEntityData.setTntOwner(plugin, entity, owner);
        assertEquals(owner, PetEntityData.getTntOwner(plugin, entity));
    }

    @SuppressWarnings("deprecation")
    @Test void invalidCurrentOwnerDoesNotSilentlyAdoptAnotherOwner() {
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, owner));
        entity.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, "invalid");
        assertNull(PetEntityData.getTntOwner(plugin, entity));
    }

    @SuppressWarnings("deprecation")
    @Test void invalidLegacyOwnerReturnsNoOwner() {
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, "invalid"));
        assertNull(PetEntityData.getTntOwner(plugin, entity));
        entity.setMetadata("hotbarpets_player", new FixedMetadataValue(plugin, 42));
        assertNull(PetEntityData.getTntOwner(plugin, entity));
    }
}
