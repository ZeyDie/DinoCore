/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import java.util.ArrayList;

public class Property
{
    public enum Type
    {
        STRING,
        INTEGER,
        BOOLEAN,
        DOUBLE;

        private static Type[] values = {STRING, INTEGER, BOOLEAN, DOUBLE};

        public static Type tryParse(final char id)
        {
            for (int x = 0; x < values.length; x++)
            {
                if (values[x].getID() == id)
                {
                    return values[x];
                }
            }

            return STRING;
        }

        public char getID()
        {
            return name().charAt(0);
        }
    }

    private String name;
    private String value;
    public String comment;
    private String[] values;

    private final boolean wasRead;
    private final boolean isList;
    private final Type type;
    private boolean changed = false;

    public Property()
    {
        wasRead = false;
        type    = null;
        isList  = false;
    }

    public Property(final String name, final String value, final Type type)
    {
        this(name, value, type, false);
    }

    Property(final String name, final String value, final Type type, final boolean read)
    {
        setName(name);
        this.value = value;
        this.type  = type;
        wasRead    = read;
        isList     = false;
    }

    public Property(final String name, final String[] values, final Type type)
    {
        this(name, values, type, false);
    }

    Property(final String name, final String[] values, final Type type, final boolean read)
    {
        setName(name);
        this.type   = type;
        this.values = values;
        wasRead     = read;
        isList      = true;
    }

    /**
     * Returns the value in this property as it's raw string.
     * 
     * @return current value
     */
    public String getString()
    {
        return value;
    }

    /**
     * Returns the value in this property as an integer,
     * if the value is not a valid integer, it will return -1.
     * 
     * @return The value
     */
    public int getInt()
    {
        return getInt(-1);
    }

    /**
     * Returns the value in this property as an integer,
     * if the value is not a valid integer, it will return the
     * provided default.
     * 
     * @param _default The default to provide if the current value is not a valid integer
     * @return The value
     */
    public int getInt(final int _default)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (final NumberFormatException e)
        {
            return _default;
        }
    }
    
    /**
     * Checks if the current value stored in this property can be converted to an integer.
     * @return True if the type of the Property is an Integer
     */
    public boolean isIntValue()
    {
        try
        {
            Integer.parseInt(value);
            return true;
        }
        catch (final NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Returns the value in this property as a boolean,
     * if the value is not a valid boolean, it will return the
     * provided default.
     * 
     * @param _default The default to provide
     * @return The value as a boolean, or the default
     */
    public boolean getBoolean(final boolean _default)
    {
        if (isBooleanValue())
        {
            return Boolean.parseBoolean(value);
        }
        else
        {
            return _default;
        }
    }

    /**
     * Checks if the current value held by this property is a valid boolean value.
     * @return True if it is a boolean value
     */
    public boolean isBooleanValue()
    {
        return ("true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase()));
    }

    /**
     * Checks if the current value held by this property is a valid double value.
     * @return True if the value can be converted to an double
     */
    public boolean isDoubleValue()
    {
        try
        {
            Double.parseDouble(value);
            return true;
        }
        catch (final NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Returns the value in this property as a double,
     * if the value is not a valid double, it will return the
     * provided default.
     * 
     * @param _default The default to provide if the current value is not a valid double
     * @return The value
     */
    public double getDouble(final double _default)
    {
        try
        {
            return Double.parseDouble(value);
        }
        catch (final NumberFormatException e)
        {
            return _default;
        }
    }

    public String[] getStringList()
    {
        return values;
    }

    /**
     * Returns the integer value of all values that can
     * be parsed in the list.
     * 
     * @return Array of length 0 if none of the values could be parsed.
     */
    public int[] getIntList()
    {
        final ArrayList<Integer> nums = new ArrayList<Integer>();
        
        for (final String value : values)
        {
            try
            {
                nums.add(Integer.parseInt(value));
            }
            catch (final NumberFormatException e){}
        }

        final int[] primitives = new int[nums.size()];

        for (int i = 0; i < nums.size(); i++)
        {
            primitives[i] = nums.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of the current values stored in this property can be converted to an integer.
     * @return True if the type of the Property is an Integer List
     */
    public boolean isIntList()
    {
        for (final String value : values)
        {
            try
            {
                Integer.parseInt(value);
            }
            catch (final NumberFormatException e)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the boolean value of all values that can
     * be parsed in the list.
     * 
     * @return Array of length 0 if none of the values could be parsed.
     */
    public boolean[] getBooleanList()
    {
        final ArrayList<Boolean> tmp = new ArrayList<Boolean>();
        for (final String value : values)
        {
            try
            {
                tmp.add(Boolean.parseBoolean(value));
            }
            catch (final NumberFormatException e){}
        }

        final boolean[] primitives = new boolean[tmp.size()];

        for (int i = 0; i < tmp.size(); i++)
        {
            primitives[i] = tmp.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of current values stored in this property can be converted to a boolean.
     * @return True if it is a boolean value
     */
    public boolean isBooleanList()
    {
        for (final String value : values)
        {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the double value of all values that can
     * be parsed in the list.
     * 
     * @return Array of length 0 if none of the values could be parsed.
     */
    public double[] getDoubleList()
    {
        final ArrayList<Double> tmp = new ArrayList<Double>();
        for (final String value : values)
        {
            try
            {
                tmp.add(Double.parseDouble(value));
            }
            catch (final NumberFormatException e) {}
        }

        final double[] primitives = new double[tmp.size()];

        for (int i = 0; i < tmp.size(); i++)
        {
            primitives[i] = tmp.get(i);
        }

        return primitives;
    }

    /**
     * Checks if all of the current values stored in this property can be converted to a double.
     * @return True if the type of the Property is a double List
     */
    public boolean isDoubleList()
    {
        for (final String value : values)
        {
            try
            {
                Double.parseDouble(value);
            }
            catch (final NumberFormatException e)
            {
                return false;
            }
        }

        return true;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Determines if this config value was just created, or if it was read from the config file.
     * This is useful for mods who auto-assign there blocks to determine if the ID returned is 
     * a configured one, or a automatically generated one.
     * 
     * @return True if this property was loaded from the config file with a value
     */
    public boolean wasRead()
    {
        return wasRead;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isList()
    {
        return isList;
    }

    public boolean hasChanged(){ return changed; }
    void resetChangedState(){ changed = false; }

    public void set(final String value)
    {
        this.value = value;
        changed = true;
    }

    public void set(final String[] values)
    {
        this.values = values;
        changed = true;
    }

    public void set(final int     value){ set(Integer.toString(value)); }
    public void set(final boolean value){ set(Boolean.toString(value)); }
    public void set(final double  value){ set(Double.toString(value));  }
}
