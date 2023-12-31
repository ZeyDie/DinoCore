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

package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {

    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }
        final ClassReader classReader = new ClassReader(bytes);
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        final RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
        classReader.accept(remapAdapter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    @Override
    public String remapClassName(final String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.','/')).replace('/', '.');
    }

    @Override
    public String unmapClassName(final String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
    }

}
