/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Some reflection helper code.
 *
 * @author cpw
 *
 */
public class ObfuscationReflectionHelper
{
    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final int fieldIndex)
    {
        try
        {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        }
        catch (final UnableToAccessFieldException e)
        {
            FMLLog.log(Level.SEVERE, e, "There was a problem getting field index %d from %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }

    public static String[] remapFieldNames(final String className, final String... fieldNames)
    {
        final String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        final String[] mappedNames = new String[fieldNames.length];
        int i = 0;
        for (final String fName : fieldNames)
        {
            mappedNames[i++] = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fName, null);
        }
        return mappedNames;
    }
    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final String... fieldNames)
    {
        try
        {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, remapFieldNames(classToAccess.getName(),fieldNames));
        }
        catch (final UnableToFindFieldException e)
        {
            FMLLog.log(Level.SEVERE,e,"Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (final UnableToAccessFieldException e)
        {
            FMLLog.log(Level.SEVERE, e, "Unable to access any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final int fieldIndex, final E value)
    {
        setPrivateValue(classToAccess, instance, value, fieldIndex);
    }

    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final int fieldIndex)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        }
        catch (final UnableToAccessFieldException e)
        {
            FMLLog.log(Level.SEVERE, e, "There was a problem setting field index %d on type %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }

    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final String fieldName, final E value)
    {
        setPrivateValue(classToAccess, instance, value, fieldName);
    }

    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final String... fieldNames)
    {
        try
        {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, remapFieldNames(classToAccess.getName(), fieldNames));
        }
        catch (final UnableToFindFieldException e)
        {
            FMLLog.log(Level.SEVERE, e, "Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (final UnableToAccessFieldException e)
        {
            FMLLog.log(Level.SEVERE, e, "Unable to set any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
    }
}
