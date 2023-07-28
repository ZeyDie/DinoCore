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
public class WorldTemplateList extends ValueObject
{
    public List field_110736_a;

    public static WorldTemplateList func_110735_a(final String par0Str)
    {
        final WorldTemplateList worldtemplatelist = new WorldTemplateList();
        worldtemplatelist.field_110736_a = new ArrayList();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);

            if (jsonrootnode.isArrayNode(new Object[] {"templates"}))
            {
                final Iterator iterator = jsonrootnode.getArrayNode(new Object[] {"templates"}).iterator();

                while (iterator.hasNext())
                {
                    final JsonNode jsonnode = (JsonNode)iterator.next();
                    worldtemplatelist.field_110736_a.add(WorldTemplate.func_110730_a(jsonnode));
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

        return worldtemplatelist;
    }
}
