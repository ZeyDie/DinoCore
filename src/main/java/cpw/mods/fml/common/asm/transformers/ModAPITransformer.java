package cpw.mods.fml.common.asm.transformers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class ModAPITransformer implements IClassTransformer {

    private static final boolean logDebugInfo = Boolean.valueOf(System.getProperty("fml.debugAPITransformer", "true"));
    private ListMultimap<String, ASMData> optionals;

    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass)
    {
        if (optionals == null || !optionals.containsKey(name))
        {
            return basicClass;
        }
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - found optionals for class %s - processing", name);
        for (final ASMData optional : optionals.get(name))
        {
            final String modId = (String) optional.getAnnotationInfo().get("modid");

            if (Loader.isModLoaded(modId) || ModAPIManager.INSTANCE.hasAPI(modId))
            {
                if (logDebugInfo) FMLRelaunchLog.finest("Optional removal skipped - mod present %s", modId);
                continue;
            }
            if (logDebugInfo) FMLRelaunchLog.finest("Optional on %s triggered - mod missing %s", name, modId);

            if (optional.getAnnotationInfo().containsKey("iface"))
            {
                Boolean stripRefs = (Boolean)optional.getAnnotationInfo().get("striprefs");
                if (stripRefs == null) stripRefs = Boolean.FALSE;
                stripInterface(classNode,(String)optional.getAnnotationInfo().get("iface"), stripRefs);
            }
            else
            {
                stripMethod(classNode, (String)optional.getObjectName());
            }

        }
        if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - class %s processed", name);

        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void stripMethod(final ClassNode classNode, final String methodDescriptor)
    {
        for (final ListIterator<MethodNode> iterator = classNode.methods.listIterator(); iterator.hasNext();)
        {
            final MethodNode method = iterator.next();
            if (methodDescriptor.equals(method.name+method.desc))
            {
                iterator.remove();
                if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - method %s removed", methodDescriptor);
                return;
            }
        }
        if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - method %s NOT removed - not found", methodDescriptor);
    }

    private void stripInterface(final ClassNode classNode, final String interfaceName, final boolean stripRefs)
    {
        final String ifaceName = interfaceName.replace('.', '/');
        final boolean found = classNode.interfaces.remove(ifaceName);
        if (found && logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s removed", interfaceName);
        if (!found && logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s NOT removed - not found", interfaceName);

        if (found && stripRefs)
        {
            if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s - stripping method signature references", interfaceName);
            for (final Iterator<MethodNode> iterator = classNode.methods.iterator(); iterator.hasNext();)
            {
                final MethodNode node = iterator.next();
                if (node.desc.contains(ifaceName))
                {
                    if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s - stripping method containing reference %s", interfaceName, node.name);
                    iterator.remove();
                }
            }
            if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s - all method signature references stripped", interfaceName);
        }
        else if (found)
        {
            if (logDebugInfo) FMLRelaunchLog.finest("Optional removal - interface %s - NOT stripping method signature references", interfaceName);
        }
    }

    public void initTable(final ASMDataTable dataTable)
    {
        optionals = ArrayListMultimap.create();
        final Set<ASMData> interfaceLists = dataTable.getAll("cpw.mods.fml.common.Optional$InterfaceList");
        addData(unpackInterfaces(interfaceLists));
        final Set<ASMData> interfaces = dataTable.getAll("cpw.mods.fml.common.Optional$Interface");
        addData(interfaces);
        final Set<ASMData> methods = dataTable.getAll("cpw.mods.fml.common.Optional$Method");
        addData(methods);
    }

    private Set<ASMData> unpackInterfaces(final Set<ASMData> packedInterfaces)
    {
        final Set<ASMData> result = Sets.newHashSet();
        for (final ASMData data : packedInterfaces)
        {
            final List<Map<String,Object>> packedList = (List<Map<String,Object>>) data.getAnnotationInfo().get("value");
            for (final Map<String,Object> packed : packedList)
            {
                final ASMData newData = data.copy(packed);
                result.add(newData);
            }
        }

        return result;
    }
    private void addData(final Set<ASMData> interfaces)
    {
        for (final ASMData data : interfaces)
        {
            optionals.put(data.getClassName(),data);
        }
    }

}
