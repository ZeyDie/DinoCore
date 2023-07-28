package net.minecraftforge.event;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ForgeEventFactory
{
    public static boolean doPlayerHarvestCheck(final EntityPlayer player, final Block block, final boolean success)
    {
        final PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    public static float getBreakSpeed(final EntityPlayer player, final Block block, final int metadata, final float original)
    {
        final PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, block, metadata, original);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.newSpeed);
    }

    public static PlayerInteractEvent onPlayerInteract(final EntityPlayer player, final Action action, final int x, final int y, final int z, final int face)
    {
        final PlayerInteractEvent event = new PlayerInteractEvent(player, action, x, y, z, face);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onPlayerDestroyItem(final EntityPlayer player, final ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
    }

    public static Result canEntitySpawn(final EntityLiving entity, final World world, final float x, final float y, final float z)
    {
        final LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean doSpecialSpawn(final EntityLiving entity, final World world, final float x, final float y, final float z)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z));
    }

    public static Result canEntityDespawn(final EntityLiving entity)
    {
        final AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }
    
    public static List getPotentialSpawns(final WorldServer world, final EnumCreatureType type, final int x, final int y, final int z, final List oldList)
    {
        final WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, x, y, z, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.list;
    }
    
    public static int getMaxSpawnPackSize(final EntityLiving entity)
    {
        final LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.maxPackSize : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(final EntityPlayer player, final String username)
    {
        final PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.displayname;
    }

    public static float fireBlockHarvesting(final ArrayList<ItemStack> drops, final World world, final Block block, final int x, final int y, final int z, final int meta, final int fortune, final float dropChance, final boolean silkTouch, final EntityPlayer player)
    {
        final BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(x, y, z, world, block, meta, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.dropChance;
    }
    
    public static ItemTooltipEvent onItemTooltip(final ItemStack itemStack, final EntityPlayer entityPlayer, final List<String> toolTip, final boolean showAdvancedItemTooltips)
    {
        final ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, toolTip, showAdvancedItemTooltips);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(final EntityZombie zombie, final World world, final int x, final int y, final int z, final EntityLivingBase attacker, final double summonChance)
    {
        final SummonAidEvent summonEvent = new SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }
}
