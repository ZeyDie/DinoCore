package net.minecraftforge.cauldron.configuration;

public class CauldronWorldConfig extends WorldConfig
{
    public boolean entityDespawnImmediate = true;

    public CauldronWorldConfig(final String worldName, final ConfigBase configFile)
    {
        super(worldName, configFile);
        init();
    }

    public void init()
    {
        entityDespawnImmediate = getBoolean( "entity-despawn-immediate", true);
        this.save();
    }
}
