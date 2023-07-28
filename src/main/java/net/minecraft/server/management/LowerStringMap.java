package net.minecraft.server.management;

import java.util.*;

public class LowerStringMap implements Map
{
    private final Map internalMap = new LinkedHashMap();

    public int size()
    {
        return this.internalMap.size();
    }

    public boolean isEmpty()
    {
        return this.internalMap.isEmpty();
    }

    public boolean containsKey(final Object par1Obj)
    {
        return this.internalMap.containsKey(par1Obj.toString().toLowerCase());
    }

    public boolean containsValue(final Object par1Obj)
    {
        return this.internalMap.containsKey(par1Obj);
    }

    public Object get(final Object par1Obj)
    {
        return this.internalMap.get(par1Obj.toString().toLowerCase());
    }

    /**
     * a map already defines a general put
     */
    public Object putLower(final String par1Str, final Object par2Obj)
    {
        return this.internalMap.put(par1Str.toLowerCase(), par2Obj);
    }

    public Object remove(final Object par1Obj)
    {
        return this.internalMap.remove(par1Obj.toString().toLowerCase());
    }

    public void putAll(final Map par1Map)
    {
        final Iterator iterator = par1Map.entrySet().iterator();

        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
            this.putLower((String)entry.getKey(), entry.getValue());
        }
    }

    public void clear()
    {
        this.internalMap.clear();
    }

    public Set keySet()
    {
        return this.internalMap.keySet();
    }

    public Collection values()
    {
        return this.internalMap.values();
    }

    public Set entrySet()
    {
        return this.internalMap.entrySet();
    }

    public Object put(final Object par1Obj, final Object par2Obj)
    {
        return this.putLower((String)par1Obj, par2Obj);
    }
}
