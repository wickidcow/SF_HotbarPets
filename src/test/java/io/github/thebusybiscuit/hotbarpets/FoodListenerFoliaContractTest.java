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

        assertTrue(pigCallback.contains("if (!pig.canUse(p))"),
            "Pig must continue checking permission when the delayed effect executes");
        assertTrue(zombieCallback.contains("if (!zombie.canUse(p))"),
            "Zombie must reject a revoked permission before removing Hunger");
        assertTrue(zombieCallback.indexOf("if (!zombie.canUse(p))")
                < zombieCallback.indexOf("p.removePotionEffect(PotionEffectType.HUNGER)"),
            "The recheck must happen before the zombie effect is applied");
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
