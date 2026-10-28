package io.github.thebusybiscuit.hotbarpets;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public class HotbarPetsRunnable {

    private final HotbarPet chicken;
    private final HotbarPet mooshroom;
    private final HotbarPet fish;
    private final HotbarPet goldenCow;

    protected HotbarPetsRunnable() {
        chicken = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_CHICKEN");
        mooshroom = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_MOOSHROOM");
        fish = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_FISH");
        goldenCow = (HotbarPet) SlimefunItem.getById("HOTBAR_PET_GOLDEN_COW");
    }

    public void tick(Player player) {
        if (!player.isOnline()) {
            return;
        }

        for (int slot = 0; slot < 9; ++slot) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }

            if (isPet(item, chicken)) {
                if (chicken.checkAndConsumeFood(player)) {
                    deliverProducedItem(player, new ItemStack(Material.EGG));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 2.0F);
                }
            } else if (isPet(item, mooshroom)) {
                if (mooshroom.checkAndConsumeFood(player)) {
                    deliverProducedItem(player, new ItemStack(Material.MUSHROOM_STEW));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, 1.0F, 2.0F);
                }
            } else if (isPet(item, fish)) {
                if (fish.checkAndConsumeFood(player)) {
                    deliverProducedItem(player, new ItemStack(Material.COOKED_COD));
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.0F, 2.0F);
                }
            } else if (isPet(item, goldenCow) && goldenCow.checkAndConsumeFood(player)) {
                deliverProducedItem(player, new ItemStack(Material.GOLD_INGOT));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, 0.8F, 2.0F);
            }
        }
    }

    /**
     * Vanilla inventory insertion may reject output if every slot is full.
     * Return all uninserted items to the player as normal ground drops
     * instead of silently destroying a reward after its food was consumed.
     * Called only inside the owning player's entity-scheduled tick.
     */
    static void deliverProducedItem(Player player, ItemStack output) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(output);
        for (ItemStack remaining : leftovers.values()) {
            if (remaining != null && !remaining.getType().isAir() && remaining.getAmount() > 0) {
                player.getWorld().dropItemNaturally(player.getLocation(), remaining);
            }
        }
    }

    private boolean isPet(ItemStack item, HotbarPet pet) {
        return pet != null && SlimefunUtils.isItemSimilar(item, pet.getItem(), true);
    }
}
