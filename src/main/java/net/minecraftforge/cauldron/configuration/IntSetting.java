package net.minecraftforge.cauldron.configuration;

public class IntSetting extends Setting<Integer>
{
    private Integer value;
    private ConfigBase config;
    
    public IntSetting(final ConfigBase config, final String path, final Integer def, final String description)
    {
        super(path, def, description);
        this.value = def;
        this.config = config;
    }

    @Override
    public Integer getValue()
    {
        return value;
    }

    @Override
    public void setValue(final String value)
    {
        this.value = org.apache.commons.lang.math.NumberUtils.toInt(value, def);
        config.set(path, this.value);
    }
}

