package net.minecraftforge.cauldron.apiimpl;

import com.google.common.base.Predicate;
import net.minecraftforge.cauldron.api.Fishing;
import net.minecraftforge.cauldron.api.WeightedRandomFishable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Forge's FishingHooks were not added until 1.7.
 */
public class FishingInterface implements Fishing {

    @Override
    public void addFish(final WeightedRandomFishable fish) {
    }

    @Override
    public void addJunk(final WeightedRandomFishable fish) {
    }

    @Override
    public void addTreasure(final WeightedRandomFishable fish) {
    }

    @Override
    public void removeMatchingFish(final Predicate<WeightedRandomFishable> test) {
    }

    @Override
    public void removeMatchingJunk(final Predicate<WeightedRandomFishable> test) {
    }

    @Override
    public void removeMatchingTreasure(final Predicate<WeightedRandomFishable> test) {
    }

    @Override
    public ItemStack getRandomFishable(final Random rand, final float baseChance, final int fishingLuckEnchantmentLevel, final int fishingSpeedEnchantmentLevel) {
        if (rand.nextDouble() < baseChance) {
            return new ItemStack(Material.RAW_FISH);
        } else {
            return null;
        }
    }
}
