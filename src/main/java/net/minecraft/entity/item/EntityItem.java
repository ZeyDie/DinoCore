package net.minecraft.entity.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Iterator;

public class EntityItem extends Entity
{
    /**
     * The age of this EntityItem (used to animate it up and down as well as expire it)
     */
    public int age;
    public int delayBeforeCanPickup;

    /** The health of this EntityItem. (For example, damage for tools) */
    private int health;

    /** The EntityItem's random initial float height. */
    public float hoverStart;
    private int lastTick = MinecraftServer.currentTick; // CraftBukkit

    /**
     * The maximum age of this EntityItem.  The item is expired once this is reached.
     */
    public int lifespan = 6000;

    public EntityItem(final World par1World, final double par2, final double par4, final double par6)
    {
        super(par1World);
        this.age = 0;
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(par2, par4, par6);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        if (par1World != null && par1World.spigotConfig != null) { // Cauldron - add null check
        this.lifespan = par1World.spigotConfig.itemDespawnRate; // Spigot
        }
    }

    public EntityItem(final World par1World, final double par2, final double par4, final double par6, final ItemStack par8ItemStack)
    {
        this(par1World, par2, par4, par6);
        // CraftBukkit start - Can't set null items in the datawatcher
        if (par8ItemStack == null || par8ItemStack.getItem() == null)
        {
            return;
        }
        // CraftBukkit end
        this.setEntityItemStack(par8ItemStack);
        this.lifespan = (par8ItemStack.getItem() == null ? par1World.spigotConfig.itemDespawnRate : par8ItemStack.getItem().getEntityLifespan(par8ItemStack, par1World)); // Spigot
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(final World par1World)
    {
        super(par1World);
        this.age = 0;
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(10, 5);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        final ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null)
        {
            if (stack.getItem().onEntityItemUpdate(this))
            {
                return;
            }
        }

        super.onUpdate();
        // CraftBukkit start - Use wall time for pickup and despawn timers
        final int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.delayBeforeCanPickup -= elapsedTicks;
        this.age += elapsedTicks;
        this.lastTick = MinecraftServer.currentTick;
        // CraftBukkit end

        final boolean forceUpdate = this.ticksExisted > 0 && this.ticksExisted % 25 == 0; // Cauldron - optimize item tick updates
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        // Cauldron start - if forced
        if (forceUpdate || noClip) {
            this.noClip = this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        }
        // Cauldron end
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        final boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

        if ((flag && this.ticksExisted % 5 == 0) || forceUpdate) // Cauldron - if forced
        {
            if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava)
            {
                this.motionY = 0.20000000298023224D;
                this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (forceUpdate && !this.worldObj.isRemote) // Cauldron - if forced
            {
                this.searchForOtherItemsNearby();
            }
        }

        float f = 0.98F;

        if (this.onGround)
        {
            f = 0.58800006F;
            final int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));

