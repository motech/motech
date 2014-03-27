package org.motechproject.mds.builder.impl;

import org.motechproject.mds.builder.EnumBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.javassist.JavassistHelper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link org.motechproject.mds.builder.EnumBuilder} interface.
 */
@Component
public class EnumBuilderImpl implements EnumBuilder, Opcodes {

    @Override
    public List<ClassData> build(Entity entity) {
        List<ClassData> list = new ArrayList<>();

        for (Field field : entity.getFields()) {
            org.motechproject.mds.domain.Type type = field.getType();

            if (type.isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(entity, field);

                if (holder.isEnum() || holder.isEnumList()) {
                    list.add(build(holder));
                }
            }
        }

        return list;
    }

    private ClassData build(ComboboxHolder holder) {
        ClassWriter classWriter = new ClassWriter(false);
        ClassHelper helper = new ClassHelper(holder);

        start(classWriter, helper);

        visitFields(classWriter, helper);
        visitValues(classWriter, helper);
        visitValueOf(classWriter, helper);
        visitInit(classWriter, helper);
        visitClinit(classWriter, helper);

        end(classWriter);

        return new ClassData(helper.className, classWriter.toByteArray());
    }

    private void start(ClassWriter classWriter, ClassHelper helper) {
        String classGenericSignature = JavassistHelper.genericSignature(Enum.class, helper.className);

        classWriter.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, helper.classPath, classGenericSignature, "java/lang/Enum", null);
    }

    private void visitFields(ClassWriter classWriter, ClassHelper helper) {
        FieldVisitor fieldVisitor;

        for (String value : helper.values) {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, value, helper.genericParam, null, null);
            fieldVisitor.visitEnd();
        }

        fieldVisitor = classWriter.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", helper.arrayGenericParam, null, null);
        fieldVisitor.visitEnd();
    }

    private void visitValues(ClassWriter classWriter, ClassHelper helper) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()" + helper.arrayGenericParam, null, null);
        methodVisitor.visitCode();

        Label l0 = new Label();
        methodVisitor.visitLabel(l0);
        methodVisitor.visitLineNumber(3, l0);
        methodVisitor.visitFieldInsn(GETSTATIC, helper.classPath, "$VALUES", helper.arrayGenericParam);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, helper.arrayGenericParam, "clone", "()Ljava/lang/Object;");
        methodVisitor.visitTypeInsn(CHECKCAST, helper.arrayGenericParam);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(1, 0);
        methodVisitor.visitEnd();
    }

    private void visitValueOf(ClassWriter classWriter, ClassHelper helper) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)" + helper.genericParam, null, null);
        methodVisitor.visitCode();

        Label l0 = new Label();
        methodVisitor.visitLabel(l0);
        methodVisitor.visitLineNumber(3, l0);
        methodVisitor.visitLdcInsn(Type.getType(helper.genericParam));
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;");
        methodVisitor.visitTypeInsn(CHECKCAST, helper.classPath);
        methodVisitor.visitInsn(ARETURN);

        Label l1 = new Label();
        methodVisitor.visitLabel(l1);
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l1, 0);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();
    }

    private void visitInit(ClassWriter classWriter, ClassHelper helper) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", "()V", null);
        methodVisitor.visitCode();

        Label l0 = new Label();
        methodVisitor.visitLabel(l0);
        methodVisitor.visitLineNumber(3, l0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ILOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V");
        methodVisitor.visitInsn(RETURN);

        Label l1 = new Label();
        methodVisitor.visitLabel(l1);
        methodVisitor.visitLocalVariable("this", helper.genericParam, null, l0, l1, 0);
        methodVisitor.visitMaxs(3, 3);
        methodVisitor.visitEnd();
    }

    private void visitClinit(ClassWriter classWriter, ClassHelper helper) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();

        Label l0 = new Label();
        methodVisitor.visitLabel(l0);
        methodVisitor.visitLineNumber(4, l0);

        for (int i = 0; i < helper.values.length; ++i) {
            String value = helper.values[i];

            methodVisitor.visitTypeInsn(NEW, helper.classPath);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(value);
            methodVisitor.visitInsn(ICONST_0 + i);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, helper.classPath, "<init>", "(Ljava/lang/String;I)V");
            methodVisitor.visitFieldInsn(PUTSTATIC, helper.classPath, value, helper.genericParam);
        }

        Label l1 = new Label();
        methodVisitor.visitLabel(l1);
        methodVisitor.visitLineNumber(3, l1);
        methodVisitor.visitInsn(ICONST_0 + helper.values.length);
        methodVisitor.visitTypeInsn(ANEWARRAY, helper.classPath);

        for (int i = 0; i < helper.values.length; ++i) {
            String value = helper.values[i];

            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0 + i);
            methodVisitor.visitFieldInsn(GETSTATIC, helper.classPath, value, helper.genericParam);
            methodVisitor.visitInsn(AASTORE);
        }

        methodVisitor.visitFieldInsn(PUTSTATIC, helper.classPath, "$VALUES", helper.arrayGenericParam);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(4, 0);
        methodVisitor.visitEnd();
    }

    private void end(ClassWriter cw) {
        cw.visitEnd();
    }

    private static final class ClassHelper {
        private String className;
        private String classPath;
        private String genericParam;
        private String arrayGenericParam;
        private String[] values;

        private ClassHelper(ComboboxHolder holder) {
            this.className = holder.getEnumFullName();
            this.values = holder.getValues();

            this.classPath = JavassistHelper.toClassPath(className, false);
            this.genericParam = JavassistHelper.toGenericParam(className);
            this.arrayGenericParam = "[" + genericParam;
        }
    }

}
