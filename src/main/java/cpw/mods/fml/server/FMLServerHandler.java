/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapDifference;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringTranslate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Handles primary communication from hooked code into the system
 *
 * The FML entry point is {@link #beginServerLoading(MinecraftServer)} called from
 * {@link net.minecraft.server.dedicated.DedicatedServer}
 *
 * Obfuscated code should focus on this class and other members of the "server"
 * (or "client") code
 *
 * The actual mod loading is handled at arms length by {@link Loader}
 *
 * It is expected that a similar class will exist for each target environment:
 * Bukkit and Client side.
 *
 * It should not be directly modified.
 *
 * @author cpw
 *
 */
public class FMLServerHandler implements IFMLSidedHandler
{
    /**
     * The singleton
     */
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();

    /**
     * A reference to the server itself
     */
    private MinecraftServer server;

    private FMLServerHandler()
    {
        FMLCommonHandler.instance().beginLoading(this);
    }
    /**
     * Called to start the whole game off from
     * {@link MinecraftServer#startServer}
     *
     * @param minecraftServer
     */
    public void beginServerLoading(final MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        Loader.instance().loadMods();
    }

    /**
     * Called a bit later on during server initialization to finish loading mods
     */
    public void finishServerLoading()
    {
        Loader.instance().initializeMods();
        LanguageRegistry.reloadLanguageTable();
        GameData.initializeServerGate(1);
    }

    @Override
    public void haltGame(final String message, final Throwable exception)
    {
        throw new RuntimeException(message, exception);
    }

    /**
     * Get the server instance
     */
    public MinecraftServer getServer()
    {
        return server;
    }

    /**
     * @return the instance
     */
    public static FMLServerHandler instance()
    {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getAdditionalBrandingInformation()
     */
    @Override
    public List<String> getAdditionalBrandingInformation()
    {
        return ImmutableList.<String>of();
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.IFMLSidedHandler#getSide()
     */
    @Override
    public Side getSide()
    {
        return Side.SERVER;
    }

    @Override
    public void showGuiScreen(final Object clientGuiElement)
    {

    }

    @Override
    public Entity spawnEntityIntoClientWorld(final EntityRegistration er, final EntitySpawnPacket packet)
    {
        // NOOP
        return null;
    }

    @Override
    public void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket)
    {
        // NOOP
    }
    @Override
    public void sendPacket(final Packet packet)
    {
        throw new RuntimeException("You cannot send a bare packet without a target on the server!");
    }
    @Override
    public void displayMissingMods(final ModMissingPacket modMissingPacket)
    {
        // NOOP on server
    }
    @Override
    public void handleTinyPacket(final NetHandler handler, final Packet131MapData mapData)
    {
        // NOOP on server
    }
    @Override
    public void setClientCompatibilityLevel(final byte compatibilityLevel)
    {
        // NOOP on server
    }
    @Override
    public byte getClientCompatibilityLevel()
    {
        return 0;
    }

    @Override
    public boolean shouldServerShouldBeKilledQuietly()
    {
        return false;
    }
    @Override
    public void disconnectIDMismatch(final MapDifference<Integer, ItemData> s, final NetHandler handler, final INetworkManager mgr)
    {

    }
    @Override
    public void addModAsResource(final ModContainer container)
    {
        final File source = container.getSource();
        try
        {
            if (source.isDirectory())
            {
                searchDirForENUSLanguage(source,"");
            }
            else
            {
                searchZipForENUSLanguage(source);
            }
        }
        catch (final IOException ioe)
        {

        }
    }
    private static final Pattern assetENUSLang = Pattern.compile("assets/(.*)/lang/en_US.lang");
    private void searchZipForENUSLanguage(final File source) throws IOException
    {
        final ZipFile zf = new ZipFile(source);
        for (final ZipEntry ze : Collections.list(zf.entries()))
        {
            final Matcher matcher = assetENUSLang.matcher(ze.getName());
            if (matcher.matches())
            {
                FMLLog.fine("Injecting found translation data in zip file %s at %s into language system", source.getName(), ze.getName());
                StringTranslate.inject(zf.getInputStream(ze));
            }
        }
        zf.close();
    }
    private void searchDirForENUSLanguage(final File source, final String path) throws IOException
    {
        for (final File file : source.listFiles())
        {
            final String currPath = path+file.getName();
            if (file.isDirectory())
            {
                searchDirForENUSLanguage(file, currPath+'/');
            }
            final Matcher matcher = assetENUSLang.matcher(currPath);
            if (matcher.matches())
            {
                FMLLog.fine("Injecting found translation data at %s into language system", currPath);
                StringTranslate.inject(new FileInputStream(file));
            }
        }
    }
    @Override
    public void updateResourcePackList()
    {

    }
    @Override
    public String getCurrentLanguage()
    {
        return "en_US";
    }

    @Override
    public void serverStopped()
    {
        // NOOP
    }
}
