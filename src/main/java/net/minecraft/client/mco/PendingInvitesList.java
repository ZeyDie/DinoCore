package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class PendingInvitesList extends ValueObject
{
    public List field_130096_a = Lists.newArrayList();

    public static PendingInvitesList func_130095_a(final String par0Str)
    {
        final PendingInvitesList pendinginviteslist = new PendingInvitesList();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);

            if (jsonrootnode.isArrayNode(new Object[] {"invites"}))
            {
                final Iterator iterator = jsonrootnode.getArrayNode(new Object[] {"invites"}).iterator();

                while (iterator.hasNext())
                {
                    final JsonNode jsonnode = (JsonNode)iterator.next();
                    pendinginviteslist.field_130096_a.add(PendingInvite.func_130091_a(jsonnode));
                }
            }
        }
        catch (final InvalidSyntaxException invalidsyntaxexception)
        {
            ;
        }

        return pendinginviteslist;
    }
}
