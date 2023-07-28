package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_6_R3.util.BlockStateListPopulator;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

// CraftBukkit start
// CraftBukkit end

public class BlockPumpkin extends BlockDirectional
{
    /** Boolean used to seperate different states of blocks */
    private boolean blockType;
    @SideOnly(Side.CLIENT)
    private Icon field_94474_b;
    @SideOnly(Side.CLIENT)
    private Icon field_94475_c;

    protected BlockPumpkin(final int par1, final boolean par2)
    {
        super(par1, Material.pumpkin);
        this.setTickRandomly(true);
        this.blockType = par2;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(final int par1, final int par2)
    {
        return par1 == 1 ? this.field_94474_b : (par1 == 0 ? this.field_94474_b : (par2 == 2 && par1 == 2 ? this.field_94475_c : (par2 == 3 && par1 == 5 ? this.field_94475_c : (par2 == 0 && par1 == 3 ? this.field_94475_c : (par2 == 1 && par1 == 4 ? this.field_94475_c : this.blockIcon)))));
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(final World par1World, final int par2, final int par3, final int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockSnow.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockSnow.blockID)
        {
            if (!par1World.isRemote)
            {
                // CraftBukkit start - Use BlockStateListPopulator
                final BlockStateListPopulator blockList = new BlockStateListPopulator(par1World.getWorld());
                blockList.setTypeId(par2, par3, par4, 0);
                blockList.setTypeId(par2, par3 - 1, par4, 0);
                blockList.setTypeId(par2, par3 - 2, par4, 0);
                final EntitySnowman entitysnowman = new EntitySnowman(par1World);
                entitysnowman.setLocationAndAngles((double)par2 + 0.5D, (double)par3 - 1.95D, (double)par4 + 0.5D, 0.0F, 0.0F);

                if (par1World.addEntity(entitysnowman, SpawnReason.BUILD_SNOWMAN))
                {
                    blockList.updateList();
                }

                // CraftBukkit end
            }

            for (int l = 0; l < 120; ++l)
            {
                par1World.spawnParticle("snowshovel", (double)par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 2.5D, (double)par4 + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
        else if (par1World.getBlockId(par2, par3 - 1, par4) == Block.blockIron.blockID && par1World.getBlockId(par2, par3 - 2, par4) == Block.blockIron.blockID)
        {
            final boolean flag = par1World.getBlockId(par2 - 1, par3 - 1, par4) == Block.blockIron.blockID && par1World.getBlockId(par2 + 1, par3 - 1, par4) == Block.blockIron.blockID;
            final boolean flag1 = par1World.getBlockId(par2, par3 - 1, par4 - 1) == Block.blockIron.blockID && par1World.getBlockId(par2, par3 - 1, par4 + 1) == Block.blockIron.blockID;

            if (flag || flag1)
            {
                // CraftBukkit start - Use BlockStateListPopulator
                final BlockStateListPopulator blockList = new BlockStateListPopulator(par1World.getWorld());
                blockList.setTypeId(par2, par3, par4, 0);
                blockList.setTypeId(par2, par3 - 1, par4, 0);
                blockList.setTypeId(par2, par3 - 2, par4, 0);

                if (flag)
                {
                    blockList.setTypeId(par2 - 1, par3 - 1, par4, 0);
                    blockList.setTypeId(par2 + 1, par3 - 1, par4, 0);
                }
                else
                {
                    blockList.setTypeId(par2, par3 - 1, par4 - 1, 0);
                    blockList.setTypeId(par2, par3 - 1, par4 + 1, 0);
                }

                final EntityIronGolem entityirongolem = new EntityIronGolem(par1World);
                entityirongolem.setPlayerCreated(true);
                entityirongolem.setLocationAndAngles((double)par2 + 0.5D, (double)par3 - 1.95D, (double)par4 + 0.5D, 0.0F, 0.0F);

                if (par1World.addEntity(entityirongolem, SpawnReason.BUILD_IRONGOLEM))
                {
                    for (int i1 = 0; i1 < 120; ++i1)
                    {
                        par1World.spawnParticle("snowballpoof", (double) par2 + par1World.rand.nextDouble(), (double)(par3 - 2) + par1World.rand.nextDouble() * 3.9D, (double) par4 + par1World.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
                    }

                    blockList.updateList();
                }
                // CraftBukkit end
            }
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(final World par1World, final int par2, final int par3, final int par4)
    {
        final int l = par1World.getBlockId(par2, par3, par4);
        final Block block = Block.blocksList[l];
        return (block == null || block.isBlockReplaceable(par1World, par2, par3, par4)) && par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4);
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(final World par1World, final int par2, final int par3, final int par4, final EntityLivingBase par5EntityLivingBase, final ItemStack par6ItemStack)
    {
        final int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
    }

    // CraftBukkit start
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(final World world, final int i, final int j, final int k, final int l)
    {
        if (Block.blocksList[l] != null && Block.blocksList[l].canProvidePower())
        {
            final org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
            final int power = block.getBlockPower();
            final BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, power, power);
            world.getServer().getPluginManager().callEvent(eventRedstone);
        }
    }
    // CraftBukkit end

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(final IconRegister par1IconRegister)
    {
        this.field_94475_c = par1IconRegister.registerIcon(this.getTextureName() + "_face_" + (this.blockType ? "on" : "off"));
        this.field_94474_b = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
    }
}
