package net.minecraftforge.cauldron;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import java.io.*;

public class VersionInfo {
    public static final VersionInfo INSTANCE = new VersionInfo();
    public final JsonRootNode versionData;

    public VersionInfo()
    {
        final InputStream installProfile = getClass().getResourceAsStream("/install_profile.json");
        final JdomParser parser = new JdomParser();

        try
        {
            versionData = parser.parse(new InputStreamReader(installProfile, Charsets.UTF_8));
        }
        catch (final Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getProfileName()
    {
        return INSTANCE.versionData.getStringValue("install","profileName");
    }

    public static String getVersionTarget()
    {
        return INSTANCE.versionData.getStringValue("install","target");
    }
    public static File getLibraryPath(final File root)
    {
        final String path = INSTANCE.versionData.getStringValue("install","path");
        final String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        final Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for (final String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        final String fileName = split[1]+"-"+split[2]+".jar";
        return new File(dest,fileName);
    }

    public static String getVersion()
    {
        return INSTANCE.versionData.getStringValue("install","version");
    }

    public static String getWelcomeMessage()
    {
        return INSTANCE.versionData.getStringValue("install","welcome");
    }

    public static String getLogoFileName()
    {
        return INSTANCE.versionData.getStringValue("install","logo");
    }

    public static JsonNode getVersionInfo()
    {
        return INSTANCE.versionData.getNode("versionInfo");
    }

    public static File getMinecraftFile(final File path)
    {
        return new File(new File(path, getMinecraftVersion()),getMinecraftVersion()+".jar");
    }
    public static String getContainedFile()
    {
        return INSTANCE.versionData.getStringValue("install","filePath");
    }
    public static void extractFile(final File path) throws IOException
    {
        INSTANCE.doFileExtract(path);
    }

    private void doFileExtract(final File path) throws IOException
    {
        final InputStream inputStream = getClass().getResourceAsStream("/"+getContainedFile());
        final OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        System.out.println("doFileExtract path = " + path.getAbsolutePath() + ", inputStream = " + inputStream + ", outputSupplier = " + outputSupplier);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public static String getMinecraftVersion()
    {
        return INSTANCE.versionData.getStringValue("install","minecraft");
    }
}