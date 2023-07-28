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

import org.objectweb.asm.*;

import java.util.Collections;

public class ModClassVisitor extends ClassVisitor
{
    private ASMModParser discoverer;

    public ModClassVisitor(final ASMModParser discoverer)
    {
        //TODO ZeyCodeReplace ASM4 on ASM5
        super(Opcodes.ASM5);
        this.discoverer = discoverer;
    }


    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
    {
        discoverer.beginNewTypeName(name, version, superName);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible)
    {
        discoverer.startClassAnnotation(annotationName);
        return new ModAnnotationVisitor(discoverer);
    }


    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value)
    {
        return new ModFieldVisitor(name, discoverer);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions)
    {
        if (discoverer.isBaseMod(Collections.<String>emptyList()) && name.equals("getPriorities") && desc.equals(Type.getMethodDescriptor(Type.getType(String.class))))
        {
            return new ModLoaderPropertiesMethodVisitor(name, discoverer);
        }
        else
        {
            return new ModMethodVisitor(name, desc, discoverer);
        }
    }
}
