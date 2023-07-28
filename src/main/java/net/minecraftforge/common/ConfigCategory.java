package net.minecraftforge.common;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

import static net.minecraftforge.common.Configuration.NEW_LINE;
import static net.minecraftforge.common.Configuration.allowedProperties;

public class ConfigCategory implements Map<String, Property>
{
    private String name;
    private String comment;
    private ArrayList<ConfigCategory> children = new ArrayList<ConfigCategory>();
    private Map<String, Property> properties = new TreeMap<String, Property>();
    public final ConfigCategory parent;
    private boolean changed = false;

    public ConfigCategory(final String name)
    {
        this(name, null);
    }

    public ConfigCategory(final String name, final ConfigCategory parent)
    {
        this.name = name;
        this.parent = parent;
        if (parent != null)
        {
            parent.children.add(this);
        }
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof ConfigCategory)
        {
            final ConfigCategory cat = (ConfigCategory)obj;
            return name.equals(cat.name) && children.equals(cat.children);  
        }
        
        return false;
    }

    public String getQualifiedName()
    {
        return getQualifiedName(name, parent);
    }

    public static String getQualifiedName(final String name, final ConfigCategory parent)
    {
        return (parent == null ? name : parent.getQualifiedName() + Configuration.CATEGORY_SPLITTER + name);
    }

    public ConfigCategory getFirstParent()
    {
        return (parent == null ? this : parent.getFirstParent());
    }

    public boolean isChild()
    {
        return parent != null;
    }

    public Map<String, Property> getValues()
    {
        return ImmutableMap.copyOf(properties);
    }

    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    public boolean containsKey(final String key)
    {
        return properties.containsKey(key);
    }

    public Property get(final String key)
    {
        return properties.get(key);
    }

    private void write(final BufferedWriter out, final String... data) throws IOException
    {
        write(out, true, data);
    }

    private void write(final BufferedWriter out, final boolean new_line, final String... data) throws IOException
    {
        for (int x = 0; x < data.length; x++)
        {
            out.write(data[x]);
        }
        if (new_line) out.write(NEW_LINE);
    }

    public void write(final BufferedWriter out, final int indent) throws IOException
    {
        final String pad0 = getIndent(indent);
        final String pad1 = getIndent(indent + 1);
        final String pad2 = getIndent(indent + 2);

        write(out, pad0, "####################");
        write(out, pad0, "# ", name);

        if (comment != null)
        {
            write(out, pad0, "#===================");
            final Splitter splitter = Splitter.onPattern("\r?\n");

            for (final String line : splitter.split(comment))
            {
                write(out, pad0, "# ", line);
            }
        }

        write(out, pad0, "####################", NEW_LINE);

        if (!allowedProperties.matchesAllOf(name))
        {
            name = '"' + name + '"';
        }

        write(out, pad0, name, " {");

        final Property[] props = properties.values().toArray(new Property[0]);

        for (int x = 0; x < props.length; x++)
        {
            final Property prop = props[x];

            if (prop.comment != null)
            {
                if (x != 0)
                {
                    out.newLine();
                }

                final Splitter splitter = Splitter.onPattern("\r?\n");
                for (final String commentLine : splitter.split(prop.comment))
                {
                    write(out, pad1, "# ", commentLine);
                }
            }

            String propName = prop.getName();

            if (!allowedProperties.matchesAllOf(propName))
            {
                propName = '"' + propName + '"';
            }

            if (prop.isList())
            {
                final char type = prop.getType().getID();
                
                write(out, pad1, String.valueOf(type), ":", propName, " <");

                for (final String line : prop.getStringList())
                {
                    write(out, pad2, line);
                }

                write(out, pad1, " >");
            }
            else if (prop.getType() == null)
            {
                write(out, pad1, propName, "=", prop.getString());
            }
            else
            {
                final char type = prop.getType().getID();
                write(out, pad1, String.valueOf(type), ":", propName, "=", prop.getString());
            }
        }

        for (final ConfigCategory child : children)
        {
            child.write(out, indent + 1);
        }

        write(out, pad0, "}", NEW_LINE);
    }

    private String getIndent(final int indent)
    {
        final StringBuilder buf = new StringBuilder("");
        for (int x = 0; x < indent; x++)
        {
            buf.append("    ");
        }
        return buf.toString();
    }

    public boolean hasChanged()
    {
        if (changed) return true;
        for (final Property prop : properties.values())
        {
            if (prop.hasChanged()) return true;
        }
        return false;
    }

    void resetChangedState()
    {
        changed = false;
        for (final Property prop : properties.values())
        {
            prop.resetChangedState();
        }
    }


    //Map bouncer functions for compatibility with older mods, to be removed once all mods stop using it.
    @Override public int size(){ return properties.size(); }
    @Override public boolean isEmpty() { return properties.isEmpty(); }
    @Override public boolean containsKey(final Object key) { return properties.containsKey(key); }
    @Override public boolean containsValue(final Object value){ return properties.containsValue(value); }
    @Override public Property get(final Object key) { return properties.get(key); }
    @Override public Property put(final String key, final Property value)
    {
        changed = true;
        return properties.put(key, value);
    }
    @Override public Property remove(final Object key)
    {
        changed = true;
        return properties.remove(key);
    }
    @Override public void putAll(final Map<? extends String, ? extends Property> m)
    {
        changed = true;
        properties.putAll(m);
    }
    @Override public void clear()
    {
        changed = true;
        properties.clear();
    }
    @Override public Set<String> keySet() { return properties.keySet(); }
    @Override public Collection<Property> values() { return properties.values(); }

    @Override //Immutable copy, changes will NOT be reflected in this category
    public Set<java.util.Map.Entry<String, Property>> entrySet()
    {
        return ImmutableSet.copyOf(properties.entrySet());
    }

    public Set<ConfigCategory> getChildren(){ return ImmutableSet.copyOf(children); }
    
    public void removeChild(final ConfigCategory child)
    {
        if (children.contains(child))
        {
            children.remove(child);
            changed = true;
        }
    }
}