            if (i > 0)
            {
                f = Block.blocksList[i].slipperiness * 0.98F;
            }
        }

        this.motionX *= (double)f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double)f;

        if (this.onGround)
        {
            this.motionY *= -0.5D;
        }

        // ++this.age; // CraftBukkit - Moved up (base age on wall time)

        final ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);
        
        if (!this.worldObj.isRemote && this.age >= lifespan - 1) // Cauldron adjust for age being off by one when it is first dropped
        {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory.callItemDespawnEvent(this).isCancelled())
            {
                this.age = 0;
                return;
            }
            // CraftBukkit end
            if (item != null)
            {   
                final ItemExpireEvent event = new ItemExpireEvent(this, (item.getItem() == null ? this.worldObj.spigotConfig.itemDespawnRate : item.getItem().getEntityLifespan(item, worldObj))); // Spigot
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    lifespan += event.extraLife;
                }
                else
                {
                    this.setDead();
                }
            }
            else
            {
                this.setDead();
            }
        }

        if (item != null && item.stackSize <= 0)
        {
            this.setDead();
        }
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    private void searchForOtherItemsNearby()
    {
        // Spigot start
        final double radius = worldObj.spigotConfig.itemMerge;
        final Iterator iterator = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(radius, radius, radius)).iterator();
        // Spigot end
        
        while (iterator.hasNext())
        {
            final EntityItem entityitem = (EntityItem)iterator.next();
            this.combineItems(entityitem);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will  be removed from the world.
     */
    public boolean combineItems(final EntityItem par1EntityItem)
    {
        if (par1EntityItem == this)
        {
            return false;
        }
        else if (par1EntityItem.isEntityAlive() && this.isEntityAlive())
        {
            final ItemStack itemstack = this.getEntityItem();
            final ItemStack itemstack1 = par1EntityItem.getEntityItem();

            if (itemstack1.getItem() != itemstack.getItem())
            {
                return false;
            }
            else if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound())
            {
                return false;
            }
            else if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound().equals(itemstack.getTagCompound()))
            {
                return false;
            }
            else if (itemstack1.getItem().getHasSubtypes() && itemstack1.getItemDamage() != itemstack.getItemDamage())
            {
                return false;
            }
            else if (itemstack1.stackSize < itemstack.stackSize)
            {
                return par1EntityItem.combineItems(this);
            }
            else if (itemstack1.stackSize + itemstack.stackSize > itemstack1.getMaxStackSize())
            {
                return false;
            }
            else
            {
                itemstack1.stackSize += itemstack.stackSize;
                par1EntityItem.delayBeforeCanPickup = Math.max(par1EntityItem.delayBeforeCanPickup, this.delayBeforeCanPickup);
                par1EntityItem.age = Math.min(par1EntityItem.age, this.age);
                par1EntityItem.setEntityItemStack(itemstack1);
                this.setDead();
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * sets the age of the item so that it'll despawn one minute after it has been dropped (instead of five). Used when
     * items are dropped from players in creative mode
     */
    public void setAgeToCreativeDespawnTime()
    {
        this.age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(final int par1)
    {
        this.attackEntityFrom(DamageSource.inFire, (float)par1);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (this.getEntityItem() != null && this.getEntityItem().itemID == Item.netherStar.itemID && par1DamageSource.isExplosion())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            this.health = (int)((float)this.health - par2);

            if (this.health <= 0)
            {
                this.setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(final NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setShort("Health", (short)((byte)this.health));
        par1NBTTagCompound.setShort("Age", (short)this.age);
        par1NBTTagCompound.setInteger("Lifespan", lifespan);

        if (this.getEntityItem() != null)
        {
            par1NBTTagCompound.setCompoundTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(final NBTTagCompound par1NBTTagCompound)
    {
        this.health = par1NBTTagCompound.getShort("Health") & 255;
        this.age = par1NBTTagCompound.getShort("Age");
        final NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("Item");
        // CraftBukkit start
        if (nbttagcompound1 != null)
        {
            final ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            if (itemstack != null)
            {
                this.setEntityItemStack(itemstack);
            }
            else
            {
                this.setDead();
            }
        }
        else
        {
            this.setDead();
        }
        // CraftBukkit end

        final ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

        if (item == null || item.stackSize <= 0)
        {
            this.setDead();
        }

        if (par1NBTTagCompound.hasKey("Lifespan"))
        {
            lifespan = par1NBTTagCompound.getInteger("Lifespan");
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(final EntityPlayer par1EntityPlayer)
    {
        if (!this.worldObj.isRemote)
        {
            if (this.delayBeforeCanPickup > 0)
            {
                return;
            }

            final EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, this);

            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return;
            }

            final ItemStack itemstack = this.getEntityItem();
            final int i = itemstack.stackSize;

            // CraftBukkit start
            final int canHold = par1EntityPlayer.inventory.canHold(itemstack);
            final int remaining = itemstack.stackSize - canHold;

            if (this.delayBeforeCanPickup <= 0 && canHold > 0)
            {
                itemstack.stackSize = canHold;
                // Cauldron start - rename to cbEvent to fix naming collision
                final PlayerPickupItemEvent cbEvent = new PlayerPickupItemEvent((org.bukkit.entity.Player) par1EntityPlayer.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
                //cbEvent.setCancelled(!par1EntityPlayer.canPickUpLoot); TODO
                this.worldObj.getServer().getPluginManager().callEvent(cbEvent);
                itemstack.stackSize = canHold + remaining;

                if (cbEvent.isCancelled())
                {
                    return;
                }
                // Cauldron end

                // Possibly < 0; fix here so we do not have to modify code below
                this.delayBeforeCanPickup = 0;
            }

            // CraftBukkit end

            if (this.delayBeforeCanPickup <= 0 && (event.getResult() == Result.ALLOW || i <= 0 || par1EntityPlayer.inventory.addItemStackToInventory(itemstack)))
            {
                if (itemstack.itemID == Block.wood.blockID)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.mineWood);
                }

                if (itemstack.itemID == Item.leather.itemID)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.killCow);
                }

                if (itemstack.itemID == Item.diamond.itemID)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.diamonds);
                }

                if (itemstack.itemID == Item.blazeRod.itemID)
                {
                    par1EntityPlayer.triggerAchievement(AchievementList.blazeRod);
                }

                GameRegistry.onPickupNotification(par1EntityPlayer, this);

                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, i);

                if (itemstack.stackSize <= 0)
                {
                    this.setDead();
                }
            }
        }
    }

    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
        return StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(final int par1)
    {
        super.travelToDimension(par1);

        if (!this.worldObj.isRemote)
        {
            this.searchForOtherItemsNearby();
        }
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    public ItemStack getEntityItem()
    {
        final ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);

        if (itemstack == null)
        {
            if (this.worldObj != null)
            {
                this.worldObj.getWorldLogAgent().logSevere("Item entity " + this.entityId + " has no item?!");
            }

            return new ItemStack(Block.stone);
        }
        else
        {
            return itemstack;
        }
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(final ItemStack par1ItemStack)
    {
        this.getDataWatcher().updateObject(10, par1ItemStack);
        this.getDataWatcher().setObjectWatched(10);
    }
}
