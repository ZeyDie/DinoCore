package net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.data.MetadataSection;

import java.io.InputStream;

@SideOnly(Side.CLIENT)
public interface Resource
{
    InputStream getInputStream();

    boolean hasMetadata();

    MetadataSection getMetadata(String s);
}
