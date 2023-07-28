package net.minecraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayerFactory;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.BonemealEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import java.util.List;


public class ItemDye extends Item
{
    /** List of dye color names */
    public static final String[] dyeColorNames = {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
    public static final String[] dyeItemNames = {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
    public static final int[] dyeColors = {1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};
    @SideOnly(Side.CLIENT)
    private Icon[] dyeIcons;

    public ItemDye(final int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(final int par1)
    {
        final int j = MathHelper.clamp_int(par1, 0, 15);
        return this.dyeIcons[j];
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack par1ItemStack)
    {
        final int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 15);
        return super.getUnlocalizedName() + "." + dyeColorNames[i];
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, int par4, final int par5, int par6, final int par7, final float par8, final float par9, final float par10)
    {
        int par61 = par6;
        int par41 = par4;
        if (!par2EntityPlayer.canPlayerEdit(par41, par5, par61, par7, par1ItemStack))
        {
            return false;
        }
        else
        {
            if (par1ItemStack.getItemDamage() == 15)
            {
                if (a(par1ItemStack, par3World, par41, par5, par61, par2EntityPlayer))   // CraftBukkit - pass entity for StructureGrowEvent
                {
                    if (!par3World.isRemote)
                    {
                        par3World.playAuxSFX(2005, par41, par5, par61, 0);
                    }

                    return true;
                }
            }
            else if (par1ItemStack.getItemDamage() == 3)
            {
                final int i1 = par3World.getBlockId(par41, par5, par61);
                final int j1 = par3World.getBlockMetadata(par41, par5, par61);

                if (i1 == Block.wood.blockID && BlockLog.limitToValidMetadata(j1) == 3)
                {
                    if (par7 == 0)
                    {
                        return false;
                    }

                    if (par7 == 1)
                    {
                        return false;
                    }

                    if (par7 == 2)
                    {
                        --par61;
                    }

                    if (par7 == 3)
                    {
                        ++par61;
                    }

                    if (par7 == 4)
                    {
                        --par41;
                    }

                    if (par7 == 5)
                    {
                        ++par41;
                    }

                    if (par3World.isAirBlock(par41, par5, par61))
                    {
                        final int k1 = Block.blocksList[Block.cocoaPlant.blockID].onBlockPlaced(par3World, par41, par5, par61, par7, par8, par9, par10, 0);
                        par3World.setBlock(par41, par5, par61, Block.cocoaPlant.blockID, k1, 2);

                        if (!par2EntityPlayer.capabilities.isCreativeMode)
                        {
                            --par1ItemStack.stackSize;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }


    // Cauldron end - CB compatibility (TODO: add manual plugin mapping?)
    public static boolean a(final ItemStack itemstack, final World world, final int i, final int j, final int k, final EntityPlayer entityplayer)
    {
        return applyBonemeal(itemstack, world, i, j, k, entityplayer);
    }
    // Cauldron end
    
    public static boolean func_96604_a(final ItemStack par0ItemStack, final World par1World, final int par2, final int par3, final int par4)
    {
        return applyBonemeal(par0ItemStack, par1World, par2, par3, par4, FakePlayerFactory.getMinecraft(par1World));
    }

    public static boolean applyBonemeal(final ItemStack par0ItemStack, final World par1World, final int par2, final int par3, final int par4, final EntityPlayer player)
    {
        final int l = par1World.getBlockId(par2, par3, par4);

        final BonemealEvent event = new BonemealEvent(player, par1World, l, par2, par3, par4);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        if (event.getResult() == Result.ALLOW)
        {
            if (!par1World.isRemote)
            {
                par0ItemStack.stackSize--;
            }
            return true;
        }

        if (l == Block.sapling.blockID)
        {
            if (!par1World.isRemote)
            {
                if ((double)par1World.rand.nextFloat() < 0.45D)
                {
                    ((BlockSapling) Block.sapling).growTree(par1World, par2, par3, par4, par1World.rand);
                }

                --par0ItemStack.stackSize;
            }

            return true;
        }
        else if (l != Block.mushroomBrown.blockID && l != Block.mushroomRed.blockID)
        {
            if (l != Block.melonStem.blockID && l != Block.pumpkinStem.blockID)
            {
                if (l > 0 && Block.blocksList[l] instanceof BlockCrops)
                {
                    if (par1World.getBlockMetadata(par2, par3, par4) == 7)
                    {
                        return false;
                    }
                    else
                    {
                        if (!par1World.isRemote)
                        {
                            ((BlockCrops)Block.blocksList[l]).fertilize(par1World, par2, par3, par4);
                            --par0ItemStack.stackSize;
                        }

                        return true;
                    }
                }
                else
                {
                    int i1;
                    int j1;
                    int k1;

                    if (l == Block.cocoaPlant.blockID)
                    {
                        i1 = par1World.getBlockMetadata(par2, par3, par4);
                        j1 = BlockDirectional.getDirection(i1);
                        k1 = BlockCocoa.func_72219_c(i1);

                        if (k1 >= 2)
                        {
                            return false;
                        }
                        else
                        {
                            if (!par1World.isRemote)
                            {
                                ++k1;
                                par1World.setBlockMetadataWithNotify(par2, par3, par4, k1 << 2 | j1, 2);
                                --par0ItemStack.stackSize;
                            }

                            return true;
                        }
                    }
                    else if (l != Block.grass.blockID)
                    {
                        return false;
                    }
                    else
                    {
                        if (!par1World.isRemote)
                        {
                            --par0ItemStack.stackSize;
                            label102:

                            for (i1 = 0; i1 < 128; ++i1)
                            {
                                j1 = par2;
                                k1 = par3 + 1;
                                int l1 = par4;

                                for (int i2 = 0; i2 < i1 / 16; ++i2)
                                {
                                    j1 += itemRand.nextInt(3) - 1;
                                    k1 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
                                    l1 += itemRand.nextInt(3) - 1;

                                    if (par1World.getBlockId(j1, k1 - 1, l1) != Block.grass.blockID || par1World.isBlockNormalCube(j1, k1, l1))
                                    {
                                        continue label102;
                                    }
                                }

                                if (par1World.getBlockId(j1, k1, l1) == 0)
                                {
                                    if (itemRand.nextInt(10) != 0)
                                    {
                                        if (Block.tallGrass.canBlockStay(par1World, j1, k1, l1))
                                        {
                                            par1World.setBlock(j1, k1, l1, Block.tallGrass.blockID, 1, 3);
                                        }
                                    }
                                    else
                                    {
                                        ForgeHooks.plantGrass(par1World, j1, k1, l1);
                                    }
                                }
                            }
                        }

                        return true;
                    }
                }
            }
            else if (par1World.getBlockMetadata(par2, par3, par4) == 7)
            {
                return false;
            }
            else
            {
                if (!par1World.isRemote)
                {
                    ((BlockStem)Block.blocksList[l]).fertilizeStem(par1World, par2, par3, par4);
                    --par0ItemStack.stackSize;
                }

                return true;
            }
        }
        else
        {
            if (!par1World.isRemote)
            {
                if ((double)par1World.rand.nextFloat() < 0.4D)
                {
                    ((BlockMushroom) Block.blocksList[l]).fertilizeMushroom(par1World, par2, par3, par4, par1World.rand);
                }

                --par0ItemStack.stackSize;
            }

            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void func_96603_a(final World par0World, final int par1, final int par2, final int par3, int par4)
    {
        int par41 = par4;
        final int i1 = par0World.getBlockId(par1, par2, par3);

        if (par41 == 0)
        {
            par41 = 15;
        }

        final Block block = i1 > 0 && i1 < Block.blocksList.length ? Block.blocksList[i1] : null;

        if (block != null)
        {
            block.setBlockBoundsBasedOnState(par0World, par1, par2, par3);

            for (int j1 = 0; j1 < par41; ++j1)
            {
                final double d0 = itemRand.nextGaussian() * 0.02D;
                final double d1 = itemRand.nextGaussian() * 0.02D;
                final double d2 = itemRand.nextGaussian() * 0.02D;
                par0World.spawnParticle("happyVillager", (double)((float)par1 + itemRand.nextFloat()), (double)par2 + (double)itemRand.nextFloat() * block.getBlockBoundsMaxY(), (double)((float)par3 + itemRand.nextFloat()), d0, d1, d2);
            }
        }
        else
        {
            for (int j1 = 0; j1 < par41; ++j1)
            {
                final double d0 = itemRand.nextGaussian() * 0.02D;
                final double d1 = itemRand.nextGaussian() * 0.02D;
                final double d2 = itemRand.nextGaussian() * 0.02D;
                par0World.spawnParticle("happyVillager", (double)((float)par1 + itemRand.nextFloat()), (double)par2 + (double)itemRand.nextFloat() * 1.0f, (double)((float)par3 + itemRand.nextFloat()), d0, d1, d2);
            }
        }
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final EntityLivingBase par3EntityLivingBase)
    {
        if (par3EntityLivingBase instanceof EntitySheep)
        {
            final EntitySheep entitysheep = (EntitySheep)par3EntityLivingBase;
            int i = BlockColored.getBlockFromDye(par1ItemStack.getItemDamage());

            if (!entitysheep.getSheared() && entitysheep.getFleeceColor() != i)
            {
                // CraftBukkit start
                final byte bColor = (byte) i;
                final SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) entitysheep.getBukkitEntity(), org.bukkit.DyeColor.getByData(bColor));
                entitysheep.worldObj.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled())
                {
                    return false;
                }

                i = (byte) event.getColor().getWoolData();
                // CraftBukkit end
                entitysheep.setFleeceColor(i);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(final int par1, final CreativeTabs par2CreativeTabs, final List par3List)
    {
        for (int j = 0; j < 16; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.dyeIcons = new Icon[dyeItemNames.length];

        for (int i = 0; i < dyeItemNames.length; ++i)
        {
            this.dyeIcons[i] = par1IconRegister.registerIcon(this.getIconString() + "_" + dyeItemNames[i]);
        }
    }
}
