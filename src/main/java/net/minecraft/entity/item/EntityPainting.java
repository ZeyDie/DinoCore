package net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumArt;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityPainting extends EntityHanging
{
    public EnumArt art;

    public EntityPainting(final World par1World)
    {
        super(par1World);
        this.art = EnumArt.values()[this.rand.nextInt(EnumArt.values().length)]; // CraftBukkit - generate a non-null painting
    }

    public EntityPainting(final World par1World, final int par2, final int par3, final int par4, final int par5)
    {
        super(par1World, par2, par3, par4, par5);
        final ArrayList arraylist = new ArrayList();
        final EnumArt[] aenumart = EnumArt.values();
        final int i1 = aenumart.length;

        for (int j1 = 0; j1 < i1; ++j1)
        {
            final EnumArt enumart = aenumart[j1];
            this.art = enumart;
            this.setDirection(par5);

            if (this.onValidSurface())
            {
                arraylist.add(enumart);
            }
        }

        if (!arraylist.isEmpty())
        {
            this.art = (EnumArt)arraylist.get(this.rand.nextInt(arraylist.size()));
        }

        this.setDirection(par5);
    }

    @SideOnly(Side.CLIENT)
    public EntityPainting(final World par1World, final int par2, final int par3, final int par4, final int par5, final String par6Str)
    {
        this(par1World, par2, par3, par4, par5);
        final EnumArt[] aenumart = EnumArt.values();
        final int i1 = aenumart.length;

        for (int j1 = 0; j1 < i1; ++j1)
        {
            final EnumArt enumart = aenumart[j1];

            if (enumart.title.equals(par6Str))
            {
                this.art = enumart;
                break;
            }
        }

        this.setDirection(par5);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setString("Motive", this.art.title);
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        final String s = par1NBTTagCompound.getString("Motive");
        final EnumArt[] aenumart = EnumArt.values();
        final int i = aenumart.length;

        for (int j = 0; j < i; ++j)
        {
            final EnumArt enumart = aenumart[j];

            if (enumart.title.equals(s))
            {
                this.art = enumart;
            }
        }

        if (this.art == null)
        {
            this.art = EnumArt.Kebab;
        }

        super.readEntityFromNBT(par1NBTTagCompound);
    }

    public int getWidthPixels()
    {
        return this.art.sizeX;
    }

    public int getHeightPixels()
    {
        return this.art.sizeY;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(final Entity par1Entity)
    {
        if (par1Entity instanceof EntityPlayer)
        {
            final EntityPlayer entityplayer = (EntityPlayer)par1Entity;

            if (entityplayer.capabilities.isCreativeMode)
            {
                return;
            }
        }

        this.entityDropItem(new ItemStack(Item.painting), 0.0F);
    }
}
