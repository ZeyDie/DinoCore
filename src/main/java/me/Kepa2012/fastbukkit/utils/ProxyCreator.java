package me.Kepa2012.fastbukkit.utils;

import javassist.*;
import javassist.bytecode.SignatureAttribute;
import me.Kepa2012.fastbukkit.model.IProxiedEvent;
import ru.zoom4ikdan4ik.settings.optimization.CoreSettings;

import java.lang.reflect.Method;
import java.util.Random;

public class ProxyCreator {
    private static final Random rng = new Random();
    private static final ClassPool pool = ClassPool.getDefault();
    private static CtClass proxyInterface;

    static {
        try {
            proxyInterface = pool.get("me.Kepa2012.fastbukkit.model.IProxiedEvent");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static IProxiedEvent createEventProxy(Method eventMethod) {
        if (!CoreSettings.getInstance().enableFastBukkit) return null;

        try {
            Class<?> declCl = eventMethod.getDeclaringClass();
            ClassLoader bukkitLoader = declCl.getClassLoader();
            pool.insertClassPath(new LoaderClassPath(bukkitLoader));
            int randomNumber = rng.nextInt(999999);

            CtClass ctProxiedEvent = pool.makeClass(eventMethod.getDeclaringClass().getName() + "_" + eventMethod.getName() + randomNumber);
            ctProxiedEvent.setInterfaces(new CtClass[]{proxyInterface});

            SignatureAttribute.ClassSignature signature = new SignatureAttribute.ClassSignature(null, null, new SignatureAttribute.ClassType[]{new SignatureAttribute.ClassType(ctProxiedEvent.getName(), new SignatureAttribute.TypeArgument[]{new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType("me.Kepa2012.fastbukkit.model.IProxiedEvent"))})});

            ctProxiedEvent.setGenericSignature(signature.encode());

            String finalMethodCode = getProxyCode(eventMethod.getDeclaringClass().getName(), eventMethod.getName(), eventMethod.getParameterTypes()[0].getName());

            CtMethod ctCallEvent = CtNewMethod.make(finalMethodCode, ctProxiedEvent);

            ctProxiedEvent.addMethod(ctCallEvent);

            Class<IProxiedEvent> c = (Class<IProxiedEvent>) ctProxiedEvent.toClass(bukkitLoader, declCl.getProtectionDomain());

            System.out.println("[DBG-FastBukkit] Created proxy " + eventMethod.getDeclaringClass().getName() + "_" + eventMethod.getName() + randomNumber);

            return c.newInstance();
        } catch (Throwable t_) {
            System.err.println("[DBG-FastBukkit] Something went wrong with " + eventMethod.getDeclaringClass().getName() + "." + eventMethod.getName());
            System.err.println("[DBG-FastBukkit] Error: " + t_.getClass().getSimpleName() + ":" + t_.getMessage());
            System.err.println("[DBG-FastBukkit] We will use slow reflection invocation");

            return null;
        }
    }

    private static String getProxyCode(String listenerClass, String methodName, String eventClass) {
        return "public void callEvent(org.bukkit.event.Listener listener, org.bukkit.event.Event event) { ((" + listenerClass + ")listener)." + methodName + "((" + eventClass + ") event); }";
    }
}
