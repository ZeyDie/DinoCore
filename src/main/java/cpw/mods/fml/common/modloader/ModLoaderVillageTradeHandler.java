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

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.src.TradeEntry;
import net.minecraft.village.MerchantRecipeList;

import java.util.List;
import java.util.Random;

public class ModLoaderVillageTradeHandler implements IVillageTradeHandler
{
    private List<TradeEntry> trades = Lists.newArrayList();

    @Override
    public void manipulateTradesForVillager(final EntityVillager villager, final MerchantRecipeList recipeList, final Random random)
    {
        for (final TradeEntry ent : trades)
        {
            if (ent.buying)
            {
                VillagerRegistry.addEmeraldBuyRecipe(villager, recipeList, random, Item.itemsList[ent.id], ent.chance, ent.min, ent.max);
            }
            else
            {
                VillagerRegistry.addEmeraldSellRecipe(villager, recipeList, random, Item.itemsList[ent.id], ent.chance, ent.min, ent.max);
            }
        }
    }

    public void addTrade(final TradeEntry entry)
    {
        trades.add(entry);
    }
}
