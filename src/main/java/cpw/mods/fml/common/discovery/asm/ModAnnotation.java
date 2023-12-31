/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.discovery.asm;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.discovery.asm.ASMModParser.AnnotationType;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Map;

public class ModAnnotation
{
    public class EnumHolder
    {

        private String desc;
        private String value;

        public EnumHolder(final String desc, final String value)
        {
            this.desc = desc;
            this.value = value;
        }

    }
    AnnotationType type;
    Type asmType;
    String member;
    Map<String,Object> values = Maps.newHashMap();
    private ArrayList<Object> arrayList;
    private Object array;
    private String arrayName;
    private ModAnnotation parent;
    public ModAnnotation(final AnnotationType type, final Type asmType, final String member)
    {
        this.type = type;
        this.asmType = asmType;
        this.member = member;
    }

    public ModAnnotation(final AnnotationType type, final Type asmType, final ModAnnotation parent)
    {
        this.type = type;
        this.asmType = asmType;
        this.parent = parent;
    }
    @Override
    public String toString()
    {
        return Objects.toStringHelper("Annotation")
                .add("type",type)
                .add("name",asmType.getClassName())
                .add("member",member)
                .add("values", values)
                .toString();
    }
    public AnnotationType getType()
    {
        return type;
    }
    public Type getASMType()
    {
        return asmType;
    }
    public String getMember()
    {
        return member;
    }
    public Map<String, Object> getValues()
    {
        return values;
    }
    public void addArray(final String name)
    {
        this.arrayList = Lists.newArrayList();
        this.arrayName = name;
    }
    public void addProperty(final String key, final Object value)
    {
        if (this.arrayList != null)
        {
            arrayList.add(value);
        }
        else
        {
            values.put(key, value);
        }
    }

    public void addEnumProperty(final String key, final String enumName, final String value)
    {
        values.put(key, new EnumHolder(enumName, value));
    }

    public void endArray()
    {
        values.put(arrayName, arrayList);
        arrayList = null;
    }
    public ModAnnotation addChildAnnotation(final String name, final String desc)
    {
        final ModAnnotation child = new ModAnnotation(AnnotationType.SUBTYPE, Type.getType(desc), this);
        if (arrayList != null)
        {
            arrayList.add(child.getValues());
        }
        return child;
    }
}
