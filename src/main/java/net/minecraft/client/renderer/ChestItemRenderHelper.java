package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@SideOnly(Side.CLIENT)
public class ChestItemRenderHelper
{
    /** The static instance of ChestItemRenderHelper. */
    public static ChestItemRenderHelper instance = new ChestItemRenderHelper();

    /** Instance of Chest's Tile Entity. */
    private TileEntityChest theChest = new TileEntityChest(0);
    private TileEntityChest field_142033_c = new TileEntityChest(1);

    /** Instance of Ender Chest's Tile Entity. */
    private TileEntityEnderChest theEnderChest = new TileEntityEnderChest();

    /**
     * Renders a chest at 0,0,0 - used for item rendering
     */
    public void renderChest(final Block par1Block, final int par2, final float par3)
    {
        if (par1Block.blockID == Block.enderChest.blockID)
        {
            TileEntityRenderer.instance.renderTileEntityAt(this.theEnderChest, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else if (par1Block.blockID == Block.chestTrapped.blockID)
        {
            TileEntityRenderer.instance.renderTileEntityAt(this.field_142033_c, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else
        {
            TileEntityRenderer.instance.renderTileEntityAt(this.theChest, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }
}
