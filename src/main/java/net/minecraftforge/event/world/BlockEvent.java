package net.minecraftforge.event.world;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

import java.util.ArrayList;

public class BlockEvent extends Event {
    public final int x;
    public final int y;
    public final int z;
    public final World world;
    public final Block block;
    public final int blockMetadata;
    public BlockEvent(final int x, final int y, final int z, final World world, final Block block, final int blockMetadata)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.block = block;
        this.blockMetadata = blockMetadata;
    }
    
    /**
     * Fired when a block is about to drop it's harvested items. The {@link #drops} array can be amended, as can the {@link #dropChance}.
     * <strong>Note well:</strong> the {@link #harvester} player field is null in a variety of scenarios. Code expecting null.
     * 
     * The {@link #dropChance} is used to determine which items in this array will actually drop, compared to a random number. If you wish, you 
     * can pre-filter yourself, and set {@link #dropChance} to 1.0f to always drop the contents of the {@link #drops} array.
     * 
     * {@link #isSilkTouching} is set if this is considered a silk touch harvesting operation, vs a normal harvesting operation. Act accordingly.
     * 
     * @author cpw
     */
    public static class HarvestDropsEvent extends BlockEvent {
        public final int fortuneLevel;
        public final ArrayList<ItemStack> drops;
        public final boolean isSilkTouching;
        public float dropChance; // Change to e.g. 1.0f, if you manipulate the list and want to guarantee it always drops
        public final EntityPlayer harvester; // May be null for non-player harvesting such as explosions or machines

        public HarvestDropsEvent(final int x, final int y, final int z, final World world, final Block block, final int blockMetadata, final int fortuneLevel, final float dropChance, final ArrayList<ItemStack> drops, final EntityPlayer harvester, final boolean isSilkTouching)
        {
            super(x, y, z, world, block, blockMetadata);
            this.fortuneLevel = fortuneLevel;
            this.dropChance = dropChance;
            this.drops = drops;
            this.isSilkTouching = isSilkTouching;
            this.harvester = harvester;
        }
    }
    
    /**
     * Event that is fired when an Block is about to be broken by a player
     * Canceling this event will prevent the Block from being broken.
     */
    @Cancelable
    public static class BreakEvent extends BlockEvent 
    {
        /** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
        private final EntityPlayer player;
        private int exp;

        public BreakEvent(final int x, final int y, final int z, final World world, final Block block, final int blockMetadata, final EntityPlayer player)
        {
            super(x, y, z, world, block, blockMetadata);
            this.player = player;

            // Cauldron start - handle event on bukkit side
            final org.bukkit.event.block.BlockBreakEvent bukkitEvent = CraftEventFactory.callBlockBreakEvent(world, x, y, z, block, blockMetadata, player);
            if (bukkitEvent.isCancelled())
            {
                this.setCanceled(true);
            }
            else
            {
                this.exp = bukkitEvent.getExpToDrop();
            }
            // Cauldron end
        }

        public EntityPlayer getPlayer()
        {
            return player;
        }
        
        /**
         * Get the experience dropped by the block after the event has processed
         *
         * @return The experience to drop or 0 if the event was canceled
         */
        public int getExpToDrop()
        {
            return this.isCanceled() ? 0 : exp;
        }

        /**
         * Set the amount of experience dropped by the block after the event has processed
         *
         * @param exp 1 or higher to drop experience, else nothing will drop
         */
        public void setExpToDrop(final int exp)
        {
            this.exp = exp;
        }
    }
}
