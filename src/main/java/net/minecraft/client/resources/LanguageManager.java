package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.StringTranslate;

import java.io.IOException;
import java.util.*;

@SideOnly(Side.CLIENT)
public class LanguageManager implements ResourceManagerReloadListener
{
    private final MetadataSerializer field_135047_b;
    private String currentLanguage;
    protected static final Locale currentLocale = new Locale();
    private Map languageMap = Maps.newHashMap();

    public LanguageManager(final MetadataSerializer par1MetadataSerializer, final String par2Str)
    {
        this.field_135047_b = par1MetadataSerializer;
        this.currentLanguage = par2Str;
        I18n.setLocale(currentLocale);
    }

    public void parseLanguageMetadata(final List par1List)
    {
        this.languageMap.clear();
        final Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            final ResourcePack resourcepack = (ResourcePack)iterator.next();

            try
            {
                final LanguageMetadataSection languagemetadatasection = (LanguageMetadataSection)resourcepack.getPackMetadata(this.field_135047_b, "language");

                if (languagemetadatasection != null)
                {
                    final Iterator iterator1 = languagemetadatasection.getLanguages().iterator();

                    while (iterator1.hasNext())
                    {
                        final Language language = (Language)iterator1.next();

                        if (!this.languageMap.containsKey(language.getLanguageCode()))
                        {
                            this.languageMap.put(language.getLanguageCode(), language);
                        }
                    }
                }
            }
            catch (final RuntimeException runtimeexception)
            {
                Minecraft.getMinecraft().getLogAgent().logWarningException("Unable to parse metadata section of resourcepack: " + resourcepack.getPackName(), runtimeexception);
            }
            catch (final IOException ioexception)
            {
                Minecraft.getMinecraft().getLogAgent().logWarningException("Unable to parse metadata section of resourcepack: " + resourcepack.getPackName(), ioexception);
            }
        }
    }

    public void onResourceManagerReload(final ResourceManager par1ResourceManager)
    {
        final ArrayList arraylist = Lists.newArrayList(new String[] {"en_US"});

        if (!"en_US".equals(this.currentLanguage))
        {
            arraylist.add(this.currentLanguage);
        }

        currentLocale.loadLocaleDataFiles(par1ResourceManager, arraylist);
        LanguageRegistry.instance().loadLanguageTable(currentLocale.field_135032_a, this.currentLanguage);
        StringTranslate.func_135063_a(currentLocale.field_135032_a);
    }

    public boolean isCurrentLocaleUnicode()
    {
        return currentLocale.isUnicode();
    }

    public boolean isCurrentLanguageBidirectional()
    {
        return this.getCurrentLanguage().isBidirectional();
    }

    public void setCurrentLanguage(final Language par1Language)
    {
        this.currentLanguage = par1Language.getLanguageCode();
    }

    public Language getCurrentLanguage()
    {
        return this.languageMap.containsKey(this.currentLanguage) ? (Language)this.languageMap.get(this.currentLanguage) : (Language)this.languageMap.get("en_US");
    }

    public SortedSet getLanguages()
    {
        return Sets.newTreeSet(this.languageMap.values());
    }
}
