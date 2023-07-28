package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;

import java.util.Iterator;
import java.util.List;

public class ChatMessageComponent
{
    private static final Gson field_111089_a = (new GsonBuilder()).registerTypeAdapter(ChatMessageComponent.class, new MessageComponentSerializer()).create();
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
    private Boolean obfuscated;
    private String text;
    private String translationKey;
    private List field_111091_i;

    public ChatMessageComponent() {}

    public ChatMessageComponent(final ChatMessageComponent par1ChatMessageComponent)
    {
        this.color = par1ChatMessageComponent.color;
        this.bold = par1ChatMessageComponent.bold;
        this.italic = par1ChatMessageComponent.italic;
        this.underline = par1ChatMessageComponent.underline;
        this.obfuscated = par1ChatMessageComponent.obfuscated;
        this.text = par1ChatMessageComponent.text;
        this.translationKey = par1ChatMessageComponent.translationKey;
        this.field_111091_i = par1ChatMessageComponent.field_111091_i == null ? null : Lists.newArrayList(par1ChatMessageComponent.field_111091_i);
    }

    public ChatMessageComponent setColor(final EnumChatFormatting par1EnumChatFormatting)
    {
        if (par1EnumChatFormatting != null && !par1EnumChatFormatting.isColor())
        {
            throw new IllegalArgumentException("Argument is not a valid color!");
        }
        else
        {
            this.color = par1EnumChatFormatting;
            return this;
        }
    }

    public EnumChatFormatting getColor()
    {
        return this.color;
    }

    public ChatMessageComponent setBold(final Boolean par1)
    {
        this.bold = par1;
        return this;
    }

    public Boolean isBold()
    {
        return this.bold;
    }

    public ChatMessageComponent setItalic(final Boolean par1)
    {
        this.italic = par1;
        return this;
    }

    public Boolean isItalic()
    {
        return this.italic;
    }

    public ChatMessageComponent setUnderline(final Boolean par1)
    {
        this.underline = par1;
        return this;
    }

    public Boolean isUnderline()
    {
        return this.underline;
    }

    public ChatMessageComponent setObfuscated(final Boolean par1)
    {
        this.obfuscated = par1;
        return this;
    }

    public Boolean isObfuscated()
    {
        return this.obfuscated;
    }

    protected String getText()
    {
        return this.text;
    }

    protected String getTranslationKey()
    {
        return this.translationKey;
    }

    protected List getSubComponents()
    {
        return this.field_111091_i;
    }

