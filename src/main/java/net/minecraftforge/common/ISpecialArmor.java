/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This interface is to be implemented by ItemArmor classes. It will allow to
 * modify computation of damage and health loss. Computation will be called
 * before the actual armor computation, which can then be cancelled.
 *
 * @see ItemArmor
 */
public interface ISpecialArmor
{
    /**
     * Retrieves the modifiers to be used when calculating armor damage.
     *
     * Armor will higher priority will have damage applied to them before
     * lower priority ones. If there are multiple pieces of armor with the
     * same priority, damage will be distributed between them based on there
     * absorption ratio.
     *
     * @param player The entity wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     *     properties based on the type or source of damage.
     * @param damage The total damage being applied to the entity
     * @param slot The armor slot the item is in.
     * @return A ArmorProperties instance holding information about how the armor effects damage.
     */
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot);

    /**
     * Get the displayed effective armor.
     *
     * @param player The player wearing the armor.
     * @param armor The ItemStack of the armor item itself.
     * @param slot The armor slot the item is in.
     * @return The number of armor points for display, 2 per shield.
     */
    public abstract int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot);

    /**
     * Applies damage to the ItemStack. The mod is responsible for reducing the
     * item durability and stack size. If the stack is depleted it will be cleaned
     * up automatically.
     *
     * @param entity The entity wearing the armor
     * @param stack The ItemStack of the armor item itself.
     * @param source The source of the damage, which can be used to alter armor
     *     properties based on the type or source of damage.
     * @param damage The amount of damage being applied to the armor
     * @param slot The armor slot the item is in.
     */
    public abstract void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot);
    
    public static class ArmorProperties implements Comparable<ArmorProperties>
    {
        public int    Priority    = 0;
        public int    AbsorbMax   = Integer.MAX_VALUE;
        public double AbsorbRatio = 0;
        public int    Slot        = 0;
        private static final boolean DEBUG = false; //Only enable this if you wish to be spamed with debugging information.
                                                    //Left it in because I figured it'd be useful for modders developing custom armor.

        public ArmorProperties(final int priority, final double ratio, final int max)
        {
            Priority    = priority;
            AbsorbRatio = ratio;
            AbsorbMax   = max;
        }

        /**
         * Gathers and applies armor reduction to damage being dealt to a entity.
         *
         * @param entity The Entity being damage
         * @param inventory An array of armor items
         * @param source The damage source type
         * @param damage The total damage being done
         * @return The left over damage that has not been absorbed by the armor
         */
        public static float ApplyArmor(final EntityLivingBase entity, final ItemStack[] inventory, final DamageSource source, double damage)
        {
            double damage1 = damage;
            if (DEBUG)
            {
                System.out.println("Start: " + damage1 + " " + (damage1 * 25));
            }
            damage1 *= 25;
            final ArrayList<ArmorProperties> dmgVals = new ArrayList<ArmorProperties>();
            for (int x = 0; x < inventory.length; x++)
            {
                final ItemStack stack = inventory[x];
                if (stack == null)
                {
                    continue;
                }
                ArmorProperties prop = null;
                if (stack.getItem() instanceof ISpecialArmor)
                {
                    final ISpecialArmor armor = (ISpecialArmor)stack.getItem();
                    prop = armor.getProperties(entity, stack, source, damage1 / 25.0D, x).copy();
                }
                else if (stack.getItem() instanceof ItemArmor && !source.isUnblockable())
                {
                    final ItemArmor armor = (ItemArmor)stack.getItem();
                    prop = new ArmorProperties(0, armor.damageReduceAmount / 25.0D, armor.getMaxDamage() + 1 - stack.getItemDamage());
                }
                if (prop != null)
                {
                    prop.Slot = x;
                    dmgVals.add(prop);
                }
            }
            if (!dmgVals.isEmpty())
            {
                final ArmorProperties[] props = dmgVals.toArray(new ArmorProperties[0]);
                StandardizeList(props, damage1);
                int level = props[0].Priority;
                double ratio = 0;
                for (final ArmorProperties prop : props)
                {
                    if (level != prop.Priority)
                    {
                        damage1 -= (damage1 * ratio);
                        ratio = 0;
                        level = prop.Priority;
                    }
                    ratio += prop.AbsorbRatio;

                    final double absorb = damage1 * prop.AbsorbRatio;
                    if (absorb > 0)
                    {
                        final ItemStack stack = inventory[prop.Slot];
                        final int itemDamage = (int)(absorb / 25.0D < 1 ? 1 : absorb / 25.0D);
                        if (stack.getItem() instanceof ISpecialArmor)
                        {
                            ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.Slot);
                        }
                        else
                        {
                            if (DEBUG)
                            {
                                System.out.println("Item: " + stack.toString() + " Absorbed: " + (absorb / 25.0D) + " Damaged: " + itemDamage);
                            }
                            stack.damageItem(itemDamage, entity);
                        }
                        if (stack.stackSize <= 0)
                        {
                            /*if (entity instanceof EntityPlayer)
                            {
                                stack.onItemDestroyedByUse((EntityPlayer)entity);
                            }*/
                            inventory[prop.Slot] = null;
                        }
                    }
                }
                damage1 -= (damage1 * ratio);
            }
            if (DEBUG)
            {
                System.out.println("Return: " + (int)(damage1 / 25.0F) + " " + damage1);
            }
            return (float)(damage1 / 25.0F);
        }

        /**
         * Sorts and standardizes the distribution of damage over armor.
         *
         * @param armor The armor information
         * @param damage The total damage being received
         */
        private static void StandardizeList(final ArmorProperties[] armor, double damage)
        {
            double damage1 = damage;
            Arrays.sort(armor);

            int     start     = 0;
            double  total     = 0;
            int     priority  = armor[0].Priority;
            int     pStart    = 0;
            boolean pChange   = false;
            boolean pFinished = false;

            if (DEBUG)
            {
                for (final ArmorProperties prop : armor)
                {
                    System.out.println(prop);
                }
                System.out.println("========================");
            }

            for (int x = 0; x < armor.length; x++)
            {
                total += armor[x].AbsorbRatio;
                if (x == armor.length - 1 || armor[x].Priority != priority)
                {
                    if (armor[x].Priority != priority)
                    {
                        total -= armor[x].AbsorbRatio;
                        x--;
                        pChange = true;
                    }
                    if (total > 1)
                    {
                        for (int y = start; y <= x; y++)
                        {
                            final double newRatio = armor[y].AbsorbRatio / total;
                            if (newRatio * damage1 > armor[y].AbsorbMax)
                            {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage1;
                                total = 0;
                                for (int z = pStart; z <= y; z++)
                                {
                                    total += armor[z].AbsorbRatio;
                                }
                                start = y + 1;
                                x = y;
                                break;
                            }
                            else
                            {
                                armor[y].AbsorbRatio = newRatio;
                                pFinished = true;
                            }
                        }
                        if (pChange && pFinished)
                        {
                            damage1 -= (damage1 * total);
                            total = 0;
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            pFinished = false;
                            if (damage1 <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].AbsorbRatio = 0;
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        for (int y = start; y <= x; y++)
                        {
                            total -= armor[y].AbsorbRatio;
                            if (damage1 * armor[y].AbsorbRatio > armor[y].AbsorbMax)
                            {
                                armor[y].AbsorbRatio = (double)armor[y].AbsorbMax / damage1;
                            }
                            total += armor[y].AbsorbRatio;
                        }
                        damage1 -= (damage1 * total);
                        total = 0;
                        if (x != armor.length - 1)
                        {
                            start = x + 1;
                            priority = armor[start].Priority;
                            pStart = start;
                            pChange = false;
                            if (damage1 <= 0)
                            {
                                for (int y = x + 1; y < armor.length; y++)
                                {
                                    armor[y].AbsorbRatio = 0;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (DEBUG)
            {
                for (final ArmorProperties prop : armor)
                {
                    System.out.println(prop);
                }
            }
        }

        public int compareTo(final ArmorProperties o)
        {
            if (o.Priority != Priority)
            {
                return o.Priority - Priority;
            }
            final double left =  (  AbsorbRatio == 0 ? 0 :   AbsorbMax * 100.0D /   AbsorbRatio);
            final double right = (o.AbsorbRatio == 0 ? 0 : o.AbsorbMax * 100.0D / o.AbsorbRatio);
            return (int)(left - right);
        }

        public String toString()
        {
            return String.format("%d, %d, %f, %d", Priority, AbsorbMax, AbsorbRatio, (AbsorbRatio == 0 ? 0 : (int)(AbsorbMax * 100.0D / AbsorbRatio)));
        }

        public ArmorProperties copy()
        {
            return new ArmorProperties(Priority, AbsorbRatio, AbsorbMax);
        }
    }
}
