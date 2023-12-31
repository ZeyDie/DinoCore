package net.minecraft.client.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityOtherPlayerMP extends AbstractClientPlayer
{
    private boolean isItemInUse;
    private int otherPlayerMPPosRotationIncrements;
    private double otherPlayerMPX;
    private double otherPlayerMPY;
    private double otherPlayerMPZ;
    private double otherPlayerMPYaw;
    private double otherPlayerMPPitch;

    public EntityOtherPlayerMP(final World par1World, final String par2Str)
    {
        super(par1World, par2Str);
        this.yOffset = 0.0F;
        this.stepHeight = 0.0F;
        this.noClip = true;
        this.field_71082_cx = 0.25F;
        this.renderDistanceWeight = 10.0D;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight()
    {
        this.yOffset = 0.0F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(final DamageSource par1DamageSource, final float par2)
    {
        return true;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(final double par1, final double par3, final double par5, final float par7, final float par8, final int par9)
    {
        this.otherPlayerMPX = par1;
        this.otherPlayerMPY = par3;
        this.otherPlayerMPZ = par5;
        this.otherPlayerMPYaw = (double)par7;
        this.otherPlayerMPPitch = (double)par8;
        this.otherPlayerMPPosRotationIncrements = par9;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.field_71082_cx = 0.0F;
        super.onUpdate();
        this.prevLimbSwingAmount = this.limbSwingAmount;
        final double d0 = this.posX - this.prevPosX;
        final double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;

        if (f > 1.0F)
        {
            f = 1.0F;
        }

        this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;

        if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null)
        {
            final ItemStack itemstack = this.inventory.mainInventory[this.inventory.currentItem];
            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], Item.itemsList[itemstack.itemID].getMaxItemUseDuration(itemstack));
            this.isItemInUse = true;
        }
        else if (this.isItemInUse && !this.isEating())
        {
            this.clearItemInUse();
            this.isItemInUse = false;
        }
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.updateEntityActionState();

        if (this.otherPlayerMPPosRotationIncrements > 0)
        {
            final double d0 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
            final double d1 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
            final double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;
            double d3;

            for (d3 = this.otherPlayerMPYaw - (double)this.rotationYaw; d3 < -180.0D; d3 += 360.0D)
            {
                ;
            }

            while (d3 >= 180.0D)
            {
                d3 -= 360.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.otherPlayerMPPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
            --this.otherPlayerMPPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        this.prevCameraYaw = this.cameraYaw;
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f1 = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;

        if (f > 0.1F)
        {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0.0F)
        {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0.0F)
        {
            f1 = 0.0F;
        }

        this.cameraYaw += (f - this.cameraYaw) * 0.4F;
        this.cameraPitch += (f1 - this.cameraPitch) * 0.8F;
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(final int par1, final ItemStack par2ItemStack)
    {
        if (par1 == 0)
        {
            this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
        }
        else
        {
            this.inventory.armorInventory[par1 - 1] = par2ItemStack;
        }
    }

    @Override
    public float getDefaultEyeHeight()
    {
        return 1.82F;
    }

    public void sendChatToPlayer(final ChatMessageComponent par1ChatMessageComponent)
    {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(par1ChatMessageComponent.toStringWithFormatting(true));
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(final int par1, final String par2Str)
    {
        return false;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
    }
}
