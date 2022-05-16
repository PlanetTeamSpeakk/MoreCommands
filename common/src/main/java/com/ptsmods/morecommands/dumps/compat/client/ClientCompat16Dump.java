package com.ptsmods.morecommands.dumps.compat.client;
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

public class ClientCompat16Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/client/ClientCompat16", null, "java/lang/Object", new String[] { "com/ptsmods/morecommands/api/util/compat/client/ClientCompat" });

classWriter.visitSource("ClientCompat16.java", null);

classWriter.visitInnerClass("dev/architectury/event/events/client/ClientChatEvent$Process", "dev/architectury/event/events/client/ClientChatEvent", "Process", ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);

classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(31, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "bufferBuilderBegin", "(" + map("Lnet/minecraft/class_287;", "Lnet/minecraft/client/render/BufferBuilder;", "Lcom/mojang/blaze3d/vertex/BufferBuilder;") + "I" + map("Lnet/minecraft/class_293;", "Lnet/minecraft/client/render/VertexFormat;", "Lcom/mojang/blaze3d/vertex/VertexFormat;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(35, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ILOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_287", "net/minecraft/client/render/BufferBuilder", "com/mojang/blaze3d/vertex/BufferBuilder"), map("method_1328", "begin", "begin"), "(I" + map("Lnet/minecraft/class_293;", "Lnet/minecraft/client/render/VertexFormat;", "Lcom/mojang/blaze3d/vertex/VertexFormat;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(36, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label2, 0);
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
methodVisitor.visitLineNumber(40, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1058", "net/minecraft/client/texture/Sprite", "net/minecraft/client/renderer/texture/TextureAtlasSprite"), map("method_4592", "getFrameCount", "getFrameCount"), "()I", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("sprite", map("Lnet/minecraft/class_1058;", "Lnet/minecraft/client/texture/Sprite;", "Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "bindTexture", "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(45, label0);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_310", "net/minecraft/client/MinecraftClient", "net/minecraft/client/Minecraft"), map("method_1551", "getInstance", "getInstance"), "()" + map("Lnet/minecraft/class_310;", "Lnet/minecraft/client/MinecraftClient;", "Lnet/minecraft/client/Minecraft;"), false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_310", "net/minecraft/client/MinecraftClient", "net/minecraft/client/Minecraft"), map("method_1531", "getTextureManager", "getTextureManager"), "()" + map("Lnet/minecraft/class_1060;", "Lnet/minecraft/client/texture/TextureManager;", "Lnet/minecraft/client/renderer/texture/TextureManager;"), false);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1060", "net/minecraft/client/texture/TextureManager", "net/minecraft/client/renderer/texture/TextureManager"), map("method_22813", "bindTexture", "bind"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(46, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label2, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getChatVisibility", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")" + map("Lnet/minecraft/class_1659;", "Lnet/minecraft/client/option/ChatVisibility;", "Lnet/minecraft/world/entity/player/ChatVisiblity;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(50, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("field_1877", "chatVisibility", "chatVisibility"), map("Lnet/minecraft/class_1659;", "Lnet/minecraft/client/option/ChatVisibility;", "Lnet/minecraft/world/entity/player/ChatVisiblity;"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getChatLineSpacing", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")D", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(55, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("field_23932", "chatLineSpacing", "chatLineSpacing"), "D");
methodVisitor.visitInsn(DRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "interactBlock", "(" + map("Lnet/minecraft/class_636;", "Lnet/minecraft/client/network/ClientPlayerInteractionManager;", "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;") + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + map("Lnet/minecraft/class_638;", "Lnet/minecraft/client/world/ClientWorld;", "Lnet/minecraft/client/multiplayer/ClientLevel;") + map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;") + map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;") + ")" + map("Lnet/minecraft/class_1269;", "Lnet/minecraft/util/ActionResult;", "Lnet/minecraft/world/InteractionResult;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(60, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_636", "net/minecraft/client/network/ClientPlayerInteractionManager", "net/minecraft/client/multiplayer/MultiPlayerGameMode"), map("method_2896", "interactBlock", "useItemOn"), "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + map("Lnet/minecraft/class_638;", "Lnet/minecraft/client/world/ClientWorld;", "Lnet/minecraft/client/multiplayer/ClientLevel;") + map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;") + map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;") + ")" + map("Lnet/minecraft/class_1269;", "Lnet/minecraft/util/ActionResult;", "Lnet/minecraft/world/InteractionResult;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("interactionManager", map("Lnet/minecraft/class_636;", "Lnet/minecraft/client/network/ClientPlayerInteractionManager;", "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_638;", "Lnet/minecraft/client/world/ClientWorld;", "Lnet/minecraft/client/multiplayer/ClientLevel;"), null, label0, label1, 3);
methodVisitor.visitLocalVariable("hand", map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;"), null, label0, label1, 4);
methodVisitor.visitLocalVariable("hit", map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;"), null, label0, label1, 5);
methodVisitor.visitMaxs(5, 6);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getResourceStream", "(" + map("Lnet/minecraft/class_3300;", "Lnet/minecraft/resource/ResourceManager;", "Lnet/minecraft/server/packs/resources/ResourceManager;") + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Ljava/io/InputStream;", null, new String[] { "java/io/IOException" });
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(65, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_3300", "net/minecraft/resource/ResourceManager", "net/minecraft/server/packs/resources/ResourceManager"), map("method_14486", "getResource", "getResource"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")" + map("Lnet/minecraft/class_3298;", "Lnet/minecraft/resource/Resource;", "Lnet/minecraft/server/packs/resources/Resource;"), true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_3298", "net/minecraft/resource/Resource", "net/minecraft/server/packs/resources/Resource"), map("method_14482", "getInputStream", "getInputStream"), "()Ljava/io/InputStream;", true);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("manager", map("Lnet/minecraft/class_3300;", "Lnet/minecraft/resource/ResourceManager;", "Lnet/minecraft/server/packs/resources/ResourceManager;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getGamma", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")D", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(70, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("field_1840", "gamma", "gamma"), "D");
methodVisitor.visitInsn(DRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newChatMessagePacket", "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + "Ljava/lang/String;Z)" + map("Lnet/minecraft/class_2596;", "Lnet/minecraft/network/Packet;", "Lnet/minecraft/network/protocol/Packet;"), "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + "Ljava/lang/String;Z)" + map("Lnet/minecraft/class_2596", "Lnet/minecraft/network/Packet", "Lnet/minecraft/network/protocol/Packet") + "<" + map("Lnet/minecraft/class_2792;", "Lnet/minecraft/network/listener/ServerPlayPacketListener;", "Lnet/minecraft/network/protocol/game/ServerGamePacketListener;") + ">;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(75, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2797", "net/minecraft/network/packet/c2s/play/ChatMessageC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatPacket"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2797", "net/minecraft/network/packet/c2s/play/ChatMessageC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatPacket"), "<init>", "(Ljava/lang/String;)V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("message", "Ljava/lang/String;", null, label0, label1, 2);
methodVisitor.visitLocalVariable("forceChat", "Z", null, label0, label1, 3);
methodVisitor.visitMaxs(3, 4);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "registerChatProcessListener", "(Ljava/util/function/Function;)V", "(Ljava/util/function/Function<" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ">;)V", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(80, label0);
methodVisitor.visitFieldInsn(GETSTATIC, "dev/architectury/event/events/client/ClientChatEvent", "PROCESS", "Ldev/architectury/event/Event;");
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitInvokeDynamicInsn("process", "(Ljava/util/function/Function;)Ldev/architectury/event/events/client/ClientChatEvent$Process;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/String;)Ldev/architectury/event/CompoundEventResult;"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/client/ClientCompat16", "lambda$registerChatProcessListener$0", "(Ljava/util/function/Function;Ljava/lang/String;)Ldev/architectury/event/CompoundEventResult;", false), Type.getType("(Ljava/lang/String;)Ldev/architectury/event/CompoundEventResult;")});
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "dev/architectury/event/Event", "register", "(Ljava/lang/Object;)V", true);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(87, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("listener", "Ljava/util/function/Function;", "Ljava/util/function/Function<" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ">;", label0, label2, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$registerChatProcessListener$0", "(Ljava/util/function/Function;Ljava/lang/String;)Ldev/architectury/event/CompoundEventResult;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(81, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/api/util/text/LiteralTextBuilder", "builder", "(Ljava/lang/String;)Lcom/ptsmods/morecommands/api/util/text/LiteralTextBuilder;", true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/api/util/text/LiteralTextBuilder", "build", "()" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), true);
methodVisitor.visitVarInsn(ASTORE, 2);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(82, label1);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_2561", "net/minecraft/text/Text", "net/minecraft/network/chat/Component"));
methodVisitor.visitVarInsn(ASTORE, 3);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(84, label2);
methodVisitor.visitVarInsn(ALOAD, 3);
Label label3 = new Label();
methodVisitor.visitJumpInsn(IFNULL, label3);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
Label label4 = new Label();
methodVisitor.visitJumpInsn(IFEQ, label4);
methodVisitor.visitLabel(label3);
methodVisitor.visitFrame(Opcodes.F_APPEND,2, new Object[] {map("net/minecraft/class_2561", "net/minecraft/text/Text", "net/minecraft/network/chat/Component"), map("net/minecraft/class_2561", "net/minecraft/text/Text", "net/minecraft/network/chat/Component")}, 0, null);
methodVisitor.visitMethodInsn(INVOKESTATIC, "dev/architectury/event/CompoundEventResult", "pass", "()Ldev/architectury/event/CompoundEventResult;", false);
Label label5 = new Label();
methodVisitor.visitJumpInsn(GOTO, label5);
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(85, label4);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/api/IMoreCommands", "get", "()Lcom/ptsmods/morecommands/api/IMoreCommands;", true);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/api/IMoreCommands", "textToString", "(" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2583;", "Lnet/minecraft/text/Style;", "Lnet/minecraft/network/chat/Style;") + "Z)Ljava/lang/String;", true);
methodVisitor.visitMethodInsn(INVOKESTATIC, "dev/architectury/event/CompoundEventResult", "interruptTrue", "(Ljava/lang/Object;)Ldev/architectury/event/CompoundEventResult;", false);
methodVisitor.visitLabel(label5);
methodVisitor.visitLineNumber(84, label5);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"dev/architectury/event/CompoundEventResult"});
methodVisitor.visitInsn(ARETURN);
Label label6 = new Label();
methodVisitor.visitLabel(label6);
methodVisitor.visitLocalVariable("listener", "Ljava/util/function/Function;", null, label0, label6, 0);
methodVisitor.visitLocalVariable("message", "Ljava/lang/String;", null, label0, label6, 1);
methodVisitor.visitLocalVariable("input", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label1, label6, 2);
methodVisitor.visitLocalVariable("output", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label2, label6, 3);
methodVisitor.visitMaxs(4, 4);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
