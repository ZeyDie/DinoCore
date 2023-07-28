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

package cpw.mods.fml.common.asm.transformers.deobf;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class FMLRemappingAdapter extends RemappingClassAdapter {
    public FMLRemappingAdapter(final ClassVisitor cv)
    {
        super(cv, FMLDeobfuscatingRemapper.INSTANCE);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, String[] interfaces)
    {
        String[] interfaces1 = interfaces;
        if (interfaces1 == null)
        {
            interfaces1 = new String[0];
        }
        FMLDeobfuscatingRemapper.INSTANCE.mergeSuperMaps(name, superName, interfaces1);
        super.visit(version, access, name, signature, superName, interfaces1);
    }
}