    public ChatMessageComponent appendComponent(final ChatMessageComponent par1ChatMessageComponent)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(par1ChatMessageComponent);
            }
            else
            {
                this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {par1ChatMessageComponent});
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), par1ChatMessageComponent});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    public ChatMessageComponent addText(final String par1Str)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromText(par1Str));
            }
            else
            {
                this.text = par1Str;
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromText(par1Str)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    /**
     * Appends a translated string.
     */
    public ChatMessageComponent addKey(final String par1Str)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromTranslationKey(par1Str));
            }
            else
            {
                this.translationKey = par1Str;
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromTranslationKey(par1Str)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    /**
     * Appends a formatted translation key. Args: key, params. The text ultimately displayed is
     * String.format(translate(key), params)
     */
    public ChatMessageComponent addFormatted(final String par1Str, final Object ... par2ArrayOfObj)
    {
        if (this.text == null && this.translationKey == null)
        {
            if (this.field_111091_i != null)
            {
                this.field_111091_i.add(createFromTranslationWithSubstitutions(par1Str, par2ArrayOfObj));
            }
            else
            {
                this.translationKey = par1Str;
                this.field_111091_i = Lists.newArrayList();
                final Object[] aobject = par2ArrayOfObj;
                final int i = par2ArrayOfObj.length;

                for (int j = 0; j < i; ++j)
                {
                    final Object object1 = aobject[j];

                    if (object1 instanceof ChatMessageComponent)
                    {
                        this.field_111091_i.add((ChatMessageComponent)object1);
                    }
                    else
                    {
                        this.field_111091_i.add(createFromText(object1.toString()));
                    }
                }
            }
        }
        else
        {
            this.field_111091_i = Lists.newArrayList(new ChatMessageComponent[] {new ChatMessageComponent(this), createFromTranslationWithSubstitutions(par1Str, par2ArrayOfObj)});
            this.text = null;
            this.translationKey = null;
        }

        return this;
    }

    public String toString()
    {
        return this.toStringWithFormatting(false);
    }

    public String toStringWithFormatting(final boolean par1)
    {
        return this.toStringWithDefaultFormatting(par1, (EnumChatFormatting)null, false, false, false, false);
    }

    /**
     * args: enableFormat, defaultColor, defaultBold, defaultItalic, defaultUnderline, defaultObfuscated
     */
    public String toStringWithDefaultFormatting(final boolean par1, final EnumChatFormatting par2EnumChatFormatting, final boolean par3, final boolean par4, final boolean par5, final boolean par6)
    {
        final StringBuilder stringbuilder = new StringBuilder();
        final EnumChatFormatting enumchatformatting1 = this.color == null ? par2EnumChatFormatting : this.color;
        final boolean flag5 = this.bold == null ? par3 : this.bold.booleanValue();
        final boolean flag6 = this.italic == null ? par4 : this.italic.booleanValue();
        final boolean flag7 = this.underline == null ? par5 : this.underline.booleanValue();
        final boolean flag8 = this.obfuscated == null ? par6 : this.obfuscated.booleanValue();

        if (this.translationKey != null)
        {
            if (par1)
            {
                appendFormattingToString(stringbuilder, enumchatformatting1, flag5, flag6, flag7, flag8);
            }

            if (this.field_111091_i != null)
            {
                final String[] astring = new String[this.field_111091_i.size()];

                for (int i = 0; i < this.field_111091_i.size(); ++i)
                {
                    astring[i] = ((ChatMessageComponent)this.field_111091_i.get(i)).toStringWithDefaultFormatting(par1, enumchatformatting1, flag5, flag6, flag7, flag8);
                }

                stringbuilder.append(StatCollector.translateToLocalFormatted(this.translationKey, (Object) astring));
            }
            else
            {
                stringbuilder.append(StatCollector.translateToLocal(this.translationKey));
            }
        }
        else if (this.text != null)
        {
            if (par1)
            {
                appendFormattingToString(stringbuilder, enumchatformatting1, flag5, flag6, flag7, flag8);
            }

            stringbuilder.append(this.text);
        }
        else
        {
            ChatMessageComponent chatmessagecomponent;

            if (this.field_111091_i != null)
            {
                for (final Iterator iterator = this.field_111091_i.iterator(); iterator.hasNext(); stringbuilder.append(chatmessagecomponent.toStringWithDefaultFormatting(par1, enumchatformatting1, flag5, flag6, flag7, flag8)))
                {
                    chatmessagecomponent = (ChatMessageComponent)iterator.next();

                    if (par1)
                    {
                        appendFormattingToString(stringbuilder, enumchatformatting1, flag5, flag6, flag7, flag8);
                    }
                }
            }
        }

        return stringbuilder.toString();
    }

    private static void appendFormattingToString(final StringBuilder par0StringBuilder, final EnumChatFormatting par1EnumChatFormatting, final boolean par2, final boolean par3, final boolean par4, final boolean par5)
    {
        if (par1EnumChatFormatting != null)
        {
            par0StringBuilder.append(par1EnumChatFormatting);
        }
        else if (par2 || par3 || par4 || par5)
        {
            par0StringBuilder.append(EnumChatFormatting.RESET);
        }

        if (par2)
        {
            par0StringBuilder.append(EnumChatFormatting.BOLD);
        }

        if (par3)
        {
            par0StringBuilder.append(EnumChatFormatting.ITALIC);
        }

        if (par4)
        {
            par0StringBuilder.append(EnumChatFormatting.UNDERLINE);
        }

        if (par5)
        {
            par0StringBuilder.append(EnumChatFormatting.OBFUSCATED);
        }
    }

    @SideOnly(Side.CLIENT)
    public static ChatMessageComponent createFromJson(final String par0Str)
    {
        try
        {
            return (ChatMessageComponent)field_111089_a.fromJson(par0Str, ChatMessageComponent.class);
        }
        catch (final Throwable throwable)
        {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Deserializing Message");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Serialized Message");
            crashreportcategory.addCrashSection("JSON string", par0Str);
            throw new ReportedException(crashreport);
        }
    }

    public static ChatMessageComponent createFromText(final String par0Str)
    {
        final ChatMessageComponent chatmessagecomponent = new ChatMessageComponent();
        chatmessagecomponent.addText(par0Str);
        return chatmessagecomponent;
    }

    public static ChatMessageComponent createFromTranslationKey(final String par0Str)
    {
        final ChatMessageComponent chatmessagecomponent = new ChatMessageComponent();
        chatmessagecomponent.addKey(par0Str);
        return chatmessagecomponent;
    }

    public static ChatMessageComponent createFromTranslationWithSubstitutions(final String par0Str, final Object ... par1ArrayOfObj)
    {
        final ChatMessageComponent chatmessagecomponent = new ChatMessageComponent();
        chatmessagecomponent.addFormatted(par0Str, par1ArrayOfObj);
        return chatmessagecomponent;
    }

    public String toJson()
    {
        return field_111089_a.toJson(this);
    }
}
