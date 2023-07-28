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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class ModAnnotationVisitor extends AnnotationVisitor
{
    private ASMModParser discoverer;
    private boolean array;
    private String name;
    private boolean isSubAnnotation;

    public ModAnnotationVisitor(final ASMModParser discoverer)
    {
        //TODO ZeyCodeReplace ASM4 on ASM5
        super(Opcodes.ASM5);
        this.discoverer = discoverer;
    }
    
    public ModAnnotationVisitor(final ASMModParser discoverer, final String name)
    {
        this(discoverer);
        this.array = true;
        this.name = name;
        discoverer.addAnnotationArray(name);
    }

    public ModAnnotationVisitor(final ASMModParser discoverer, final boolean isSubAnnotation)
    {
        this(discoverer);
        this.isSubAnnotation = true;
    }

    @Override
    public void visit(final String key, final Object value)
    {
        discoverer.addAnnotationProperty(key, value);
    }
    
    @Override
    public void visitEnum(final String name, final String desc, final String value)
    {
        discoverer.addAnnotationEnumProperty(name, desc, value);
    }
    
    @Override
    public AnnotationVisitor visitArray(final String name)
    {
        return new ModAnnotationVisitor(discoverer, name);
    }
    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc)
    {
        discoverer.addSubAnnotation(name, desc);
        return new ModAnnotationVisitor(discoverer, true);
    }
    @Override
    public void visitEnd()
    {
        if (array)
        {
            discoverer.endArray();
        }
        
        if (isSubAnnotation)
        {
            discoverer.endSubAnnotation();
        }
    }
}
