package net.minecraftforge.event;

import com.google.common.collect.Maps;
import mcp.mobius.mobiuscore.profiler.ProfilerSection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;


public class ASMEventHandler implements IEventListener
{
    private static int IDs = 0;
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);    
    private static final ASMClassLoader LOADER = new ASMClassLoader();
    private static final HashMap<Method, Class<?>> cache = Maps.newHashMap();
    
    private final IEventListener handler;
    private final ForgeSubscribe subInfo;
    private final String         package_; // Cauldron - mobius
    public ASMEventHandler(final Object target, final Method method) throws Exception
    {
        package_ = method.getDeclaringClass().getCanonicalName(); // Cauldron - mobius
        handler = (IEventListener)createWrapper(method).getConstructor(Object.class).newInstance(target);
        subInfo = method.getAnnotation(ForgeSubscribe.class);
    }

    @Override
    public void invoke(final Event event)
    {
        if (handler != null)
        {
            if (!event.isCancelable() || !event.isCanceled() || subInfo.receiveCanceled())
            {
                // Cauldron start - mobius hooks
            	ProfilerSection.EVENT_INVOKE.start();
                handler.invoke(event);
                ProfilerSection.EVENT_INVOKE.stop(event, package_, handler);
                // Cauldron end
            }
        }
    }
    
    public EventPriority getPriority()
    {
        return subInfo.priority();
    }
    
    public Class<?> createWrapper(final Method callback)
    {
        if (cache.containsKey(callback))
        {
            return cache.get(callback);
        }

        final ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        
        final String name = getUniqueName(callback);
        final String desc = name.replace('.',  '/');
        final String instType = Type.getInternalName(callback.getDeclaringClass());
        final String eventType = Type.getInternalName(callback.getParameterTypes()[0]);
        
        /*
        System.out.println("Name:     " + name);
        System.out.println("Desc:     " + desc);
        System.out.println("InstType: " + instType);
        System.out.println("Callback: " + callback.getName() + Type.getMethodDescriptor(callback));
        System.out.println("Event:    " + eventType);
        */
        
        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{ HANDLER_DESC });

        cw.visitSource(".dynamic", null);
        {
            cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, instType);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventType);
            mv.visitMethodInsn(INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback));
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        final Class<?> ret = LOADER.define(name, cw.toByteArray());
        cache.put(callback, ret);
        return ret;
    }
    
    private String getUniqueName(final Method callback)
    {
        return String.format("%s_%d_%s_%s_%s", getClass().getName(), IDs++, 
                callback.getDeclaringClass().getSimpleName(), 
                callback.getName(), 
                callback.getParameterTypes()[0].getSimpleName());
    }
    
    private static class ASMClassLoader extends ClassLoader
    {
        private ASMClassLoader()
        {
            super(ASMClassLoader.class.getClassLoader());
        }
        
        public Class<?> define(final String name, final byte[] data)
        {
            return defineClass(name, data, 0, data.length);
        }
    }

}
