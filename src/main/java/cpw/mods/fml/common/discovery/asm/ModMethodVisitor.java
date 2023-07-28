package cpw.mods.fml.common.discovery.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModMethodVisitor extends MethodVisitor {

    private String methodName;
    private String methodDescriptor;
    private ASMModParser discoverer;

    public ModMethodVisitor(final String name, final String desc, final ASMModParser discoverer)
    {
        //TODO ZeyCodeReplace ASM4 on ASM5
        super(Opcodes.ASM5);
        this.methodName = name;
        this.methodDescriptor = desc;
        this.discoverer = discoverer;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible)
    {
        discoverer.startMethodAnnotation(methodName, methodDescriptor, annotationName);
        return new ModAnnotationVisitor(discoverer);
    }

}
