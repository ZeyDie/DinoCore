package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.TaskLongRunning;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.WorldTemplate;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@SideOnly(Side.CLIENT)
class TaskWorldCreation extends TaskLongRunning
{
    private final String field_96589_c;
    private final String field_96587_d;
    private final String field_104065_f;
    private final WorldTemplate field_111253_f;

    final GuiScreenCreateOnlineWorld field_96590_a;

    public TaskWorldCreation(final GuiScreenCreateOnlineWorld par1GuiScreenCreateOnlineWorld, final String par2Str, final String par3Str, final String par4Str, final WorldTemplate par5WorldTemplate)
    {
        this.field_96590_a = par1GuiScreenCreateOnlineWorld;
        this.field_96589_c = par2Str;
        this.field_96587_d = par3Str;
        this.field_104065_f = par4Str;
        this.field_111253_f = par5WorldTemplate;
    }

    public void run()
    {
        final String s = I18n.getString("mco.create.world.wait");
        this.setMessage(s);
        final McoClient mcoclient = new McoClient(GuiScreenCreateOnlineWorld.func_96248_a(this.field_96590_a).getSession());

        try
        {
            if (this.field_111253_f != null)
            {
                mcoclient.func_96386_a(this.field_96589_c, this.field_96587_d, this.field_104065_f, this.field_111253_f.field_110734_a);
            }
            else
            {
                mcoclient.func_96386_a(this.field_96589_c, this.field_96587_d, this.field_104065_f, "-1");
            }

            GuiScreenCreateOnlineWorld.func_96246_c(this.field_96590_a).displayGuiScreen(GuiScreenCreateOnlineWorld.func_96247_b(this.field_96590_a));
        }
        catch (final ExceptionMcoService exceptionmcoservice)
        {
            GuiScreenCreateOnlineWorld.func_130026_d(this.field_96590_a).getLogAgent().logSevere(exceptionmcoservice.toString());
            this.setFailedMessage(exceptionmcoservice.toString());
        }
        catch (final UnsupportedEncodingException unsupportedencodingexception)
        {
            GuiScreenCreateOnlineWorld.func_130027_e(this.field_96590_a).getLogAgent().logWarning("Realms: " + unsupportedencodingexception.getLocalizedMessage());
            this.setFailedMessage(unsupportedencodingexception.getLocalizedMessage());
        }
        catch (final IOException ioexception)
        {
            GuiScreenCreateOnlineWorld.func_130028_f(this.field_96590_a).getLogAgent().logWarning("Realms: could not parse response");
            this.setFailedMessage(ioexception.getLocalizedMessage());
        }
        catch (final Exception exception)
        {
            this.setFailedMessage(exception.getLocalizedMessage());
        }
    }
}
