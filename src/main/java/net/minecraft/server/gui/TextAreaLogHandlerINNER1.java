package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@SideOnly(Side.SERVER)
class TextAreaLogHandlerINNER1 extends Formatter
{
    final TextAreaLogHandler field_120031_a;

    TextAreaLogHandlerINNER1(final TextAreaLogHandler par1TextAreaLogHandler)
    {
        this.field_120031_a = par1TextAreaLogHandler;
    }

    public String format(final LogRecord par1LogRecord)
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(" [").append(par1LogRecord.getLevel().getName()).append("] ");
        stringbuilder.append(this.formatMessage(par1LogRecord));
        stringbuilder.append('\n');
        final Throwable throwable = par1LogRecord.getThrown();

        if (throwable != null)
        {
            final StringWriter stringwriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter.toString());
        }

        return stringbuilder.toString();
    }
}
