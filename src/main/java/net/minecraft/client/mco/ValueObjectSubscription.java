package net.minecraft.client.mco;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ValueObject;

@SideOnly(Side.CLIENT)
public class ValueObjectSubscription extends ValueObject
{
    public long field_98171_a;
    public int field_98170_b;

    public static ValueObjectSubscription func_98169_a(final String par0Str)
    {
        final ValueObjectSubscription valueobjectsubscription = new ValueObjectSubscription();

        try
        {
            final JsonRootNode jsonrootnode = (new JdomParser()).parse(par0Str);
            valueobjectsubscription.field_98171_a = Long.parseLong(jsonrootnode.getNumberValue(new Object[] {"startDate"}));
            valueobjectsubscription.field_98170_b = Integer.parseInt(jsonrootnode.getNumberValue(new Object[] {"daysLeft"}));
        }
        catch (final InvalidSyntaxException invalidsyntaxexception)
        {
            ;
        }
        catch (final IllegalArgumentException illegalargumentexception)
        {
            ;
        }

        return valueobjectsubscription;
    }
}
