package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class McoServerAddress extends ValueObject
{
    public String field_96417_a;

    public static McoServerAddress func_98162_a(final String par0Str)
    {
        final McoServerAddress mcoserveraddress = new McoServerAddress();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);
            mcoserveraddress.field_96417_a = jsonrootnode.getStringValue(new Object[] {"address"});
        }
        catch (final InvalidSyntaxException invalidsyntaxexception)
        {
            ;
        }
        catch (final IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return mcoserveraddress;
    }
}
