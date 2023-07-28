package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BackupList
{
    public List field_111223_a;

    public static BackupList func_111222_a(final String par0Str)
    {
        final BackupList backuplist = new BackupList();
        backuplist.field_111223_a = new ArrayList();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);

            if (jsonrootnode.isArrayNode(new Object[] {"backups"}))
            {
                final Iterator iterator = jsonrootnode.getArrayNode(new Object[] {"backups"}).iterator();

                while (iterator.hasNext())
                {
                    final JsonNode jsonnode = (JsonNode)iterator.next();
                    backuplist.field_111223_a.add(Backup.func_110724_a(jsonnode));
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

        return backuplist;
    }
}
