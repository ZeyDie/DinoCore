package net.minecraftforge.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

//Preliminary, simple Fake Player class 
public class FakePlayer extends EntityPlayerMP
{
    public FakePlayer(final World world, final String name)
    {
        super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, name, new ItemInWorldManager(world));
    }

    public void sendChatToPlayer(final String s){}
    public boolean canCommandSenderUseCommand(final int i, final String s){ return false; }
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0,0,0);
    }

    @Override
    public void sendChatToPlayer(final ChatMessageComponent chatmessagecomponent){}
    @Override
    public void addStat(final StatBase par1StatBase, final int par2){}
    @Override
    public void openGui(final Object mod, final int modGuiId, final World world, final int x, final int y, final int z){}
    @Override 
    public boolean isEntityInvulnerable(){ return true; }
    @Override
    public boolean canAttackPlayer(final EntityPlayer player){ return false; }
    @Override
    public void onDeath(final DamageSource source){ return; }
    @Override
    public void onUpdate(){ return; }
    @Override
    public void travelToDimension(final int dim){ return; }
    @Override
    public void updateClientInfo(final Packet204ClientInfo pkt){ return; }
}
