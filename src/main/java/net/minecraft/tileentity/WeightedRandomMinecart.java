package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomItem;

public class WeightedRandomMinecart extends WeightedRandomItem
{
    public final NBTTagCompound field_98222_b;
    public final String minecartName;

    final MobSpawnerBaseLogic field_98221_d;

    public WeightedRandomMinecart(final MobSpawnerBaseLogic par1MobSpawnerBaseLogic, final NBTTagCompound par2NBTTagCompound)
    {
        super(par2NBTTagCompound.getInteger("Weight"));
        this.field_98221_d = par1MobSpawnerBaseLogic;
        final NBTTagCompound nbttagcompound1 = par2NBTTagCompound.getCompoundTag("Properties");
        String s = par2NBTTagCompound.getString("Type");

        if (s.equals("Minecart"))
        {
            if (nbttagcompound1 != null)
            {
                switch (nbttagcompound1.getInteger("Type"))
                {
                    case 0:
                        s = "MinecartRideable";
                        break;
                    case 1:
                        s = "MinecartChest";
                        break;
                    case 2:
                        s = "MinecartFurnace";
                }
            }
            else
            {
                s = "MinecartRideable";
            }
        }

        this.field_98222_b = nbttagcompound1;
        this.minecartName = s;
    }

    public WeightedRandomMinecart(final MobSpawnerBaseLogic par1MobSpawnerBaseLogic, final NBTTagCompound par2NBTTagCompound, String par3Str)
    {
        super(1);
        String par3Str1 = par3Str;
        this.field_98221_d = par1MobSpawnerBaseLogic;

        if (par3Str1.equals("Minecart"))
        {
            if (par2NBTTagCompound != null)
            {
                switch (par2NBTTagCompound.getInteger("Type"))
                {
                    case 0:
                        par3Str1 = "MinecartRideable";
                        break;
                    case 1:
                        par3Str1 = "MinecartChest";
                        break;
                    case 2:
                        par3Str1 = "MinecartFurnace";
                }
            }
            else
            {
                par3Str1 = "MinecartRideable";
            }
        }

        this.field_98222_b = par2NBTTagCompound;
        this.minecartName = par3Str1;
    }

    public NBTTagCompound func_98220_a()
    {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setCompoundTag("Properties", this.field_98222_b);
        nbttagcompound.setString("Type", this.minecartName);
        nbttagcompound.setInteger("Weight", this.itemWeight);
        return nbttagcompound;
    }
}
