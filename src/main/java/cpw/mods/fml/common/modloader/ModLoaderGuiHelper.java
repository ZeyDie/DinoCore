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

package cpw.mods.fml.common.modloader;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import java.util.Set;

public class ModLoaderGuiHelper implements IGuiHandler
{

    private BaseModProxy mod;
    private Set<Integer> ids;
    private Container container;
    private int currentID;

    ModLoaderGuiHelper(final BaseModProxy mod)
    {
        this.mod = mod;
        this.ids = Sets.newHashSet();
    }

    @Override
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z)
    {
        return container;
    }

    @Override
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z)
    {
        return ModLoaderHelper.getClientSideGui(mod, player, ID, x, y, z);
    }

    public void injectContainerAndID(final Container container, final int ID)
    {
        this.container = container;
        this.currentID = ID;
    }

    public Object getMod()
    {
        return mod;
    }

    public void associateId(final int additionalID)
    {
        this.ids.add(additionalID);
    }

}
