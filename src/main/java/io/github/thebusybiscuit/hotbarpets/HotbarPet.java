package io.github.thebusybiscuit.hotbarpets;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HotbarPet extends SlimefunItem {

    private static final long MESSAGE_DELAY = 2000;

    // Keep the wording and leading space consistent across all hungry-pet messages.
    public static final String HUNGRY_MESSAGE =
            " would have helped you, but it's hungry! Keep its favorite food in your inventory.";
    // Folia calls pets from different player-owned regions concurrently.
    private static final Map<UUID, Long> messageDelay = new ConcurrentHashMap<>();
    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    private final ItemStack food;

    public HotbarPet(ItemGroup itemGroup, SlimefunItemStack item, ItemStack food, ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);
        this.food = food;
    }

    public ItemStack getFavouriteFood() {
        return this.food;
    }

    /**
     * Optional per-pet ability lock. Off by default to preserve existing HotbarPets
     * behavior for all servers and all already-crafted Slimefun pet items.
     */
    public boolean canUse(Player player) {
        HotbarPets plugin = JavaPlugin.getPlugin(HotbarPets.class);
        if (!plugin.getConfig().getBoolean("permission-gating.enabled", false)) {
            return true;
        }

        String prefix = plugin.getConfig().getString(
                "permission-gating.prefix", HotbarPetPermissions.DEFAULT_PREFIX);
        return player.hasPermission(HotbarPetPermissions.permissionFor(getId(), prefix));
    }

    /**
     * Tries to consume the pets favourite food, if this was successful it will return true. Otherwise false.
     * If it fails to consume food it will send a message to the player, this is on a delay as to not spam the player.
     * The default delay is {@link #MESSAGE_DELAY}.
     *
     * @param player
     *            The {@link Player} who owns this pet
     *
     * @return If the food consumption was successful
     */
    public boolean checkAndConsumeFood(Player player) {
        if (!canUse(player)) {
            if (messageDelay.getOrDefault(player.getUniqueId(), 0L) <= System.currentTimeMillis()) {
                player.sendMessage(
                    Component.text("Unlock your ", NamedTextColor.RED)
                        .append(LEGACY_SECTION.deserialize(getItemName()))
                        .append(Component.text(" before using its ability.", NamedTextColor.RED))
                );
                messageDelay.put(player.getUniqueId(), System.currentTimeMillis() + MESSAGE_DELAY);
            }
            return false;
        }

        if (!player.getInventory().containsAtLeast(getFavouriteFood(), 1)) {
            if (messageDelay.getOrDefault(player.getUniqueId(), 0L) <= System.currentTimeMillis()) {
                player.sendMessage(
                    Component.text("Your ", NamedTextColor.BLUE)
                        .append(LEGACY_SECTION.deserialize(getItemName()))
                        .append(Component.text(
                            HUNGRY_MESSAGE,
                            NamedTextColor.BLUE
                        ))
                );
                messageDelay.put(player.getUniqueId(), System.currentTimeMillis() + MESSAGE_DELAY);
            }

            return false;
        }

        player.getInventory().removeItem(getFavouriteFood());
        return true;
    }

    public static Map<UUID, Long> getMessageDelay() {
        return messageDelay;
    }
}
