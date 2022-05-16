package com.ptsmods.morecommands.dumps.compat;
import static com.ptsmods.morecommands.dumps.ASMDump.map
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;

public class Compat182Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/Compat182", null, "com/ptsmods/morecommands/compat/Compat18", null);

classWriter.visitSource("Compat182.java", null);

classWriter.visitInnerClass(map("net/minecraft/class_6880$class_6883", "net/minecraft/util/registry/RegistryEntry$Reference", "net/minecraft/core/Holder$Reference"), map("net/minecraft/class_6880", "net/minecraft/util/registry/RegistryEntry", "net/minecraft/core/Holder"), map("class_6883", "Reference", "Reference"), ACC_PUBLIC | ACC_STATIC);

classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "registryEntryReference", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", null);
fieldVisitor.visitEnd();
}
{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "blockTags", "Ljava/util/Map;", "Ljava/util/Map<" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + "Ljava/lang/Object;>;", null);
fieldVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(19, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/ptsmods/morecommands/compat/Compat18", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "registryContainsId", "(" + map("Lnet/minecraft/class_2370;", "Lnet/minecraft/util/registry/SimpleRegistry;", "Lnet/minecraft/core/MappedRegistry;") + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Z", "<T:Ljava/lang/Object;>(" + map("Lnet/minecraft/class_2370", "Lnet/minecraft/util/registry/SimpleRegistry", "Lnet/minecraft/core/MappedRegistry") + "<TT;>;" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Z", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(27, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2370", "net/minecraft/util/registry/SimpleRegistry", "net/minecraft/core/MappedRegistry"), map("method_10250", "containsId", "containsKey"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Z", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("registry", map("Lnet/minecraft/class_2370;", "Lnet/minecraft/util/registry/SimpleRegistry;", "Lnet/minecraft/core/MappedRegistry;"), map("Lnet/minecraft/class_2370", "Lnet/minecraft/util/registry/SimpleRegistry", "Lnet/minecraft/core/MappedRegistry") + "<TT;>;", label0, label1, 1);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "tagContains", "(Ljava/lang/Object;Ljava/lang/Object;)Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
Label label1 = new Label();
Label label2 = new Label();
methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
Label label3 = new Label();
Label label4 = new Label();
Label label5 = new Label();
methodVisitor.visitTryCatchBlock(label3, label4, label5, "java/lang/IllegalAccessException");
methodVisitor.visitTryCatchBlock(label3, label4, label5, "java/lang/reflect/InvocationTargetException");
Label label6 = new Label();
methodVisitor.visitLabel(label6);
methodVisitor.visitLineNumber(33, label6);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ASTORE, 3);
Label label7 = new Label();
methodVisitor.visitLabel(label7);
methodVisitor.visitLineNumber(35, label7);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat182", "registryEntryReference", "Ljava/lang/Class;");
Label label8 = new Label();
methodVisitor.visitJumpInsn(IFNONNULL, label8);
Label label9 = new Label();
methodVisitor.visitLabel(label9);
methodVisitor.visitLineNumber(36, label9);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitVarInsn(ASTORE, 4);
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(38, label0);
methodVisitor.visitLdcInsn(Type.getType(map("Lnet/minecraft/class_6880$class_6883;", "Lnet/minecraft/util/registry/RegistryEntry$Reference;", "Lnet/minecraft/core/Holder$Reference;")));
methodVisitor.visitVarInsn(ASTORE, 4);
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(39, label1);
Label label10 = new Label();
methodVisitor.visitJumpInsn(GOTO, label10);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_FULL, 5, new Object[] {"com/ptsmods/morecommands/compat/Compat182", "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Class"}, 1, new Object[] {"java/lang/Exception"});
methodVisitor.visitVarInsn(ASTORE, 5);
methodVisitor.visitLabel(label10);
methodVisitor.visitLineNumber(41, label10);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat182", "registryEntryReference", "Ljava/lang/Class;");
methodVisitor.visitLabel(label8);
methodVisitor.visitLineNumber(44, label8);
methodVisitor.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;", false);
methodVisitor.visitVarInsn(ASTORE, 4);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitInsn(ARRAYLENGTH);
methodVisitor.visitVarInsn(ISTORE, 5);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitVarInsn(ISTORE, 6);
Label label11 = new Label();
methodVisitor.visitLabel(label11);
methodVisitor.visitFrame(Opcodes.F_APPEND,3, new Object[] {"[Ljava/lang/reflect/Method;", Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
methodVisitor.visitVarInsn(ILOAD, 6);
methodVisitor.visitVarInsn(ILOAD, 5);
Label label12 = new Label();
methodVisitor.visitJumpInsn(IF_ICMPGE, label12);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ILOAD, 6);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitVarInsn(ASTORE, 7);
Label label13 = new Label();
methodVisitor.visitLabel(label13);
methodVisitor.visitLineNumber(45, label13);
methodVisitor.visitVarInsn(ALOAD, 7);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", false);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat182", "registryEntryReference", "Ljava/lang/Class;");
Label label14 = new Label();
methodVisitor.visitJumpInsn(IF_ACMPNE, label14);
methodVisitor.visitVarInsn(ALOAD, 7);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
methodVisitor.visitInsn(ARRAYLENGTH);
methodVisitor.visitJumpInsn(IFNE, label14);
methodVisitor.visitVarInsn(ALOAD, 7);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getModifiers", "()I", false);
methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
methodVisitor.visitJumpInsn(IFNE, label14);
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(47, label3);
methodVisitor.visitVarInsn(ALOAD, 7);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
methodVisitor.visitVarInsn(ASTORE, 3);
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(51, label4);
methodVisitor.visitJumpInsn(GOTO, label14);
methodVisitor.visitLabel(label5);
methodVisitor.visitLineNumber(48, label5);
methodVisitor.visitFrame(Opcodes.F_FULL, 8, new Object[] {"com/ptsmods/morecommands/compat/Compat182", "java/lang/Object", "java/lang/Object", "java/lang/Object", "[Ljava/lang/reflect/Method;", Opcodes.INTEGER, Opcodes.INTEGER, "java/lang/reflect/Method"}, 1, new Object[] {"java/lang/ReflectiveOperationException"});
methodVisitor.visitVarInsn(ASTORE, 8);
Label label15 = new Label();
methodVisitor.visitLabel(label15);
methodVisitor.visitLineNumber(49, label15);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/api/ReflectionHelper", "LOG", "Lorg/apache/logging/log4j/Logger;");
methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
methodVisitor.visitLdcInsn("Could not get registry entry of object ");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;)V", true);
Label label16 = new Label();
methodVisitor.visitLabel(label16);
methodVisitor.visitLineNumber(50, label16);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitInsn(IRETURN);
methodVisitor.visitLabel(label14);
methodVisitor.visitLineNumber(44, label14);
methodVisitor.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
methodVisitor.visitIincInsn(6, 1);
methodVisitor.visitJumpInsn(GOTO, label11);
methodVisitor.visitLabel(label12);
methodVisitor.visitLineNumber(54, label12);
methodVisitor.visitFrame(Opcodes.F_CHOP,3, null, 0, null);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitTypeInsn(INSTANCEOF, map("net/minecraft/class_6880", "net/minecraft/util/registry/RegistryEntry", "net/minecraft/core/Holder"));
Label label17 = new Label();
methodVisitor.visitJumpInsn(IFNE, label17);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitInsn(IRETURN);
methodVisitor.visitLabel(label17);
methodVisitor.visitLineNumber(56, label17);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_6880", "net/minecraft/util/registry/RegistryEntry", "net/minecraft/core/Holder"));
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_6862", "net/minecraft/tag/TagKey", "net/minecraft/tags/TagKey"));
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_6880", "net/minecraft/util/registry/RegistryEntry", "net/minecraft/core/Holder"), map("method_40220", "isIn", "is"), "(" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;") + ")Z", true);
methodVisitor.visitInsn(IRETURN);
Label label18 = new Label();
methodVisitor.visitLabel(label18);
methodVisitor.visitLocalVariable("c", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", label0, label8, 4);
methodVisitor.visitLocalVariable("e", "Ljava/lang/ReflectiveOperationException;", null, label15, label14, 8);
methodVisitor.visitLocalVariable("method", "Ljava/lang/reflect/Method;", null, label13, label14, 7);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label6, label18, 0);
methodVisitor.visitLocalVariable("tag", "Ljava/lang/Object;", null, label6, label18, 1);
methodVisitor.visitLocalVariable("obj", "Ljava/lang/Object;", null, label6, label18, 2);
methodVisitor.visitLocalVariable("registryEntry", "Ljava/lang/Object;", null, label7, label18, 3);
methodVisitor.visitMaxs(3, 9);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getRegistry", "(" + map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;") + map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;") + ")" + map("Lnet/minecraft/class_2378;", "Lnet/minecraft/util/registry/Registry;", "Lnet/minecraft/core/Registry;"), "<E:Ljava/lang/Object;>(" + map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;") + map("Lnet/minecraft/class_5321", "Lnet/minecraft/util/registry/RegistryKey", "Lnet/minecraft/resources/ResourceKey") + "<+" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;>;)" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(61, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_5455", "net/minecraft/util/registry/DynamicRegistryManager", "net/minecraft/core/RegistryAccess"), map("method_30530", "get", "registryOrThrow"), "(" + map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;") + ")" + map("Lnet/minecraft/class_2378;", "Lnet/minecraft/util/registry/Registry;", "Lnet/minecraft/core/Registry;"), true);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("manager", map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("key", map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;"), map("Lnet/minecraft/class_5321", "Lnet/minecraft/util/registry/RegistryKey", "Lnet/minecraft/resources/ResourceKey") + "<+" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;>;", label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getBiome", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")" + map("Lnet/minecraft/class_1959;", "Lnet/minecraft/world/biome/Biome;", "Lnet/minecraft/world/level/biome/Biome;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(66, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1937", "net/minecraft/world/World", "net/minecraft/world/level/Level"), map("method_23753", "getBiome", "getBiome"), "(" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")" + map("Lnet/minecraft/class_6880;", "Lnet/minecraft/util/registry/RegistryEntry;", "Lnet/minecraft/core/Holder;"), false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_6880", "net/minecraft/util/registry/RegistryEntry", "net/minecraft/core/Holder"), map("comp_349", "value", "value"), "()Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_1959", "net/minecraft/world/biome/Biome", "net/minecraft/world/level/biome/Biome"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getBlockTags", "()Ljava/util/Map;", "()Ljava/util/Map<" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + "Ljava/lang/Object;>;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(71, label0);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat182", "blockTags", "Ljava/util/Map;");
Label label1 = new Label();
methodVisitor.visitJumpInsn(IFNONNULL, label1);
methodVisitor.visitLdcInsn(Type.getType(map("Lnet/minecraft/class_3481;", "Lnet/minecraft/tag/BlockTags;", "Lnet/minecraft/tags/BlockTags;")));
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getFields", "()[Ljava/lang/reflect/Field;", false);
methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "stream", "([Ljava/lang/Object;)Ljava/util/stream/Stream;", false);
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/Compat182", "lambda$getBlockTags$0", "(Ljava/lang/reflect/Field;)" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;"), false), Type.getType("(Ljava/lang/reflect/Field;)" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;"))});
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(72, label2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKEVIRTUAL, map("net/minecraft/class_6862", "net/minecraft/tag/TagKey", "net/minecraft/tags/TagKey"), map("comp_327", "id", "location"), "()" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), false), Type.getType("(" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;") + ")" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"))});
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/Compat182", "lambda$getBlockTags$1", "(" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;") + ")Ljava/lang/Object;", false), Type.getType("(" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;") + ")Ljava/lang/Object;")});
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(73, label3);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/google/common/collect/ImmutableMap", "toImmutableMap", "(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/util/stream/Collector;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, "java/util/Map");
methodVisitor.visitInsn(DUP);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat182", "blockTags", "Ljava/util/Map;");
Label label4 = new Label();
methodVisitor.visitJumpInsn(GOTO, label4);
methodVisitor.visitLabel(label1);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat182", "blockTags", "Ljava/util/Map;");
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(71, label4);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/util/Map"});
methodVisitor.visitInsn(ARETURN);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat182;", null, label0, label5, 0);
methodVisitor.visitMaxs(3, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$getBlockTags$1", "(" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;") + ")Ljava/lang/Object;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(73, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("tag", map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;"), null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$getBlockTags$0", "(Ljava/lang/reflect/Field;)" + map("Lnet/minecraft/class_6862;", "Lnet/minecraft/tag/TagKey;", "Lnet/minecraft/tags/TagKey;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(72, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/api/ReflectionHelper", "getFieldValue", "(Ljava/lang/reflect/Field;Ljava/lang/Object;)Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_6862", "net/minecraft/tag/TagKey", "net/minecraft/tags/TagKey"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("f", "Ljava/lang/reflect/Field;", null, label0, label1, 0);
methodVisitor.visitMaxs(2, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(20, label0);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat182", "registryEntryReference", "Ljava/lang/Class;");
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(21, label1);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat182", "blockTags", "Ljava/util/Map;");
methodVisitor.visitInsn(RETURN);
methodVisitor.visitMaxs(1, 0);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
