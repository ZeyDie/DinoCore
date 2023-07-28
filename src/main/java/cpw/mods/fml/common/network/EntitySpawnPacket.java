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

package cpw.mods.fml.common.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.util.MathHelper;

import java.io.*;
import java.util.List;
import java.util.logging.Level;

public class EntitySpawnPacket extends FMLPacket
{

    public int networkId;
    public int modEntityId;
    public int entityId;
    public double scaledX;
    public double scaledY;
    public double scaledZ;
    public float scaledYaw;
    public float scaledPitch;
    public float scaledHeadYaw;
    public List metadata;
    public int throwerId;
    public double speedScaledX;
    public double speedScaledY;
    public double speedScaledZ;
    public ByteArrayDataInput dataStream;
    public int rawX;
    public int rawY;
    public int rawZ;

    public EntitySpawnPacket()
    {
        super(Type.ENTITYSPAWN);
    }

    @Override
    public byte[] generatePacket(final Object... data)
    {
        final EntityRegistration er = (EntityRegistration) data[0];
        final Entity ent = (Entity) data[1];
        final NetworkModHandler handler = (NetworkModHandler) data[2];
        final ByteArrayDataOutput dat = ByteStreams.newDataOutput();

        dat.writeInt(handler.getNetworkId());
        dat.writeInt(er.getModEntityId());
        // entity id
        dat.writeInt(ent.entityId);

        // entity pos x,y,z
        dat.writeInt(MathHelper.floor_double(ent.posX * 32.0D));
        dat.writeInt(MathHelper.floor_double(ent.posY * 32.0D));
        dat.writeInt(MathHelper.floor_double(ent.posZ * 32.0D));

        // yaw, pitch
        dat.writeByte((byte) (ent.rotationYaw * 256.0F / 360.0F));
        dat.writeByte((byte) (ent.rotationPitch * 256.0F / 360.0F));

        // head yaw
        if (ent instanceof EntityLiving)
        {
            dat.writeByte((byte) (((EntityLiving)ent).rotationYawHead * 256.0F / 360.0F));
        }
        else
        {
            dat.writeByte(0);
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        try
        {
            ent.getDataWatcher().writeWatchableObjects(dos);
        }
        catch (final Throwable e) // MCPC+ - change IOException to Throwable for binary compatibility
        {
            // unpossible
        }

        dat.write(bos.toByteArray());

        if (ent instanceof IThrowableEntity)
        {
            final Entity owner = ((IThrowableEntity)ent).getThrower();
            dat.writeInt(owner == null ? ent.entityId : owner.entityId);
            final double maxVel = 3.9D;
            double mX = ent.motionX;
            double mY = ent.motionY;
            double mZ = ent.motionZ;
            if (mX < -maxVel) mX = -maxVel;
            if (mY < -maxVel) mY = -maxVel;
            if (mZ < -maxVel) mZ = -maxVel;
            if (mX >  maxVel) mX =  maxVel;
            if (mY >  maxVel) mY =  maxVel;
            if (mZ >  maxVel) mZ =  maxVel;
            dat.writeInt((int)(mX * 8000.0D));
            dat.writeInt((int)(mY * 8000.0D));
            dat.writeInt((int)(mZ * 8000.0D));
        }
        else
        {
            dat.writeInt(0);
        }
        if (ent instanceof IEntityAdditionalSpawnData)
        {
            ((IEntityAdditionalSpawnData)ent).writeSpawnData(dat);
        }

        return dat.toByteArray();
    }

    @Override
    public FMLPacket consumePacket(final byte[] data)
    {
        final ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        networkId = dat.readInt();
        modEntityId = dat.readInt();
        entityId = dat.readInt();
        rawX = dat.readInt();
        rawY = dat.readInt();
        rawZ = dat.readInt();
        scaledX = rawX / 32.0D;
        scaledY = rawY / 32.0D;
        scaledZ = rawZ / 32.0D;
        scaledYaw = dat.readByte() * 360.0F / 256.0F;
        scaledPitch = dat.readByte() * 360.0F / 256.0F;
        scaledHeadYaw = dat.readByte() * 360.0F / 256.0F;
        final ByteArrayInputStream bis = new ByteArrayInputStream(data, 27, data.length - 27);
        final DataInputStream dis = new DataInputStream(bis);
        try
        {
            metadata = DataWatcher.readWatchableObjects(dis);
        }
        catch (final IOException e)
        {
            // Nope
        }
        dat.skipBytes(data.length - bis.available() - 27);
        throwerId = dat.readInt();
        if (throwerId != 0)
        {
            speedScaledX = dat.readInt() / 8000.0D;
            speedScaledY = dat.readInt() / 8000.0D;
            speedScaledZ = dat.readInt() / 8000.0D;
        }

        this.dataStream = dat;
        return this;
    }

    @Override
    public void execute(final INetworkManager network, final FMLNetworkHandler handler, final NetHandler netHandler, final String userName)
    {
        final NetworkModHandler nmh = handler.findNetworkModHandler(networkId);
        final ModContainer mc = nmh.getContainer();

        final EntityRegistration registration = EntityRegistry.instance().lookupModSpawn(mc, modEntityId);
        if (registration == null || registration.getEntityClass() == null)
        {
            FMLLog.log(Level.WARNING, "Missing mod entity information for %s : %d", mc.getModId(), modEntityId);
            return;
        }


        final Entity entity = FMLCommonHandler.instance().spawnEntityIntoClientWorld(registration, this);
    }

}
