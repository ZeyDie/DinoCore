package org.bukkit.util.io;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * This class is designed to be used in conjunction with the {@link
 * ConfigurationSerializable} API. It translates objects to an internal
 * implementation for later deserialization using {@link
 * BukkitObjectInputStream}.
 * <p>
 * Behavior of implementations extending this class is not guaranteed across
 * future versions.
 */
public class BukkitObjectOutputStream extends ObjectOutputStream {

    /**
     * Constructor provided to mirror super functionality.
     *
     * @throws IOException
     * @throws SecurityException
     * @see ObjectOutputStream#ObjectOutputStream()
     */
    protected BukkitObjectOutputStream() throws IOException, SecurityException {
        super();
        super.enableReplaceObject(true);
    }

    /**
     * Object output stream decoration constructor.
     *
     * @param out
     * @throws IOException
     * @see ObjectOutputStream#ObjectOutputStream(OutputStream)
     */
    public BukkitObjectOutputStream(final OutputStream out) throws IOException {
        super(out);
        super.enableReplaceObject(true);
    }

    @Override
    protected Object replaceObject(Object obj) throws IOException {
        Object obj1 = obj;
        if (!(obj1 instanceof Serializable) && (obj1 instanceof ConfigurationSerializable)) {
            obj1 = Wrapper.newWrapper((ConfigurationSerializable) obj1);
        }

        return super.replaceObject(obj1);
    }
}
