package io.github.thebusybiscuit.hotbarpets.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.junit.jupiter.api.Test;

/** Cancellation contracts for defensive pet food debit on Bukkit/Paper. */
class CancelledEventAbilityContractTest {

    @Test
    void alreadyCancelledDamageCannotConsumeDefensivePetFood() throws Exception {
        EventHandler handler = DamageListener.class
            .getMethod("onDamage", EntityDamageEvent.class)
            .getAnnotation(EventHandler.class);
        assertNotNull(handler, "DamageListener must remain registered");
        assertTrue(handler.ignoreCancelled(),
            "Cancelled damage cannot require Creeper/Magma Cube/Slime/etc. food");
        assertEquals(EventPriority.HIGHEST, handler.priority(),
            "Check after ordinary protection plugin cancellation priorities");
    }

    @Test
    void alreadyCancelledPhantomTargetCannotConsumePandaFood() throws Exception {
        EventHandler handler = PhantomListener.class
            .getMethod("onPhantomSpawn", EntityTargetLivingEntityEvent.class)
            .getAnnotation(EventHandler.class);
        assertNotNull(handler, "Phantom target listener must remain registered");
        assertTrue(handler.ignoreCancelled(), "Do not debit Panda when targeting was denied");
        assertEquals(EventPriority.HIGHEST, handler.priority());
    }
}
