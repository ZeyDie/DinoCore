package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.ResourceManager;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public interface TextureObject
{
    void loadTexture(ResourceManager resourcemanager) throws IOException;

    int getGlTextureId();
}
