package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ValueObjectList extends ValueObject
{
    public List field_96430_d;

    public static ValueObjectList func_98161_a(final String par0Str)
    {
        final ValueObjectList valueobjectlist = new ValueObjectList();
        valueobjectlist.field_96430_d = new ArrayList();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);

            if (jsonrootnode.isArrayNode(new Object[] {"servers"}))
            {
                final Iterator iterator = jsonrootnode.getArrayNode(new Object[] {"servers"}).iterator();

                while (iterator.hasNext())
                {
                    final JsonNode jsonnode = (JsonNode)iterator.next();
                    valueobjectlist.field_96430_d.add(McoServer.func_98163_a(jsonnode));
                }
            }
        }
        catch (final InvalidSyntaxException invalidsyntaxexception)
        {
            ;
        }
        catch (final IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return valueobjectlist;
    }
}
