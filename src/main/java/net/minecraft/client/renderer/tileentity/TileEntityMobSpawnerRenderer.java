package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer
{
    public void renderTileEntityMobSpawner(final TileEntityMobSpawner par1TileEntityMobSpawner, final double par2, final double par4, final double par6, final float par8)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 + 0.5F, (float)par4, (float)par6 + 0.5F);
        func_98144_a(par1TileEntityMobSpawner.getSpawnerLogic(), par2, par4, par6, par8);
        GL11.glPopMatrix();
    }

    public static void func_98144_a(final MobSpawnerBaseLogic par0MobSpawnerBaseLogic, final double par1, final double par3, final double par5, final float par7)
    {
        final Entity entity = par0MobSpawnerBaseLogic.func_98281_h();

        if (entity != null)
        {
            entity.setWorld(par0MobSpawnerBaseLogic.getSpawnerWorld());
            final float f1 = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GL11.glRotatef((float)(par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double)par7) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            entity.setLocationAndAngles(par1, par3, par5, 0.0F, 0.0F);
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, par7);
        }
    }

    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4, final double par6, final float par8)
    {
        this.renderTileEntityMobSpawner((TileEntityMobSpawner)par1TileEntity, par2, par4, par6, par8);
    }
}
