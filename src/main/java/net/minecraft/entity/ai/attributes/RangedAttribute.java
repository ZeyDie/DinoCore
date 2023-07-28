package net.minecraft.entity.ai.attributes;

public class RangedAttribute extends BaseAttribute
{
    private final double minimumValue;
    private final double maximumValue;
    private String field_111119_c;

    public RangedAttribute(final String par1Str, final double par2, final double par4, final double par6)
    {
        super(par1Str, par2);
        this.minimumValue = par4;
        this.maximumValue = par6;

        if (par4 > par6)
        {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        else if (par2 < par4)
        {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        else if (par2 > par6)
        {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public RangedAttribute func_111117_a(final String par1Str)
    {
        this.field_111119_c = par1Str;
        return this;
    }

    public String func_111116_f()
    {
        return this.field_111119_c;
    }

    public double clampValue(double par1)
    {
        double par11 = par1;
        if (par11 < this.minimumValue)
        {
            par11 = this.minimumValue;
        }

        if (par11 > this.maximumValue)
        {
            par11 = this.maximumValue;
        }

        return par11;
    }
}
