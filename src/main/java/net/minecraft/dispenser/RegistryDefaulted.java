package net.minecraft.dispenser;

public class RegistryDefaulted extends RegistrySimple
{
    /**
     * Default object for this registry, returned when an object is not found.
     */
    private final Object defaultObject;

    public RegistryDefaulted(final Object par1Obj)
    {
        this.defaultObject = par1Obj;
    }

    public Object getObject(final Object par1Obj)
    {
        final Object object1 = super.getObject(par1Obj);
        return object1 == null ? this.defaultObject : object1;
    }
}
