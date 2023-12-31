package org.spigotmc;

import org.bukkit.craftbukkit.v1_6_R3.util.LongHash;

public class FlatMap<V>
{

    private static final int FLAT_LOOKUP_SIZE = 512;
    private final Object[][] flatLookup = new Object[ FLAT_LOOKUP_SIZE * 2 ][ FLAT_LOOKUP_SIZE * 2 ];

    public void put(final long msw, final long lsw, final V value)
    {
        final long acx = Math.abs( msw );
        final long acz = Math.abs( lsw );
        if ( acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE )
        {
            flatLookup[(int) ( msw + FLAT_LOOKUP_SIZE )][(int) ( lsw + FLAT_LOOKUP_SIZE )] = value;
        }
    }

    public void put(final long key, final V value)
    {
        put( LongHash.msw( key ), LongHash.lsw( key ), value );

    }

    public void remove(final long key)
    {
        put( key, null );
    }

    public void remove(final long msw, final long lsw)
    {
        put( msw, lsw, null );
    }

    public boolean contains(final long msw, final long lsw)
    {
        return get( msw, lsw ) != null;
    }

    public boolean contains(final long key)
    {
        return get( key ) != null;
    }

    public V get(final long msw, final long lsw)
    {
        final long acx = Math.abs( msw );
        final long acz = Math.abs( lsw );
        if ( acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE )
        {
            return (V) flatLookup[(int) ( msw + FLAT_LOOKUP_SIZE )][(int) ( lsw + FLAT_LOOKUP_SIZE )];
        } else
        {
            return null;
        }
    }

    public V get(final long key)
    {
        return get( LongHash.msw( key ), LongHash.lsw( key ) );
    }
}
