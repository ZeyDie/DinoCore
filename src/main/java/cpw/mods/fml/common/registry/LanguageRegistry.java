/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class LanguageRegistry
{
    private static final LanguageRegistry INSTANCE = new LanguageRegistry();

    private Map<String,Properties> modLanguageData=new HashMap<String,Properties>();

    public static LanguageRegistry instance()
    {
        return INSTANCE;
    }

    public String getStringLocalization(final String key)
    {
        return getStringLocalization(key, FMLCommonHandler.instance().getCurrentLanguage());
    }

    public String getStringLocalization(final String key, final String lang)
    {
        String localizedString = "";
        final Properties langPack = modLanguageData.get(lang);

        if (langPack != null) {
            if (langPack.getProperty(key) != null) {
                localizedString = langPack.getProperty(key);
            }
        }

        return localizedString;
    }

    public void addStringLocalization(final String key, final String value)
    {
        addStringLocalization(key, "en_US", value);
    }
    public void addStringLocalization(final String key, final String lang, final String value)
    {
        Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            langPack=new Properties();
            modLanguageData.put(lang, langPack);
        }
        langPack.put(key,value);
    }

    public void addStringLocalization(final Properties langPackAdditions) {
        addStringLocalization(langPackAdditions, "en_US");
    }

    public void addStringLocalization(final Properties langPackAdditions, final String lang) {
        Properties langPack = modLanguageData.get(lang);
        if (langPack == null) {
            langPack = new Properties();
            modLanguageData.put(lang, langPack);
        }
        if (langPackAdditions != null) {
            langPack.putAll(langPackAdditions);
        }
    }

    public static void reloadLanguageTable()
    {
//        // reload language table by forcing lang to null and reloading the properties file
//        String lang = StringTranslate.getInstance().func_74811_c();
//        StringTranslate.getInstance().func_74810_a(lang, true);
    }


    public void addNameForObject(final Object objectToName, final String lang, final String name)
    {
        String objectName;
        if (objectToName instanceof Item) {
            objectName=((Item)objectToName).getUnlocalizedName();
        } else if (objectToName instanceof Block) {
            objectName=((Block)objectToName).getUnlocalizedName();
        } else if (objectToName instanceof ItemStack) {
            objectName=((ItemStack)objectToName).getItem().getUnlocalizedName((ItemStack)objectToName);
        } else {
            throw new IllegalArgumentException(String.format("Illegal object for naming %s",objectToName));
        }
        objectName+=".name";
        addStringLocalization(objectName, lang, name);
    }

    public static void addName(final Object objectToName, final String name)
    {
        instance().addNameForObject(objectToName, "en_US", name);
    }

    public void loadLanguageTable(final Map field_135032_a, final String lang)
    {
        final Properties usPack=modLanguageData.get("en_US");
        if (usPack!=null) {
            field_135032_a.putAll(usPack);
        }
        final Properties langPack=modLanguageData.get(lang);
        if (langPack==null) {
            return;
        }
        field_135032_a.putAll(langPack);
    }

    public void loadLocalization(final String localizationFile, final String lang, final boolean isXML)
    {
        final URL urlResource = this.getClass().getResource(localizationFile);
        if (urlResource != null)
        {
            loadLocalization(urlResource, lang, isXML);
        }
        else
        {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            if (activeModContainer!=null)
            {
                FMLLog.log(activeModContainer.getModId(), Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
            else
            {
                FMLLog.log(Level.SEVERE, "The language resource %s cannot be located on the classpath. This is a programming error.", localizationFile);
            }
        }
    }

    public void loadLocalization(final URL localizationFile, final String lang, final boolean isXML)
    {
        InputStream langStream = null;
        final Properties langPack = new Properties();

        try    {
            langStream = localizationFile.openStream();

            if (isXML) {
                langPack.loadFromXML(langStream);
            }
            else {
                langPack.load(new InputStreamReader(langStream,Charsets.UTF_8));
            }

            addStringLocalization(langPack, lang);
        }
        catch (final IOException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to load localization from file %s", localizationFile);
        }
        finally    {
            try    {
                if (langStream != null)    {
                    langStream.close();
                }
            }
            catch (final IOException ex) {
                // HUSH
            }
        }
    }
}
