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

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import net.minecraft.entity.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.SpawnListEntry;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
// Cauldron start

// Cauldron end

public class EntityRegistry
{
    public class EntityRegistration
    {
        private Class<? extends Entity> entityClass;
        private ModContainer container;
        private String entityName;
        private int modId;
        private int trackingRange;
        private int updateFrequency;
        private boolean sendsVelocityUpdates;
        private Function<EntitySpawnPacket, Entity> customSpawnCallback;
        private boolean usesVanillaSpawning;
        public EntityRegistration(final ModContainer mc, final Class<? extends Entity> entityClass, final String entityName, final int id, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates)
        {
            this.container = mc;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
        }
        public Class<? extends Entity> getEntityClass()
        {
            return entityClass;
        }
        public ModContainer getContainer()
        {
            return container;
        }
        public String getEntityName()
        {
            return entityName;
        }
        public int getModEntityId()
        {
            return modId;
        }
        public int getTrackingRange()
        {
            return trackingRange;
        }
        public int getUpdateFrequency()
        {
            return updateFrequency;
        }
        public boolean sendsVelocityUpdates()
        {
            return sendsVelocityUpdates;
        }

        public boolean usesVanillaSpawning()
        {
            return usesVanillaSpawning;
        }
        public boolean hasCustomSpawning()
        {
            return customSpawnCallback != null;
        }
        public Entity doCustomSpawning(final EntitySpawnPacket packet) throws Exception
        {
            return customSpawnCallback.apply(packet);
        }
        public void setCustomSpawning(final Function<EntitySpawnPacket, Entity> callable, final boolean usesVanillaSpawning)
        {
            this.customSpawnCallback = callable;
            this.usesVanillaSpawning = usesVanillaSpawning;
        }
    }

    private static final EntityRegistry INSTANCE = new EntityRegistry();

    private BitSet availableIndicies;
    private ListMultimap<ModContainer, EntityRegistration> entityRegistrations = ArrayListMultimap.create();
    private Map<String,ModContainer> entityNames = Maps.newHashMap();
    private BiMap<Class<? extends Entity>, EntityRegistration> entityClassRegistrations = HashBiMap.create();
    public static Map<Class <? extends Entity>, String> entityTypeMap = Maps.newHashMap(); // Cauldron - used by CraftCustomEntity
    public static Map<String, Class <? extends Entity>> entityClassMap = Maps.newHashMap(); // Cauldron - used by CraftWorld
    public static EntityRegistry instance()
    {
        return INSTANCE;
    }

    private EntityRegistry()
    {
        availableIndicies = new BitSet(256);
        availableIndicies.set(1,255);
        for (final Object id : EntityList.IDtoClassMapping.keySet())
        {
            availableIndicies.clear((Integer)id);
        }
    }

