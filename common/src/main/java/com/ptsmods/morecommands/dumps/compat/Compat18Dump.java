package com.ptsmods.morecommands.dumps.compat;
import static com.ptsmods.morecommands.dumps.ASMDump.map
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

public class Compat18Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/Compat18", null, "com/ptsmods/morecommands/compat/Compat17", null);

classWriter.visitSource("Compat18.java", null);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(12, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/ptsmods/morecommands/compat/Compat17", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat18;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeSpawnerLogicNbt", "(" + map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;") + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(15, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1917", "net/minecraft/world/MobSpawnerLogic", "net/minecraft/world/level/BaseSpawner"), map("method_8272", "writeNbt", "save"), "(" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat18;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("logic", map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 3);
methodVisitor.visitLocalVariable("nbt", map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, label0, label1, 4);
methodVisitor.visitMaxs(2, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeBENBT", "(" + map("Lnet/minecraft/class_2586;", "Lnet/minecraft/block/entity/BlockEntity;", "Lnet/minecraft/world/level/block/entity/BlockEntity;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(20, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2586", "net/minecraft/block/entity/BlockEntity", "net/minecraft/world/level/block/entity/BlockEntity"), map("method_38242", "createNbtWithIdentifyingData", "saveWithFullMetadata"), "()" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat18;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("be", map("Lnet/minecraft/class_2586;", "Lnet/minecraft/block/entity/BlockEntity;", "Lnet/minecraft/world/level/block/entity/BlockEntity;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "doubleStream", "(Lit/unimi/dsi/fastutil/doubles/DoubleList;)Ljava/util/stream/DoubleStream;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(25, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "it/unimi/dsi/fastutil/doubles/DoubleList", "doubleStream", "()Ljava/util/stream/DoubleStream;", true);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat18;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("doubles", "Lit/unimi/dsi/fastutil/doubles/DoubleList;", null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
