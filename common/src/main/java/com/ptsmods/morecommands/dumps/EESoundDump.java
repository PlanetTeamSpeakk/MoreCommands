package com.ptsmods.morecommands.dumps;

import com.ptsmods.morecommands.api.Version;
import org.objectweb.asm.*;
public class EESoundDump extends ASMDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/miscellaneous/EESound", null, map("net/minecraft/class_1101", "net/minecraft/client/sound/MovingSoundInstance", "net/minecraft/client/resources/sounds/AbstractTickableSoundInstance"), null);

classWriter.visitSource("EESound.java", null);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(15, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitFieldInsn(GETSTATIC, map("net/minecraft/class_2378", "net/minecraft/util/registry/Registry", "net/minecraft/core/Registry"), map("field_11156", "SOUND_EVENT", "SOUND_EVENT"), map("Lnet/minecraft/class_2378;", "Lnet/minecraft/util/registry/Registry;", "Lnet/minecraft/core/Registry;"));
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2960", "net/minecraft/util/Identifier", "net/minecraft/resources/ResourceLocation"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitLdcInsn("morecommands:ee");
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2960", "net/minecraft/util/Identifier", "net/minecraft/resources/ResourceLocation"), "<init>", "(Ljava/lang/String;)V", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2378", "net/minecraft/util/registry/Registry", "net/minecraft/core/Registry"), map("method_10223", "get", "get"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_3414", "net/minecraft/sound/SoundEvent", "net/minecraft/sounds/SoundEvent"));
methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_3414", "net/minecraft/sound/SoundEvent", "net/minecraft/sounds/SoundEvent"));
methodVisitor.visitFieldInsn(GETSTATIC, map("net/minecraft/class_3419", "net/minecraft/sound/SoundCategory", "net/minecraft/sounds/SoundSource"), map("field_15250", "MASTER", "MASTER"), map("Lnet/minecraft/class_3419;", "Lnet/minecraft/sound/SoundCategory;", "Lnet/minecraft/sounds/SoundSource;"));
if (Version.getCurrent().isNewerThanOrEqual(Version.V1_19)) {
    methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_5819", "net/minecraft/util/math/random/Random", "net/minecraft/util/RandomSource"), map("method_43053", "create", "createNewThreadLocalInstance"), "()L" + map("net/minecraft/class_5819", "net/minecraft/util/math/random/AbstractRandom", "net/minecraft/util/RandomSource") + ";", true);
    methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_1101", "net/minecraft/client/sound/MovingSoundInstance", "net/minecraft/client/resources/sounds/AbstractTickableSoundInstance"), "<init>", "(" + map("Lnet/minecraft/class_3414;", "Lnet/minecraft/sound/SoundEvent;", "Lnet/minecraft/sounds/SoundEvent;") + map("Lnet/minecraft/class_3419;", "Lnet/minecraft/sound/SoundCategory;", "Lnet/minecraft/sounds/SoundSource;") + map("Lnet/minecraft/class_5819;", "Lnet/minecraft/util/math/random/Random;", "Lnet/minecraft/util/RandomSource;") + ")V", false);
} else
    methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_1101", "net/minecraft/client/sound/MovingSoundInstance", "net/minecraft/client/resources/sounds/AbstractTickableSoundInstance"), "<init>", "(" + map("Lnet/minecraft/class_3414;", "Lnet/minecraft/sound/SoundEvent;", "Lnet/minecraft/sounds/SoundEvent;") + map("Lnet/minecraft/class_3419;", "Lnet/minecraft/sound/SoundCategory;", "Lnet/minecraft/sounds/SoundSource;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(16, label1);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitInsn(FCONST_0);
methodVisitor.visitFieldInsn(PUTFIELD, "com/ptsmods/morecommands/miscellaneous/EESound", "pitch", "F");
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(17, label2);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitFieldInsn(PUTFIELD, "com/ptsmods/morecommands/miscellaneous/EESound", "repeat", "Z");
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(18, label3);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/ptsmods/morecommands/miscellaneous/EESound", "tick", "()V", false);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(19, label4);
methodVisitor.visitInsn(RETURN);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/miscellaneous/EESound;", null, label0, label5, 0);
methodVisitor.visitMaxs(5, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "shouldAlwaysPlay", "()Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(23, label0);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/miscellaneous/EESound;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "canPlay", "()Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(28, label0);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/miscellaneous/EESound;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "tick", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(33, label0);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_310", "net/minecraft/client/MinecraftClient", "net/minecraft/client/Minecraft"), map("method_1551", "getInstance", "getInstance"), "()" + map("Lnet/minecraft/class_310;", "Lnet/minecraft/client/MinecraftClient;", "Lnet/minecraft/client/Minecraft;"), false);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_310", "net/minecraft/client/MinecraftClient", "net/minecraft/client/Minecraft"), map("field_1724", "player", "player"), map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;"));
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_746", "net/minecraft/client/network/ClientPlayerEntity", "net/minecraft/client/player/LocalPlayer"), map("method_19538", "getPos", "position"), "()" + map("Lnet/minecraft/class_243;", "Lnet/minecraft/util/math/Vec3d;", "Lnet/minecraft/world/phys/Vec3;"), false);
methodVisitor.visitVarInsn(ASTORE, 1);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(34, label1);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_243", "net/minecraft/util/math/Vec3d", "net/minecraft/world/phys/Vec3"), map("field_1352", "x", "x"), "D");
methodVisitor.visitFieldInsn(PUTFIELD, "com/ptsmods/morecommands/miscellaneous/EESound", "x", "D");
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(35, label2);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_243", "net/minecraft/util/math/Vec3d", "net/minecraft/world/phys/Vec3"), map("field_1351", "y", "y"), "D");
methodVisitor.visitFieldInsn(PUTFIELD, "com/ptsmods/morecommands/miscellaneous/EESound", "y", "D");
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(36, label3);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_243", "net/minecraft/util/math/Vec3d", "net/minecraft/world/phys/Vec3"), map("field_1350", "z", "z"), "D");
methodVisitor.visitFieldInsn(PUTFIELD, "com/ptsmods/morecommands/miscellaneous/EESound", "z", "D");
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(37, label4);
methodVisitor.visitInsn(RETURN);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/miscellaneous/EESound;", null, label0, label5, 0);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_243;", "Lnet/minecraft/util/math/Vec3d;", "Lnet/minecraft/world/phys/Vec3;"), null, label1, label5, 1);
methodVisitor.visitMaxs(3, 2);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
