package io.github.thebusybiscuit.hotbarpets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FoodListenerFoliaContractTest {

    private static final Path SOURCE = Path.of(
        "src/main/java/io/github/thebusybiscuit/hotbarpets/listeners/FoodListener.java");

    @Test
    void delayedFoodEffectsUseThePlayerEntityScheduler() throws IOException {
        String source = Files.readString(SOURCE);

        assertTrue(
            source.contains("p.getScheduler().runDelayed"),
            "Player mutations must follow the player's owning entity scheduler on Folia");
        assertFalse(
            source.contains("getServer().getScheduler()"),
            "Folia-supported food effects must not use the classic Bukkit scheduler");
        assertFalse(
            source.contains("scheduleSyncDelayedTask"),
            "Delayed food effects must not fall back to the classic sync scheduler");
    }

    @Test
    void delayedFoodEffectsRecheckOptionalAbilityPermissionsBeforeApplying() throws IOException {
        String source = Files.readString(SOURCE);
        int first = source.indexOf("p.getScheduler().runDelayed");
        int second = source.indexOf("p.getScheduler().runDelayed", first + 1);

        assertTrue(first >= 0 && second > first, "Both Pig and Zombie effects must stay scheduled");
        String pigCallback = source.substring(first, second);
        String zombieCallback = source.substring(second);

        assertTrue(pigCallback.contains("if (!pig.checkAndConsumeFood(p))"),
            "Pig must recheck permission and consume food when the delayed effect executes");
        assertTrue(zombieCallback.contains("if (!zombie.canUse(p))"),
            "Zombie must reject a revoked permission before removing Hunger");
        assertTrue(zombieCallback.indexOf("if (!zombie.canUse(p))")
                < zombieCallback.indexOf("p.removePotionEffect(PotionEffectType.HUNGER)"),
            "The recheck must happen before the zombie effect is applied");
    }

    @Test
    void delayedPigCannotApplyEffectsAfterItsFoodWasRemoved() throws IOException {
        String source = Files.readString(SOURCE);
        int first = source.indexOf("p.getScheduler().runDelayed");
        int second = source.indexOf("p.getScheduler().runDelayed", first + 1);
        assertTrue(first >= 0 && second > first, "Locate the delayed Pig and Zombie callbacks");

        String pigCallback = source.substring(first, second);
        int payment = pigCallback.indexOf("if (!pig.checkAndConsumeFood(p))");
        int effect = pigCallback.indexOf("p.setSaturation(");
        assertTrue(payment >= 0 && effect > payment,
            "Pig must check food and permission at execution before applying benefits");
        assertFalse(pigCallback.contains("p.getInventory().removeItem("),
            "Do not bypass the checked food-debit helper inside the delayed callback");

        String petSource = Files.readString(Path.of(
            "src/main/java/io/github/thebusybiscuit/hotbarpets/HotbarPet.java"));
        assertTrue(petSource.contains("if (!canUse(player))"),
            "The shared food-debit helper must recheck optional permissions");
        assertTrue(petSource.contains("if (!player.getInventory().removeItemAnySlot(payment).isEmpty())"),
            "The shared food-debit helper must reject unfulfilled inventory payment");
        assertTrue(petSource.contains("payment.setAmount(1);"),
            "Every pet ability must request exactly one favorite-food item");
    }

    @Test
    void foodBenefitsAreNotScheduledForCancelledConsumption() throws Exception {
        var handler = io.github.thebusybiscuit.hotbarpets.listeners.FoodListener.class
            .getMethod("onEat", org.bukkit.event.player.PlayerItemConsumeEvent.class)
            .getAnnotation(org.bukkit.event.EventHandler.class);
        assertTrue(handler != null && handler.ignoreCancelled(),
            "Cancelled consumption cannot grant either Pig or Zombie perks");
        assertTrue(handler.priority() == org.bukkit.event.EventPriority.MONITOR,
            "Observe final consumption state after ordinary cancellation handlers");
    }

    @Test
    void bothDelayedFoodBranchesRemainEntityOwned() throws IOException {
        String source = Files.readString(SOURCE);
        int first = source.indexOf("p.getScheduler().runDelayed");
        int second = source.indexOf("p.getScheduler().runDelayed", first + 1);

        assertTrue(first >= 0, "Pig pet delayed effect must use the player scheduler");
        assertTrue(second > first, "Zombie pet delayed effect must also use the player scheduler");
        assertTrue(
            source.contains("null, 2L"),
            "The existing two-tick delay must be retained without inventing a retirement side effect");
    }
}