    /**
     * Register the mod entity type with FML

     * @param entityClass The entity class
     * @param entityName A unique name for the entity
     * @param id A mod specific ID for the entity
     * @param mod The mod
     * @param trackingRange The range at which MC will send tracking updates
     * @param updateFrequency The frequency of tracking updates
     * @param sendsVelocityUpdates Whether to send velocity information packets as well
     */
    public static void registerModEntity(final Class<? extends Entity> entityClass, final String entityName, final int id, final Object mod, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates)
    {
        instance().doModEntityRegistration(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
        registerBukkitType(entityClass, entityName); // Cauldron - register EntityType for Bukkit
    }

    private void doModEntityRegistration(final Class<? extends Entity> entityClass, final String entityName, final int id, final Object mod, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates)
    {
        final ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        final EntityRegistration er = new EntityRegistration(mc, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        try
        {
            entityClassRegistrations.put(entityClass, er);
            entityNames.put(entityName, mc);
            if (!EntityList.classToStringMapping.containsKey(entityClass))
            {
                final String entityModName = String.format("%s.%s", mc.getModId(), entityName);
                EntityList.classToStringMapping.put(entityClass, entityModName);
                EntityList.stringToClassMapping.put(entityModName, entityClass);
                FMLLog.finest("Automatically registered mod %s entity %s as %s", mc.getModId(), entityName, entityModName);
            }
            else
            {
                FMLLog.fine("Skipping automatic mod %s entity registration for already registered class %s", mc.getModId(), entityClass.getName());
            }
        }
        catch (final IllegalArgumentException e)
        {
            FMLLog.log(Level.WARNING, e, "The mod %s tried to register the entity (name,class) (%s,%s) one or both of which are already registered", mc.getModId(), entityName, entityClass.getName());
            return;
        }
        entityRegistrations.put(mc, er);
    }

    public static void registerGlobalEntityID(final Class <? extends Entity > entityClass, final String entityName, int id)
    {
        if (EntityList.classToStringMapping.containsKey(entityClass))
        {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null)
            {
                modId = activeModContainer.getModId();
            }
            else
            {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.");
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        int id1 = instance().validateAndClaimId(id);
        EntityList.addMapping(entityClass, entityName, id1);
        registerBukkitType(entityClass, entityName); // Cauldron - register EntityType for Bukkit
    }

    private int validateAndClaimId(final int id)
    {
        // workaround for broken ML
        int realId = id;
        if (id < Byte.MIN_VALUE)
        {
            FMLLog.warning("Compensating for modloader out of range compensation by mod : entityId %d for mod %s is now %d", id, Loader.instance().activeModContainer().getModId(), realId);
            realId += 3000;
        }

        if (realId < 0)
        {
            realId += Byte.MAX_VALUE;
        }
        try
        {
            UnsignedBytes.checkedCast(realId);
        }
        catch (final IllegalArgumentException e)
        {
            FMLLog.log(Level.SEVERE, "The entity ID %d for mod %s is not an unsigned byte and may not work", id, Loader.instance().activeModContainer().getModId());
        }

        if (!availableIndicies.get(realId))
        {
            FMLLog.severe("The mod %s has attempted to register an entity ID %d which is already reserved. This could cause severe problems", Loader.instance().activeModContainer().getModId(), id);
        }
        availableIndicies.clear(realId);
        return realId;
    }

    public static void registerGlobalEntityID(final Class <? extends Entity > entityClass, final String entityName, final int id, final int backgroundEggColour, final int foregroundEggColour)
    {
        if (EntityList.classToStringMapping.containsKey(entityClass))
        {
            final ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null)
            {
                modId = activeModContainer.getModId();
            }
            else
            {
                FMLLog.severe("There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.");
            }
            FMLLog.warning("The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first", modId, entityClass);
            return;
        }
        instance().validateAndClaimId(id);
        EntityList.addMapping(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
        registerBukkitType(entityClass, entityName); // Cauldron - register EntityType for Bukkit
    }

    // Cauldron start
    private static void registerBukkitType(final Class <? extends Entity > entityClass, String entityName)
    {
        String entityName3 = entityName;
        final ModContainer activeModContainer = Loader.instance().activeModContainer();
        String modId = "unknown";
        // fixup bad entity names from mods
        if (entityName3.contains("."))
        {
            if ((entityName3.indexOf(".") + 1) < entityName3.length())
                entityName3 = entityName3.substring(entityName3.indexOf(".") + 1, entityName3.length());
        }
        entityName3.replace("entity", "");
        if (entityName3.startsWith("ent"))
            entityName3.replace("ent", "");
        String entityName2 = entityName3.replaceAll("[^A-Za-z0-9]", ""); // remove all non-digits/alphanumeric
        if (activeModContainer != null)
            modId = activeModContainer.getModId();
        String entityName1 = modId + "-" + entityName2;
        entityTypeMap.put(entityClass, entityName1);
        entityClassMap.put(entityName1, entityClass);
    }

    // used by CraftCustomEntity
    public static String getCustomEntityTypeName(final Class <? extends Entity > entityClass)
    {
        return entityTypeMap.get(entityClass);
    }
    // Cauldron end

    public static void addSpawn(final Class <? extends EntityLiving > entityClass, final int weightedProb, final int min, final int max, final EnumCreatureType typeOfCreature, final BiomeGenBase... biomes)
    {
        for (final BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked") final List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);

            for (final SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.entityClass == entityClass)
                {
                    entry.itemWeight = weightedProb;
                    entry.minGroupCount = min;
                    entry.maxGroupCount = max;
                    break;
                }
            }

            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    public static void addSpawn(final String entityName, final int weightedProb, final int min, final int max, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        final Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            addSpawn((Class <? extends EntityLiving >) entityClazz, weightedProb, min, max, spawnList, biomes);
        }
    }

    public static void removeSpawn(final Class <? extends EntityLiving > entityClass, final EnumCreatureType typeOfCreature, final BiomeGenBase... biomes)
    {
        for (final BiomeGenBase biome : biomes)
        {
            @SuppressWarnings("unchecked") final Iterator<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature).iterator();

            while (spawns.hasNext())
            {
                final SpawnListEntry entry = spawns.next();
                if (entry.entityClass == entityClass)
                {
                    spawns.remove();
                }
            }
        }
    }

    public static void removeSpawn(final String entityName, final EnumCreatureType spawnList, final BiomeGenBase... biomes)
    {
        final Class <? extends Entity > entityClazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(entityName);

        if (EntityLiving.class.isAssignableFrom(entityClazz))
        {
            removeSpawn((Class <? extends EntityLiving >) entityClazz, spawnList, biomes);
        }
    }

    public static int findGlobalUniqueEntityId()
    {
        final int res = instance().availableIndicies.nextSetBit(0);
        if (res < 0)
        {
            throw new RuntimeException("No more entity indicies left");
        }
        return res;
    }

    public EntityRegistration lookupModSpawn(final Class<? extends Entity> clazz, boolean keepLooking)
    {
        boolean keepLooking1 = keepLooking;
        Class<?> localClazz = clazz;

        do
        {
            final EntityRegistration er = entityClassRegistrations.get(localClazz);
            if (er != null)
            {
                return er;
            }
            localClazz = localClazz.getSuperclass();
            keepLooking1 = (!Object.class.equals(localClazz));
        }
        while (keepLooking1);

        return null;
    }

    public EntityRegistration lookupModSpawn(final ModContainer mc, final int modEntityId)
    {
        for (final EntityRegistration er : entityRegistrations.get(mc))
        {
            if (er.getModEntityId() == modEntityId)
            {
                return er;
            }
        }
        return null;
    }

    public boolean tryTrackingEntity(final EntityTracker entityTracker, final Entity entity)
    {

        final EntityRegistration er = lookupModSpawn(entity.getClass(), true);
        if (er != null)
        {
            entityTracker.addEntityToTracker(entity, er.getTrackingRange(), er.getUpdateFrequency(), er.sendsVelocityUpdates());
            return true;
        }
        return false;
    }

    /**
     *
     * DO NOT USE THIS METHOD
     *
     * @param entityClass
     * @param entityTypeId
     * @param updateRange
     * @param updateInterval
     * @param sendVelocityInfo
     */
    @Deprecated
    public static EntityRegistration registerModLoaderEntity(final Object mod, final Class<? extends Entity> entityClass, final int entityTypeId, final int updateRange, final int updateInterval,
                                                             final boolean sendVelocityInfo)
    {
        final String entityName = (String) EntityList.classToStringMapping.get(entityClass);
        if (entityName == null)
        {
            throw new IllegalArgumentException(String.format("The ModLoader mod %s has tried to register an entity tracker for a non-existent entity type %s", Loader.instance().activeModContainer().getModId(), entityClass.getCanonicalName()));
        }
        instance().doModEntityRegistration(entityClass, entityName, entityTypeId, mod, updateRange, updateInterval, sendVelocityInfo);
        return instance().entityClassRegistrations.get(entityClass);
    }

}
