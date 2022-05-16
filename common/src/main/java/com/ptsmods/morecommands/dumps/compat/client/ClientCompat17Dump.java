package com.ptsmods.morecommands.dumps.compat.client;
import static com.ptsmods.morecommands.dumps.ASMDump.map
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

public class ClientCompat17Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/client/ClientCompat17", null, "com/ptsmods/morecommands/compat/client/ClientCompat16", null);

classWriter.visitSource("ClientCompat17.java", null);

classWriter.visitInnerClass(map("net/minecraft/class_293$class_5596", "net/minecraft/client/render/VertexFormat$DrawMode", "com/mojang/blaze3d/vertex/VertexFormat$Mode"), map("net/minecraft/class_293", "net/minecraft/client/render/VertexFormat", "com/mojang/blaze3d/vertex/VertexFormat"), map("class_5596", "DrawMode", "Mode"), ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(9, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/ptsmods/morecommands/compat/client/ClientCompat16", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat17;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "bufferBuilderBegin", "(" + map("Lnet/minecraft/class_287;", "Lnet/minecraft/client/render/BufferBuilder;", "Lcom/mojang/blaze3d/vertex/BufferBuilder;") + "I" + map("Lnet/minecraft/class_293;", "Lnet/minecraft/client/render/VertexFormat;", "Lcom/mojang/blaze3d/vertex/VertexFormat;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(13, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_293$class_5596", "net/minecraft/client/render/VertexFormat$DrawMode", "com/mojang/blaze3d/vertex/VertexFormat$Mode"), "values", "()[" + map("Lnet/minecraft/class_293$class_5596;", "Lnet/minecraft/client/render/VertexFormat$DrawMode;", "Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;"), false);
methodVisitor.visitVarInsn(ILOAD, 2);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_287", "net/minecraft/client/render/BufferBuilder", "com/mojang/blaze3d/vertex/BufferBuilder"), map("method_1328", "begin", "begin"), "(" + map("Lnet/minecraft/class_293$class_5596;", "Lnet/minecraft/client/render/VertexFormat$DrawMode;", "Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;") + map("Lnet/minecraft/class_293;", "Lnet/minecraft/client/render/VertexFormat;", "Lcom/mojang/blaze3d/vertex/VertexFormat;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(14, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat17;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("builder", map("Lnet/minecraft/class_287;", "Lnet/minecraft/client/render/BufferBuilder;", "Lcom/mojang/blaze3d/vertex/BufferBuilder;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("drawMode", "I", null, label0, label2, 2);
methodVisitor.visitLocalVariable("format", map("Lnet/minecraft/class_293;", "Lnet/minecraft/client/render/VertexFormat;", "Lcom/mojang/blaze3d/vertex/VertexFormat;"), null, label0, label2, 3);
methodVisitor.visitMaxs(3, 4);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getFrameCount", "(" + map("Lnet/minecraft/class_1058;", "Lnet/minecraft/client/texture/Sprite;", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;") + ")I", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(18, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1058", "net/minecraft/client/texture/Sprite", "net/minecraft/client/renderer/texture/TextureAtlasSprite"), map("method_33442", "getDistinctFrameCount", "getUniqueFrames"), "()Ljava/util/stream/IntStream;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/IntStream", "max", "()Ljava/util/OptionalInt;", true);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/OptionalInt", "orElse", "(I)I", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("sprite", map("Lnet/minecraft/class_1058;", "Lnet/minecraft/client/texture/Sprite;", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"), null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "bindTexture", "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(23, label0);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/mojang/blaze3d/systems/RenderSystem", "setShaderTexture", "(I" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(24, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat17;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label2, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
