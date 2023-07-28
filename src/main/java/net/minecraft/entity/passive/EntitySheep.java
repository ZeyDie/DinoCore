package net.minecraft.entity.passive;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

import java.util.ArrayList;
import java.util.Random;

// CraftBukkit start
// CraftBukkit end

public class EntitySheep extends EntityAnimal implements IShearable
{
    private final InventoryCrafting field_90016_e = new InventoryCrafting(new ContainerSheep(this), 2, 1);

    /**
     * Holds the RGB table of the sheep colors - in OpenGL glColor3f values - used to render the sheep colored fleece.
     */
    public static final float[][] fleeceColorTable = {{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};

    /**
     * Used to control movement as well as wool regrowth. Set to 40 on handleHealthUpdate and counts down with each
     * tick.
     */
    private int sheepTimer;

    /** The eat grass AI task for this mob. */
    private EntityAIEatGrass aiEatGrass = new EntityAIEatGrass(this);

    public EntitySheep(final World par1World)
    {
        super(par1World);
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Item.wheat.itemID, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, this.aiEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.field_90016_e.setInventorySlotContents(0, new ItemStack(Item.dyePowder, 1, 0));
        this.field_90016_e.setInventorySlotContents(1, new ItemStack(Item.dyePowder, 1, 0));
        this.field_90016_e.resultInventory = new InventoryCraftResult(); // CraftBukkit - add result slot for event
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    //TODO ZoomCodeReplace protected on public
    public boolean isAIEnabled()
    {
        return true;
    }

    //TODO ZoomCodeReplace protected on public
    public void updateAITasks()
    {
        this.sheepTimer = this.aiEatGrass.getEatGrassTick();
        super.updateAITasks();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isRemote)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        super.onLivingUpdate();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(final boolean par1, final int par2)
    {
        if (!this.getSheared())
        {
            this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 0.0F);
        }
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return Block.cloth.blockID;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1)
    {
        if (par1 == 10)
        {
            this.sheepTimer = 40;
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(final EntityPlayer par1EntityPlayer)
    {
        return super.interact(par1EntityPlayer);
        // Cauldron - TODO: missing PlayerShearEvent!
    }

    @SideOnly(Side.CLIENT)
    public float func_70894_j(final float par1)
    {
        return this.sheepTimer <= 0 ? 0.0F : (this.sheepTimer >= 4 && this.sheepTimer <= 36 ? 1.0F : (this.sheepTimer < 4 ? ((float)this.sheepTimer - par1) / 4.0F : -((float)(this.sheepTimer - 40) - par1) / 4.0F));
    }

    @SideOnly(Side.CLIENT)
    public float func_70890_k(final float par1)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            final float f1 = ((float)(this.sheepTimer - 4) - par1) / 32.0F;
            return ((float)Math.PI / 5.0F) + ((float)Math.PI * 7.0F / 100.0F) * MathHelper.sin(f1 * 28.7F);
        }
        else
        {
            return this.sheepTimer > 0 ? ((float)Math.PI / 5.0F) : this.rotationPitch / (180.0F / (float)Math.PI);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Sheared", this.getSheared());
        par1NBTTagCompound.setByte("Color", (byte)this.getFleeceColor());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setSheared(par1NBTTagCompound.getBoolean("Sheared"));
        this.setFleeceColor(par1NBTTagCompound.getByte("Color"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.sheep.say";
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void playStepSound(final int par1, final int par2, final int par3, final int par4)
    {
        this.playSound("mob.sheep.step", 0.15F, 1.0F);
    }

    public int getFleeceColor()
    {
        return this.dataWatcher.getWatchableObjectByte(16) & 15;
    }

    public void setFleeceColor(final int par1)
    {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & 240 | par1 & 15)));
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean getSheared()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(final boolean par1)
    {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (par1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 16)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -17)));
        }
    }

    /**
     * This method is called when a sheep spawns in the world to select the color of sheep fleece.
     */
    public static int getRandomFleeceColor(final Random par0Random)
    {
        final int i = par0Random.nextInt(100);
        return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (par0Random.nextInt(500) == 0 ? 6 : 0))));
    }

    public EntitySheep func_90015_b(final EntityAgeable par1EntityAgeable)
    {
        final EntitySheep entitysheep = (EntitySheep)par1EntityAgeable;
        final EntitySheep entitysheep1 = new EntitySheep(this.worldObj);
        final int i = this.func_90014_a(this, entitysheep);
        entitysheep1.setFleeceColor(15 - i);
        return entitysheep1;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus()
    {
        // CraftBukkit start
        final SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((org.bukkit.entity.Sheep) this.getBukkitEntity());
        this.worldObj.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            this.setSheared(false);
        }

        // CraftBukkit end

        if (this.isChild())
        {
            this.addGrowth(60);
        }
    }

    public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData)
    {
        EntityLivingData par1EntityLivingData1 = super.onSpawnWithEgg(par1EntityLivingData);
        this.setFleeceColor(getRandomFleeceColor(this.worldObj.rand));
        return par1EntityLivingData1;
    }

    private int func_90014_a(final EntityAnimal par1EntityAnimal, final EntityAnimal par2EntityAnimal)
    {
        final int i = this.func_90013_b(par1EntityAnimal);
        final int j = this.func_90013_b(par2EntityAnimal);
        this.field_90016_e.getStackInSlot(0).setItemDamage(i);
        this.field_90016_e.getStackInSlot(1).setItemDamage(j);
        final ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(this.field_90016_e, ((EntitySheep)par1EntityAnimal).worldObj);
        final int k;

        if (itemstack != null && itemstack.getItem().itemID == Item.dyePowder.itemID)
        {
            k = itemstack.getItemDamage();
        }
        else
        {
            k = this.worldObj.rand.nextBoolean() ? i : j;
        }

        return k;
    }

    private int func_90013_b(final EntityAnimal par1EntityAnimal)
    {
        return 15 - ((EntitySheep)par1EntityAnimal).getFleeceColor();
    }

    public EntityAgeable createChild(final EntityAgeable par1EntityAgeable)
    {
        return this.func_90015_b(par1EntityAgeable);
    }

    @Override
    public boolean isShearable(final ItemStack item, final World world, final int X, final int Y, final int Z)
    {
        return !getSheared() && !isChild();
    }

    @Override
    public ArrayList<ItemStack> onSheared(final ItemStack item, final World world, final int X, final int Y, final int Z, final int fortune)
    {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        setSheared(true);
        final int i = 1 + rand.nextInt(3);
        for (int j = 0; j < i; j++)
        {
            ret.add(new ItemStack(Block.cloth.blockID, 1, getFleeceColor()));
        }
        this.worldObj.playSoundAtEntity(this, "mob.sheep.shear", 1.0F, 1.0F);
        return ret;
    }
}
