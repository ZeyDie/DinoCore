package net.minecraft.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Random;

public class WeightedRandomChestContent extends WeightedRandomItem
{
    /** The Item/Block ID to generate in the Chest. */
    public ItemStack theItemId;

    /** The minimum chance of item generating. */
    public int theMinimumChanceToGenerateItem;

    /** The maximum chance of item generating. */
    public int theMaximumChanceToGenerateItem;

    public WeightedRandomChestContent(final int par1, final int par2, final int par3, final int par4, final int par5)
    {
        super(par5);
        this.theItemId = new ItemStack(par1, 1, par2);
        this.theMinimumChanceToGenerateItem = par3;
        this.theMaximumChanceToGenerateItem = par4;
    }

    public WeightedRandomChestContent(final ItemStack par1ItemStack, final int par2, final int par3, final int par4)
    {
        super(par4);
        this.theItemId = par1ItemStack;
        this.theMinimumChanceToGenerateItem = par2;
        this.theMaximumChanceToGenerateItem = par3;
    }

    /**
     * Generates the Chest contents.
     */
    public static void generateChestContents(final Random par0Random, final WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, final IInventory par2IInventory, final int par3)
    {
        for (int j = 0; j < par3; ++j)
        {
            final WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(par0Random, par1ArrayOfWeightedRandomChestContent);
            final ItemStack[] stacks = weightedrandomchestcontent.generateChestContent(par0Random, par2IInventory);

            for (final ItemStack item : stacks)
            {
                par2IInventory.setInventorySlotContents(par0Random.nextInt(par2IInventory.getSizeInventory()), item);
            }
        }
    }

    /**
     * Generates the Dispenser contents.
     */
    public static void generateDispenserContents(final Random par0Random, final WeightedRandomChestContent[] par1ArrayOfWeightedRandomChestContent, final TileEntityDispenser par2TileEntityDispenser, final int par3)
    {
        for (int j = 0; j < par3; ++j)
        {
            final WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent)WeightedRandom.getRandomItem(par0Random, par1ArrayOfWeightedRandomChestContent);
            final ItemStack[] stacks = weightedrandomchestcontent.generateChestContent(par0Random, par2TileEntityDispenser);

            for (final ItemStack item : stacks)
            {
                par2TileEntityDispenser.setInventorySlotContents(par0Random.nextInt(par2TileEntityDispenser.getSizeInventory()), item);
            }
        }
    }

    public static WeightedRandomChestContent[] func_92080_a(final WeightedRandomChestContent[] par0ArrayOfWeightedRandomChestContent, final WeightedRandomChestContent ... par1ArrayOfWeightedRandomChestContent)
    {
        final WeightedRandomChestContent[] aweightedrandomchestcontent1 = new WeightedRandomChestContent[par0ArrayOfWeightedRandomChestContent.length + par1ArrayOfWeightedRandomChestContent.length];
        int i = 0;

        for (int j = 0; j < par0ArrayOfWeightedRandomChestContent.length; ++j)
        {
            aweightedrandomchestcontent1[i++] = par0ArrayOfWeightedRandomChestContent[j];
        }

        final WeightedRandomChestContent[] aweightedrandomchestcontent2 = par1ArrayOfWeightedRandomChestContent;
        final int k = par1ArrayOfWeightedRandomChestContent.length;

        for (int l = 0; l < k; ++l)
        {
            final WeightedRandomChestContent weightedrandomchestcontent1 = aweightedrandomchestcontent2[l];
            aweightedrandomchestcontent1[i++] = weightedrandomchestcontent1;
        }

        return aweightedrandomchestcontent1;
    }

    // -- Forge hooks
    /**
     * Allow a mod to submit a custom implementation that can delegate item stack generation beyond simple stack lookup
     *
     * @param random The current random for generation
     * @param newInventory The inventory being generated (do not populate it, but you can refer to it)
     * @return An array of {@link ItemStack} to put into the chest
     */
    protected ItemStack[] generateChestContent(final Random random, final IInventory newInventory)
    {
        return ChestGenHooks.generateStacks(random, theItemId, theMinimumChanceToGenerateItem, theMaximumChanceToGenerateItem);
    }

}
