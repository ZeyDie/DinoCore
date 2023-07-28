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
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ModCandidate;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class ASMModParser
{

    private Type asmType;
    private int classVersion;
    private Type asmSuperType;
    private LinkedList<ModAnnotation> annotations = Lists.newLinkedList();
    private String baseModProperties;

    static enum AnnotationType
    {
        CLASS, FIELD, METHOD, SUBTYPE;
    }

    public ASMModParser(final InputStream stream) throws IOException
    {
        try
        {
            final ClassReader reader = new ClassReader(stream);
            reader.accept(new ModClassVisitor(this), 0);
        }
        catch (final Exception ex)
        {
            FMLLog.log(Level.SEVERE, ex, "Unable to read a class file correctly");
            throw new LoaderException(ex);
        }
    }

    public void beginNewTypeName(final String typeQName, final int classVersion, final String superClassQName)
    {
        this.asmType = Type.getObjectType(typeQName);
        this.classVersion = classVersion;
        this.asmSuperType = !Strings.isNullOrEmpty(superClassQName) ? Type.getObjectType(superClassQName) : null;
    }

    public void startClassAnnotation(final String annotationName)
    {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.CLASS, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
    }

    public void addAnnotationProperty(final String key, final Object value)
    {
        annotations.getFirst().addProperty(key, value);
    }

    public void startFieldAnnotation(final String fieldName, final String annotationName)
    {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.FIELD, Type.getType(annotationName), fieldName);
        annotations.addFirst(ann);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper("ASMAnnotationDiscoverer")
                .add("className", asmType.getClassName())
                .add("classVersion", classVersion)
                .add("superName", asmSuperType.getClassName())
                .add("annotations", annotations)
                .add("isBaseMod", isBaseMod(Collections.<String>emptyList()))
                .add("baseModProperties", baseModProperties)
                .toString();
    }

    public Type getASMType()
    {
        return asmType;
    }

    public int getClassVersion()
    {
        return classVersion;
    }

    public Type getASMSuperType()
    {
        return asmSuperType;
    }

    public LinkedList<ModAnnotation> getAnnotations()
    {
        return annotations;
    }

    public void validate()
    {
//        if (classVersion > 50.0)
//        {
//
//            throw new LoaderException(new RuntimeException("Mod compiled for Java 7 detected"));
//        }
    }

    public boolean isBaseMod(final List<String> rememberedTypes)
    {
        return getASMSuperType().equals(Type.getType("LBaseMod;")) || getASMSuperType().equals(Type.getType("Lnet/minecraft/src/BaseMod;"))|| rememberedTypes.contains(getASMSuperType().getClassName());
    }

    public void setBaseModProperties(final String foundProperties)
    {
        this.baseModProperties = foundProperties;
    }

    public String getBaseModProperties()
    {
        return this.baseModProperties;
    }

    public void sendToTable(final ASMDataTable table, final ModCandidate candidate)
    {
        for (final ModAnnotation ma : annotations)
        {
            table.addASMData(candidate, ma.asmType.getClassName(), this.asmType.getClassName(), ma.member, ma.values);
        }
    }

    public void addAnnotationArray(final String name)
    {
        annotations.getFirst().addArray(name);
    }

    public void addAnnotationEnumProperty(final String name, final String desc, final String value)
    {
        annotations.getFirst().addEnumProperty(name, desc, value);

    }

    public void endArray()
    {
        annotations.getFirst().endArray();

    }

    public void addSubAnnotation(final String name, final String desc)
    {
        final ModAnnotation ma = annotations.getFirst();
        annotations.addFirst(ma.addChildAnnotation(name, desc));
    }

    public void endSubAnnotation()
    {
        // take the child and stick it at the end
        final ModAnnotation child = annotations.removeFirst();
        annotations.addLast(child);
    }

    public void startMethodAnnotation(final String methodName, final String methodDescriptor, final String annotationName)
    {
        final ModAnnotation ann = new ModAnnotation(AnnotationType.METHOD, Type.getType(annotationName), methodName+methodDescriptor);
        annotations.addFirst(ann);
    }
}
