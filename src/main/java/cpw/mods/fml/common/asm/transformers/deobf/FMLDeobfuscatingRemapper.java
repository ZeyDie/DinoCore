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

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.patcher.ClassPatchManager;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FMLDeobfuscatingRemapper extends Remapper {
    public static final FMLDeobfuscatingRemapper INSTANCE = new FMLDeobfuscatingRemapper();

    private BiMap<String, String> classNameBiMap;
    private BiMap<String, String> mcpNameBiMap;

    private Map<String,Map<String,String>> rawFieldMaps;
    private Map<String,Map<String,String>> rawMethodMaps;

    private Map<String,Map<String,String>> fieldNameMaps;
    private Map<String,Map<String,String>> methodNameMaps;

    private LaunchClassLoader classLoader;


    private static final boolean DEBUG_REMAPPING = Boolean.parseBoolean(System.getProperty("fml.remappingDebug", "false"));
    private static final boolean DUMP_FIELD_MAPS = Boolean.parseBoolean(System.getProperty("fml.remappingDebug.dumpFieldMaps", "false")) && DEBUG_REMAPPING;
    private static final boolean DUMP_METHOD_MAPS = Boolean.parseBoolean(System.getProperty("fml.remappingDebug.dumpMethodMaps", "false")) && DEBUG_REMAPPING;

    private FMLDeobfuscatingRemapper()
    {
        classNameBiMap=ImmutableBiMap.of();
        mcpNameBiMap=ImmutableBiMap.of();
    }

    public void setupLoadOnly(final String deobfFileName, final boolean loadAll)
    {
        try
        {
            final File mapData = new File(deobfFileName);
            final LZMAInputSupplier zis = new LZMAInputSupplier(new FileInputStream(mapData));
            final InputSupplier<InputStreamReader> srgSupplier = CharStreams.newReaderSupplier(zis,Charsets.UTF_8);
            final List<String> srgList = CharStreams.readLines(srgSupplier);
            rawMethodMaps = Maps.newHashMap();
            rawFieldMaps = Maps.newHashMap();
            final Builder<String, String> builder = ImmutableBiMap.<String,String>builder();
            final Builder<String, String> mcpBuilder = ImmutableBiMap.<String,String>builder();
            final Splitter splitter = Splitter.on(CharMatcher.anyOf(": ")).omitEmptyStrings().trimResults();
            for (final String line : srgList)
            {
                final String[] parts = Iterables.toArray(splitter.split(line),String.class);
                final String typ = parts[0];
                if ("CL".equals(typ))
                {
                    parseClass(builder, parts);
                    parseMCPClass(mcpBuilder,parts);
                }
                else if ("MD".equals(typ) && loadAll)
                {
                    parseMethod(parts);
                }
                else if ("FD".equals(typ) && loadAll)
                {
                    parseField(parts);
                }
            }
            classNameBiMap = builder.build();
            // Special case some mappings for modloader mods
            mcpBuilder.put("BaseMod","net/minecraft/src/BaseMod");
            mcpBuilder.put("ModLoader","net/minecraft/src/ModLoader");
            mcpBuilder.put("EntityRendererProxy","net/minecraft/src/EntityRendererProxy");
            mcpBuilder.put("MLProp","net/minecraft/src/MLProp");
            mcpBuilder.put("TradeEntry","net/minecraft/src/TradeEntry");
            mcpNameBiMap = mcpBuilder.build();
        }
        catch (final IOException ioe)
        {
            Logger.getLogger("FML").log(Level.SEVERE, "An error occurred loading the deobfuscation map data", ioe);
        }
        methodNameMaps = Maps.newHashMapWithExpectedSize(rawMethodMaps.size());
        fieldNameMaps = Maps.newHashMapWithExpectedSize(rawFieldMaps.size());

    }
    public void setup(final File mcDir, final LaunchClassLoader classLoader, final String deobfFileName)
    {
        this.classLoader = classLoader;
        try
        {
            final InputStream classData = getClass().getResourceAsStream(deobfFileName);
            final LZMAInputSupplier zis = new LZMAInputSupplier(classData);
            final InputSupplier<InputStreamReader> srgSupplier = CharStreams.newReaderSupplier(zis,Charsets.UTF_8);
            final List<String> srgList = CharStreams.readLines(srgSupplier);
            rawMethodMaps = Maps.newHashMap();
            rawFieldMaps = Maps.newHashMap();
            final Builder<String, String> builder = ImmutableBiMap.<String,String>builder();
            final Builder<String, String> mcpBuilder = ImmutableBiMap.<String,String>builder();
            final Splitter splitter = Splitter.on(CharMatcher.anyOf(": ")).omitEmptyStrings().trimResults();
            for (final String line : srgList)
            {
                final String[] parts = Iterables.toArray(splitter.split(line),String.class);
                final String typ = parts[0];
                if ("CL".equals(typ))
                {
                    parseClass(builder, parts);
                    parseMCPClass(mcpBuilder,parts);
                }
                else if ("MD".equals(typ))
                {
                    parseMethod(parts);
                }
                else if ("FD".equals(typ))
                {
                    parseField(parts);
                }
            }
            classNameBiMap = builder.build();
            // Special case some mappings for modloader mods
            mcpBuilder.put("BaseMod","net/minecraft/src/BaseMod");
            mcpBuilder.put("ModLoader","net/minecraft/src/ModLoader");
            mcpBuilder.put("EntityRendererProxy","net/minecraft/src/EntityRendererProxy");
            mcpBuilder.put("MLProp","net/minecraft/src/MLProp");
            mcpBuilder.put("TradeEntry","net/minecraft/src/TradeEntry");
            mcpNameBiMap = mcpBuilder.build();
        }
        catch (final IOException ioe)
        {
            FMLRelaunchLog.log(Level.SEVERE, ioe, "An error occurred loading the deobfuscation map data");
        }
        methodNameMaps = Maps.newHashMapWithExpectedSize(rawMethodMaps.size());
        fieldNameMaps = Maps.newHashMapWithExpectedSize(rawFieldMaps.size());
    }

    public boolean isRemappedClass(String className)
    {
        String className1 = className.replace('.', '/');
        return classNameBiMap.containsKey(className1) || mcpNameBiMap.containsKey(className1) || (!classNameBiMap.isEmpty() && className1.indexOf('/') == -1);
    }

    private void parseField(final String[] parts)
    {
        final String oldSrg = parts[1];
        final int lastOld = oldSrg.lastIndexOf('/');
        final String cl = oldSrg.substring(0,lastOld);
        final String oldName = oldSrg.substring(lastOld+1);
        final String newSrg = parts[2];
        final int lastNew = newSrg.lastIndexOf('/');
        final String newName = newSrg.substring(lastNew+1);
        if (!rawFieldMaps.containsKey(cl))
        {
            rawFieldMaps.put(cl, Maps.<String,String>newHashMap());
        }
        rawFieldMaps.get(cl).put(oldName + ":" + getFieldType(cl, oldName), newName);
        rawFieldMaps.get(cl).put(oldName + ":null", newName);
    }

    /*
     * Cache the field descriptions for classes so we don't repeatedly reload the same data again and again
     */
    private Map<String,Map<String,String>> fieldDescriptions = Maps.newHashMap();

    // Cache null values so we don't waste time trying to recompute classes with no field or method maps
    private Set<String> negativeCacheMethods = Sets.newHashSet();
    private Set<String> negativeCacheFields = Sets.newHashSet();

    private String getFieldType(final String owner, final String name)
    {
        if (fieldDescriptions.containsKey(owner))
        {
            return fieldDescriptions.get(owner).get(name);
        }
        synchronized (fieldDescriptions)
        {
            try
            {
                final byte[] classBytes = ClassPatchManager.INSTANCE.getPatchedResource(owner, map(owner).replace('/', '.'), classLoader);
                if (classBytes == null)
                {
                    return null;
                }
                final ClassReader cr = new ClassReader(classBytes);
                final ClassNode classNode = new ClassNode();
                cr.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                final Map<String,String> resMap = Maps.newHashMap();
                for (final FieldNode fieldNode : (List<FieldNode>) classNode.fields) {
                    resMap.put(fieldNode.name, fieldNode.desc);
                }
                fieldDescriptions.put(owner, resMap);
                return resMap.get(name);
            }
            catch (final IOException e)
            {
                FMLLog.log(Level.SEVERE,e, "A critical exception occured reading a class file %s", owner);
            }
            return null;
        }
    }

    private void parseClass(final Builder<String, String> builder, final String[] parts)
    {
        builder.put(parts[1],parts[2]);
    }

    private void parseMCPClass(final Builder<String, String> builder, final String[] parts)
    {
        final int clIdx = parts[2].lastIndexOf('/');
        builder.put("net/minecraft/src/"+parts[2].substring(clIdx+1),parts[2]);
    }

    private void parseMethod(final String[] parts)
    {
        final String oldSrg = parts[1];
        final int lastOld = oldSrg.lastIndexOf('/');
        final String cl = oldSrg.substring(0,lastOld);
        final String oldName = oldSrg.substring(lastOld+1);
        final String sig = parts[2];
        final String newSrg = parts[3];
        final int lastNew = newSrg.lastIndexOf('/');
        final String newName = newSrg.substring(lastNew+1);
        if (!rawMethodMaps.containsKey(cl))
        {
            rawMethodMaps.put(cl, Maps.<String,String>newHashMap());
        }
        rawMethodMaps.get(cl).put(oldName+sig, newName);
    }

    @Override
    public String mapFieldName(final String owner, final String name, final String desc)
    {
        if (classNameBiMap == null || classNameBiMap.isEmpty())
        {
            return name;
        }
        final Map<String, String> fieldMap = getFieldMap(owner);
        return fieldMap!=null && fieldMap.containsKey(name+":"+desc) ? fieldMap.get(name+":"+desc) : name;
    }

    @Override
    public String map(final String typeName)
    {
        if (classNameBiMap == null || classNameBiMap.isEmpty())
        {
            return typeName;
        }

        final int dollarIdx = typeName.indexOf('$');
        final String realType = dollarIdx > -1 ? typeName.substring(0, dollarIdx) : typeName;
        final String subType = dollarIdx > -1 ? typeName.substring(dollarIdx+1) : "";

        String result = classNameBiMap.containsKey(realType) ? classNameBiMap.get(realType) : mcpNameBiMap.containsKey(realType) ? mcpNameBiMap.get(realType) : realType;
        result = dollarIdx > -1 ? result+"$"+subType : result;
        return result;
    }

    public String unmap(final String typeName)
    {
        if (classNameBiMap == null || classNameBiMap.isEmpty())
        {
            return typeName;
        }
        final int dollarIdx = typeName.indexOf('$');
        final String realType = dollarIdx > -1 ? typeName.substring(0, dollarIdx) : typeName;
        final String subType = dollarIdx > -1 ? typeName.substring(dollarIdx+1) : "";


        String result = classNameBiMap.containsValue(realType) ? classNameBiMap.inverse().get(realType) : mcpNameBiMap.containsValue(realType) ? mcpNameBiMap.inverse().get(realType) : realType;
        result = dollarIdx > -1 ? result+"$"+subType : result;
        return result;
    }


    @Override
    public String mapMethodName(final String owner, final String name, final String desc)
    {
        if (classNameBiMap==null || classNameBiMap.isEmpty())
        {
            return name;
        }
        final Map<String, String> methodMap = getMethodMap(owner);
        final String methodDescriptor = name+desc;
        return methodMap!=null && methodMap.containsKey(methodDescriptor) ? methodMap.get(methodDescriptor) : name;
    }

    private Map<String,String> getFieldMap(final String className)
    {
        if (!fieldNameMaps.containsKey(className) && !negativeCacheFields.contains(className))
        {
            findAndMergeSuperMaps(className);
            if (!fieldNameMaps.containsKey(className))
            {
                negativeCacheFields.add(className);
            }

            if (DUMP_FIELD_MAPS)
            {
                FMLRelaunchLog.finest("Field map for %s : %s", className, fieldNameMaps.get(className));
            }
        }
        return fieldNameMaps.get(className);
    }

    private Map<String,String> getMethodMap(final String className)
    {
        if (!methodNameMaps.containsKey(className) && !negativeCacheMethods.contains(className))
        {
            findAndMergeSuperMaps(className);
            if (!methodNameMaps.containsKey(className))
            {
                negativeCacheMethods.add(className);
            }
            if (DUMP_METHOD_MAPS)
            {
                FMLRelaunchLog.finest("Method map for %s : %s", className, methodNameMaps.get(className));
            }

        }
        return methodNameMaps.get(className);
    }

    private void findAndMergeSuperMaps(final String name)
    {
        try
        {
            String superName = null;
            String[] interfaces = new String[0];
            final byte[] classBytes = ClassPatchManager.INSTANCE.getPatchedResource(name, map(name), classLoader);
            if (classBytes != null)
            {
                final ClassReader cr = new ClassReader(classBytes);
                superName = cr.getSuperName();
                interfaces = cr.getInterfaces();
            }
            mergeSuperMaps(name, superName, interfaces);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
    public void mergeSuperMaps(final String name, final String superName, final String[] interfaces)
    {
//        System.out.printf("Computing super maps for %s: %s %s\n", name, superName, Arrays.asList(interfaces));
        if (classNameBiMap == null || classNameBiMap.isEmpty())
        {
            return;
        }
        // Skip Object
        if (Strings.isNullOrEmpty(superName))
        {
            return;
        }

        final List<String> allParents = ImmutableList.<String>builder().add(superName).addAll(Arrays.asList(interfaces)).build();
        // generate maps for all parent objects
        for (final String parentThing : allParents)
        {
            if (!methodNameMaps.containsKey(parentThing))
            {
                findAndMergeSuperMaps(parentThing);
            }
        }
        final Map<String, String> methodMap = Maps.<String,String>newHashMap();
        final Map<String, String> fieldMap = Maps.<String,String>newHashMap();
        for (final String parentThing : allParents)
        {
            if (methodNameMaps.containsKey(parentThing))
            {
                methodMap.putAll(methodNameMaps.get(parentThing));
            }
            if (fieldNameMaps.containsKey(parentThing))
            {
                fieldMap.putAll(fieldNameMaps.get(parentThing));
            }
        }
        if (rawMethodMaps.containsKey(name))
        {
            methodMap.putAll(rawMethodMaps.get(name));
        }
        if (rawFieldMaps.containsKey(name))
        {
            fieldMap.putAll(rawFieldMaps.get(name));
        }
        methodNameMaps.put(name, ImmutableMap.copyOf(methodMap));
        fieldNameMaps.put(name, ImmutableMap.copyOf(fieldMap));
//        System.out.printf("Maps: %s %s\n", name, methodMap);
    }

    public Set<String> getObfedClasses()
    {
        return ImmutableSet.copyOf(classNameBiMap.keySet());
    }
}
