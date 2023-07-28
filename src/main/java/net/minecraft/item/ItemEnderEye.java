package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item
{
    public ItemEnderEye(final int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final World par3World, final int par4, final int par5, final int par6, final int par7, final float par8, final float par9, final float par10)
    {
        final int i1 = par3World.getBlockId(par4, par5, par6);
        final int j1 = par3World.getBlockMetadata(par4, par5, par6);

        if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && i1 == Block.endPortalFrame.blockID && !BlockEndPortalFrame.isEnderEyeInserted(j1))
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                par3World.setBlockMetadataWithNotify(par4, par5, par6, j1 + 4, 2);
                par3World.func_96440_m(par4, par5, par6, Block.endPortalFrame.blockID);
                --par1ItemStack.stackSize;
                int k1;

                for (k1 = 0; k1 < 16; ++k1)
                {
                    final double d0 = (double)((float)par4 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    final double d1 = (double)((float)par5 + 0.8125F);
                    final double d2 = (double)((float)par6 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    final double d3 = 0.0D;
                    final double d4 = 0.0D;
                    final double d5 = 0.0D;
                    par3World.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                k1 = j1 & 3;
                int l1 = 0;
                int i2 = 0;
                boolean flag = false;
                boolean flag1 = true;
                final int j2 = Direction.rotateRight[k1];
                int k2;
                int l2;
                int i3;
                int j3;
                int k3;

                for (k2 = -2; k2 <= 2; ++k2)
                {
                    j3 = par4 + Direction.offsetX[j2] * k2;
                    l2 = par6 + Direction.offsetZ[j2] * k2;
                    k3 = par3World.getBlockId(j3, par5, l2);

                    if (k3 == Block.endPortalFrame.blockID)
                    {
                        i3 = par3World.getBlockMetadata(j3, par5, l2);

                        if (!BlockEndPortalFrame.isEnderEyeInserted(i3))
                        {
                            flag1 = false;
                            break;
                        }

                        i2 = k2;

                        if (!flag)
                        {
                            l1 = k2;
                            flag = true;
                        }
                    }
                }

                if (flag1 && i2 == l1 + 2)
                {
                    for (k2 = l1; k2 <= i2; ++k2)
                    {
                        j3 = par4 + Direction.offsetX[j2] * k2;
                        l2 = par6 + Direction.offsetZ[j2] * k2;
                        j3 += Direction.offsetX[k1] * 4;
                        l2 += Direction.offsetZ[k1] * 4;
                        k3 = par3World.getBlockId(j3, par5, l2);
                        i3 = par3World.getBlockMetadata(j3, par5, l2);

                        if (k3 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(i3))
                        {
                            flag1 = false;
                            break;
                        }
                    }

                    for (k2 = l1 - 1; k2 <= i2 + 1; k2 += 4)
                    {
                        for (j3 = 1; j3 <= 3; ++j3)
                        {
                            l2 = par4 + Direction.offsetX[j2] * k2;
                            k3 = par6 + Direction.offsetZ[j2] * k2;
                            l2 += Direction.offsetX[k1] * j3;
                            k3 += Direction.offsetZ[k1] * j3;
                            i3 = par3World.getBlockId(l2, par5, k3);
                            final int l3 = par3World.getBlockMetadata(l2, par5, k3);

                            if (i3 != Block.endPortalFrame.blockID || !BlockEndPortalFrame.isEnderEyeInserted(l3))
                            {
                                flag1 = false;
                                break;
                            }
                        }
                    }

                    if (flag1)
                    {
                        for (k2 = l1; k2 <= i2; ++k2)
                        {
                            for (j3 = 1; j3 <= 3; ++j3)
                            {
                                l2 = par4 + Direction.offsetX[j2] * k2;
                                k3 = par6 + Direction.offsetZ[j2] * k2;
                                l2 += Direction.offsetX[k1] * j3;
                                k3 += Direction.offsetZ[k1] * j3;
                                par3World.setBlock(l2, par5, k3, Block.endPortal.blockID, 0, 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer)
    {
        final MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, false);

        if (movingobjectposition != null && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
        {
            final int i = par2World.getBlockId(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);

            if (i == Block.endPortalFrame.blockID)
            {
                return par1ItemStack;
            }
        }

        if (!par2World.isRemote)
        {
            final ChunkPosition chunkposition = par2World.findClosestStructure("Stronghold", (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);

            if (chunkposition != null)
            {
                final EntityEnderEye entityendereye = new EntityEnderEye(par2World, par3EntityPlayer.posX, par3EntityPlayer.posY + 1.62D - (double)par3EntityPlayer.yOffset, par3EntityPlayer.posZ);
                entityendereye.moveTowards((double)chunkposition.x, chunkposition.y, (double)chunkposition.z);
                par2World.spawnEntityInWorld(entityendereye);
                par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                par2World.playAuxSFXAtEntity((EntityPlayer)null, 1002, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ, 0);

                if (!par3EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }
            }
        }

        return par1ItemStack;
    }
}
