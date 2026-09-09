package io.github.thebusybiscuit.hotbarpets;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                    player.getInventory().addItem(new ItemStack(Material.EGG));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 2.0F);
                }
            } else if (isPet(item, mooshroom)) {
                if (mooshroom.checkAndConsumeFood(player)) {
                    player.getInventory().addItem(new ItemStack(Material.MUSHROOM_STEW));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, 1.0F, 2.0F);
                }
            } else if (isPet(item, fish)) {
                if (fish.checkAndConsumeFood(player)) {
                    player.getInventory().addItem(new ItemStack(Material.COOKED_COD));
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.0F, 2.0F);
                }
            } else if (isPet(item, goldenCow) && goldenCow.checkAndConsumeFood(player)) {
                player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_AMBIENT, 0.8F, 2.0F);
            }
        }
    }

    private boolean isPet(ItemStack item, HotbarPet pet) {
        return pet != null && SlimefunUtils.isItemSimilar(item, pet.getItem(), true);
    }
}
