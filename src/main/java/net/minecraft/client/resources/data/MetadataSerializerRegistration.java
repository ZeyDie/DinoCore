package net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class MetadataSerializerRegistration
{
    final MetadataSectionSerializer field_110502_a;
    final Class field_110500_b;

    final MetadataSerializer field_110501_c;

    private MetadataSerializerRegistration(final MetadataSerializer par1MetadataSerializer, final MetadataSectionSerializer par2MetadataSectionSerializer, final Class par3Class)
    {
        this.field_110501_c = par1MetadataSerializer;
        this.field_110502_a = par2MetadataSectionSerializer;
        this.field_110500_b = par3Class;
    }

    MetadataSerializerRegistration(final MetadataSerializer par1MetadataSerializer, final MetadataSectionSerializer par2MetadataSectionSerializer, final Class par3Class, final MetadataSerializerEmptyAnon par4MetadataSerializerEmptyAnon)
    {
        this(par1MetadataSerializer, par2MetadataSectionSerializer, par3Class);
    }
}
