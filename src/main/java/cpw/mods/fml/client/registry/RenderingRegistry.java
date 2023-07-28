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

package cpw.mods.fml.client.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

import java.util.List;
import java.util.Map;

/**
 * @author cpw
 *
 */
public class RenderingRegistry
{
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();

    private int nextRenderId = 40;

    private Map<Integer, ISimpleBlockRenderingHandler> blockRenderers = Maps.newHashMap();

    private List<EntityRendererInfo> entityRenderers = Lists.newArrayList();

    /**
     * Add a new armour prefix to the RenderPlayer
     *
     * @param armor
     */
    public static int addNewArmourRendererPrefix(final String armor)
    {
        RenderBiped.bipedArmorFilenamePrefix = ObjectArrays.concat(RenderBiped.bipedArmorFilenamePrefix, armor);
        return RenderBiped.bipedArmorFilenamePrefix.length - 1;
    }

    /**
     * Register an entity rendering handler. This will, after mod initialization, be inserted into the main
     * render map for entities
     *
     * @param entityClass
     * @param renderer
     */
    public static void registerEntityRenderingHandler(final Class<? extends Entity> entityClass, final Render renderer)
    {
        instance().entityRenderers.add(new EntityRendererInfo(entityClass, renderer));
    }

    /**
     * Register a simple block rendering handler
     *
     * @param handler
     */
    public static void registerBlockHandler(final ISimpleBlockRenderingHandler handler)
    {
        instance().blockRenderers.put(handler.getRenderId(), handler);
    }

    /**
     * Register the simple block rendering handler
     * This version will not call getRenderId on the passed in handler, instead using the supplied ID, so you
     * can easily re-use the same rendering handler for multiple IDs
     *
     * @param renderId
     * @param handler
     */
    public static void registerBlockHandler(final int renderId, final ISimpleBlockRenderingHandler handler)
    {
        instance().blockRenderers.put(renderId, handler);
    }
    /**
     * Get the next available renderId from the block render ID list
     */
    public static int getNextAvailableRenderId()
    {
        return instance().nextRenderId++;
    }

    /**
     * Add a texture override for the given path and return the used index
     *
     * @param fileToOverride
     * @param fileToAdd
     */
    @Deprecated
    public static int addTextureOverride(final String fileToOverride, final String fileToAdd)
    {
        return -1;
    }

    /**
     * Add a texture override for the given path and index
     *
     * @param path
     * @param overlayPath
     * @param index
     */
    public static void addTextureOverride(final String path, final String overlayPath, final int index)
    {
//        TextureFXManager.instance().addNewTextureOverride(path, overlayPath, index);
    }

    /**
     * Get and reserve a unique texture index for the supplied path
     *
     * @param path
     */
    @Deprecated
    public static int getUniqueTextureIndex(final String path)
    {
        return -1;
    }

    @Deprecated public static RenderingRegistry instance()
    {
        return INSTANCE;
    }

    private static class EntityRendererInfo
    {
        public EntityRendererInfo(final Class<? extends Entity> target, final Render renderer)
        {
            this.target = target;
            this.renderer = renderer;
        }
        private Class<? extends Entity> target;
        private Render renderer;
    }

    public boolean renderWorldBlock(final RenderBlocks renderer, final IBlockAccess world, final int x, final int y, final int z, final Block block, final int modelId)
    {
        if (!blockRenderers.containsKey(modelId)) { return false; }
        final ISimpleBlockRenderingHandler bri = blockRenderers.get(modelId);
        return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    }

    public void renderInventoryBlock(final RenderBlocks renderer, final Block block, final int metadata, final int modelID)
    {
        if (!blockRenderers.containsKey(modelID)) { return; }
        final ISimpleBlockRenderingHandler bri = blockRenderers.get(modelID);
        bri.renderInventoryBlock(block, metadata, modelID, renderer);
    }

    public boolean renderItemAsFull3DBlock(final int modelId)
    {
        final ISimpleBlockRenderingHandler bri = blockRenderers.get(modelId);
        return bri != null && bri.shouldRender3DInInventory();
    }

    public void loadEntityRenderers(final Map<Class<? extends Entity>, Render> rendererMap)
    {
        for (final EntityRendererInfo info : entityRenderers)
        {
            rendererMap.put(info.target, info.renderer);
            info.renderer.setRenderManager(RenderManager.instance);
        }
    }
}
