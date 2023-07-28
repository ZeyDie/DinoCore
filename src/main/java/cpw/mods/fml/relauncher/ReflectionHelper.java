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
package cpw.mods.fml.relauncher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * Some reflection helper code.
 *
 * @author cpw
 *
 */
public class ReflectionHelper
{
    public static class UnableToFindMethodException extends RuntimeException
    {
        private String[] methodNames;

        public UnableToFindMethodException(final String[] methodNames, final Exception failed)
        {
            super(failed);
            this.methodNames = methodNames;
        }

    }

    public static class UnableToFindClassException extends RuntimeException
    {
        private String[] classNames;

        public UnableToFindClassException(final String[] classNames, final Exception err)
        {
            super(err);
            this.classNames = classNames;
        }

    }

    public static class UnableToAccessFieldException extends RuntimeException
    {

        private String[] fieldNameList;

        public UnableToAccessFieldException(final String[] fieldNames, final Exception e)
        {
            super(e);
            this.fieldNameList = fieldNames;
        }
    }

    public static class UnableToFindFieldException extends RuntimeException
    {
        private String[] fieldNameList;
        public UnableToFindFieldException(final String[] fieldNameList, final Exception e)
        {
            super(e);
            this.fieldNameList = fieldNameList;
        }
    }

    public static Field findField(final Class<?> clazz, final String... fieldNames)
    {
        Exception failed = null;
        for (final String fieldName : fieldNames)
        {
            try
            {
                final Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (final Exception e)
            {
                failed = e;
            }
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(final Class <? super E > classToAccess, final E instance, final int fieldIndex)
    {
        try
        {
            final Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T) f.get(instance);
        }
        catch (final Exception e)
        {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, E> T getPrivateValue(final Class <? super E > classToAccess, final E instance, final String... fieldNames)
    {
        try
        {
            return (T) findField(classToAccess, fieldNames).get(instance);
        }
        catch (final Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static <T, E> void setPrivateValue(final Class <? super T > classToAccess, final T instance, final E value, final int fieldIndex)
    {
        try
        {
            final Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (final Exception e)
        {
            throw new UnableToAccessFieldException(new String[0] , e);
        }
    }

    public static <T, E> void setPrivateValue(final Class <? super T > classToAccess, final T instance, final E value, final String... fieldNames)
    {
        try
        {
            findField(classToAccess, fieldNames).set(instance, value);
        }
        catch (final Exception e)
        {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }

    public static Class<? super Object> getClass(final ClassLoader loader, final String... classNames)
    {
        Exception err = null;
        for (final String className : classNames)
        {
            try
            {
                return (Class<? super Object>) Class.forName(className, false, loader);
            }
            catch (final Exception e)
            {
                err = e;
            }
        }

        throw new UnableToFindClassException(classNames, err);
    }


    public static <E> Method findMethod(final Class<? super E> clazz, final E instance, final String[] methodNames, final Class<?>... methodTypes)
    {
        Exception failed = null;
        for (final String methodName : methodNames)
        {
            try
            {
                final Method m = clazz.getDeclaredMethod(methodName, methodTypes);
                m.setAccessible(true);
                return m;
            }
            catch (final Exception e)
            {
                failed = e;
            }
        }
        throw new UnableToFindMethodException(methodNames, failed);
    }
}
