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

public class ClientCompat19Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/client/ClientCompat19", null, "com/ptsmods/morecommands/compat/client/ClientCompat17", null);

classWriter.visitSource("ClientCompat19.java", null);

classWriter.visitInnerClass("dev/architectury/event/events/client/ClientChatEvent$Process", "dev/architectury/event/events/client/ClientChatEvent", "Process", ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);

classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(32, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/ptsmods/morecommands/compat/client/ClientCompat17", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getChatVisibility", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")" + map("Lnet/minecraft/class_1659;", "Lnet/minecraft/client/option/ChatVisibility;", "Lnet/minecraft/world/entity/player/ChatVisiblity;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(36, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("method_42539", "getChatVisibility", "chatVisibility"), "()" + map("Lnet/minecraft/class_7172;", "Lnet/minecraft/client/option/SimpleOption;", "Lnet/minecraft/client/OptionInstance;"), false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_7172", "net/minecraft/client/option/SimpleOption", "net/minecraft/client/OptionInstance"), map("method_41753", "getValue", "get"), "()Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_1659", "net/minecraft/client/option/ChatVisibility", "net/minecraft/world/entity/player/ChatVisiblity"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getChatLineSpacing", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")D", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(41, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("method_42546", "getChatLineSpacing", "chatLineSpacing"), "()" + map("Lnet/minecraft/class_7172;", "Lnet/minecraft/client/option/SimpleOption;", "Lnet/minecraft/client/OptionInstance;"), false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_7172", "net/minecraft/client/option/SimpleOption", "net/minecraft/client/OptionInstance"), map("method_41753", "getValue", "get"), "()Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
methodVisitor.visitInsn(DRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "interactBlock", "(" + map("Lnet/minecraft/class_636;", "Lnet/minecraft/client/network/ClientPlayerInteractionManager;", "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;") + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + map("Lnet/minecraft/class_638;", "Lnet/minecraft/client/world/ClientWorld;", "Lnet/minecraft/client/multiplayer/ClientLevel;") + map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;") + map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;") + ")" + map("Lnet/minecraft/class_1269;", "Lnet/minecraft/util/ActionResult;", "Lnet/minecraft/world/InteractionResult;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(46, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_636", "net/minecraft/client/network/ClientPlayerInteractionManager", "net/minecraft/client/multiplayer/MultiPlayerGameMode"), map("method_2896", "interactBlock", "useItemOn"), "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;") + map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;") + ")" + map("Lnet/minecraft/class_1269;", "Lnet/minecraft/util/ActionResult;", "Lnet/minecraft/world/InteractionResult;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("interactionManager", map("Lnet/minecraft/class_636;", "Lnet/minecraft/client/network/ClientPlayerInteractionManager;", "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_638;", "Lnet/minecraft/client/world/ClientWorld;", "Lnet/minecraft/client/multiplayer/ClientLevel;"), null, label0, label1, 3);
methodVisitor.visitLocalVariable("hand", map("Lnet/minecraft/class_1268;", "Lnet/minecraft/util/Hand;", "Lnet/minecraft/world/InteractionHand;"), null, label0, label1, 4);
methodVisitor.visitLocalVariable("hit", map("Lnet/minecraft/class_3965;", "Lnet/minecraft/util/hit/BlockHitResult;", "Lnet/minecraft/world/phys/BlockHitResult;"), null, label0, label1, 5);
methodVisitor.visitMaxs(4, 6);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getResourceStream", "(" + map("Lnet/minecraft/class_3300;", "Lnet/minecraft/resource/ResourceManager;", "Lnet/minecraft/server/packs/resources/ResourceManager;") + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Ljava/io/InputStream;", null, new String[] { "java/io/IOException" });
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(51, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_3300", "net/minecraft/resource/ResourceManager", "net/minecraft/server/packs/resources/ResourceManager"), map("method_14486", "getResource", "getResource"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Ljava/util/Optional;", true);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "orElse", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_3298", "net/minecraft/resource/Resource", "net/minecraft/server/packs/resources/Resource"));
methodVisitor.visitVarInsn(ASTORE, 3);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(52, label1);
methodVisitor.visitVarInsn(ALOAD, 3);
Label label2 = new Label();
methodVisitor.visitJumpInsn(IFNONNULL, label2);
methodVisitor.visitInsn(ACONST_NULL);
Label label3 = new Label();
methodVisitor.visitJumpInsn(GOTO, label3);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {map("net/minecraft/class_3298", "net/minecraft/resource/Resource", "net/minecraft/server/packs/resources/Resource")}, 0, null);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3298", "net/minecraft/resource/Resource", "net/minecraft/server/packs/resources/Resource"), map("method_14482", "getInputStream", "open"), "()Ljava/io/InputStream;", false);
methodVisitor.visitLabel(label3);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/io/InputStream"});
methodVisitor.visitInsn(ARETURN);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label4, 0);
methodVisitor.visitLocalVariable("manager", map("Lnet/minecraft/class_3300;", "Lnet/minecraft/resource/ResourceManager;", "Lnet/minecraft/server/packs/resources/ResourceManager;"), null, label0, label4, 1);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label4, 2);
methodVisitor.visitLocalVariable("res", map("Lnet/minecraft/class_3298;", "Lnet/minecraft/resource/Resource;", "Lnet/minecraft/server/packs/resources/Resource;"), null, label1, label4, 3);
methodVisitor.visitMaxs(2, 4);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getGamma", "(" + map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;") + ")D", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(57, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_315", "net/minecraft/client/option/GameOptions", "net/minecraft/client/Options"), map("method_42473", "getGamma", "gamma"), "()" + map("Lnet/minecraft/class_7172;", "Lnet/minecraft/client/option/SimpleOption;", "Lnet/minecraft/client/OptionInstance;"), false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_7172", "net/minecraft/client/option/SimpleOption", "net/minecraft/client/OptionInstance"), map("method_41753", "getValue", "get"), "()Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
methodVisitor.visitInsn(DRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("options", map("Lnet/minecraft/class_315;", "Lnet/minecraft/client/option/GameOptions;", "Lnet/minecraft/client/Options;"), null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newChatMessagePacket", "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + "Ljava/lang/String;Z)" + map("Lnet/minecraft/class_2596;", "Lnet/minecraft/network/Packet;", "Lnet/minecraft/network/protocol/Packet;"), "(" + map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;") + "Ljava/lang/String;Z)" + map("Lnet/minecraft/class_2596", "Lnet/minecraft/network/Packet", "Lnet/minecraft/network/protocol/Packet") + "<" + map("Lnet/minecraft/class_2792;", "Lnet/minecraft/network/listener/ServerPlayPacketListener;", "Lnet/minecraft/network/protocol/game/ServerGamePacketListener;") + ">;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(62, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_746", "net/minecraft/client/network/ClientPlayerEntity", "net/minecraft/client/player/LocalPlayer"), map("method_5667", "getUuid", "getUUID"), "()Ljava/util/UUID;", false);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_7470", "net/minecraft/network/encryption/ChatMessageSigner", "net/minecraft/network/chat/MessageSigner"), map("method_43866", "create", "create"), "(Ljava/util/UUID;)" + map("Lnet/minecraft/class_7470;", "Lnet/minecraft/network/encryption/ChatMessageSigner;", "Lnet/minecraft/network/chat/MessageSigner;"), false);
methodVisitor.visitVarInsn(ASTORE, 4);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(63, label1);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitTypeInsn(CHECKCAST, "com/ptsmods/morecommands/mixin/compat/compat19/plus/MixinClientPlayerEntityAccessor");
methodVisitor.visitVarInsn(ASTORE, 5);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(65, label2);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitLdcInsn("/");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
Label label3 = new Label();
methodVisitor.visitJumpInsn(IFNE, label3);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2797", "net/minecraft/network/packet/c2s/play/ChatMessageC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatPacket"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/api/util/text/LiteralTextBuilder", "builder", "(Ljava/lang/String;)Lcom/ptsmods/morecommands/api/util/text/LiteralTextBuilder;", true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/api/util/text/LiteralTextBuilder", "build", "()" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/mixin/compat/compat19/plus/MixinClientPlayerEntityAccessor", "callSignChatMessage", "(" + map("Lnet/minecraft/class_7470;", "Lnet/minecraft/network/encryption/ChatMessageSigner;", "Lnet/minecraft/network/chat/MessageSigner;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")" + map("Lnet/minecraft/class_7469;", "Lnet/minecraft/network/encryption/ChatMessageSignature;", "Lnet/minecraft/network/chat/MessageSignature;"), true);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2797", "net/minecraft/network/packet/c2s/play/ChatMessageC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatPacket"), "<init>", "(Ljava/lang/String;" + map("Lnet/minecraft/class_7469;", "Lnet/minecraft/network/encryption/ChatMessageSignature;", "Lnet/minecraft/network/chat/MessageSignature;") + "Z)V", false);
methodVisitor.visitInsn(ARETURN);
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(67, label3);
methodVisitor.visitFrame(Opcodes.F_APPEND,2, new Object[] {map("net/minecraft/class_7470", "net/minecraft/network/encryption/ChatMessageSigner", "net/minecraft/network/chat/MessageSigner"), "com/ptsmods/morecommands/mixin/compat/compat19/plus/MixinClientPlayerEntityAccessor"}, 0, null);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitInsn(ICONST_1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
methodVisitor.visitVarInsn(ASTORE, 2);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(68, label4);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_746", "net/minecraft/client/network/ClientPlayerEntity", "net/minecraft/client/player/LocalPlayer"), map("field_3944", "networkHandler", "connection"), map("Lnet/minecraft/class_634;", "Lnet/minecraft/client/network/ClientPlayNetworkHandler;", "Lnet/minecraft/client/multiplayer/ClientPacketListener;"));
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_634", "net/minecraft/client/network/ClientPlayNetworkHandler", "net/minecraft/client/multiplayer/ClientPacketListener"), map("method_2886", "getCommandDispatcher", "getCommands"), "()Lcom/mojang/brigadier/CommandDispatcher;", false);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_746", "net/minecraft/client/network/ClientPlayerEntity", "net/minecraft/client/player/LocalPlayer"), map("field_3944", "networkHandler", "connection"), map("Lnet/minecraft/class_634;", "Lnet/minecraft/client/network/ClientPlayNetworkHandler;", "Lnet/minecraft/client/multiplayer/ClientPacketListener;"));
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_634", "net/minecraft/client/network/ClientPlayNetworkHandler", "net/minecraft/client/multiplayer/ClientPacketListener"), map("method_2875", "getCommandSource", "getSuggestionsProvider"), "()" + map("Lnet/minecraft/class_637;", "Lnet/minecraft/client/network/ClientCommandSource;", "Lnet/minecraft/client/multiplayer/ClientSuggestionProvider;"), false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/brigadier/CommandDispatcher", "parse", "(Ljava/lang/String;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", false);
methodVisitor.visitVarInsn(ASTORE, 6);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLineNumber(69, label5);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 6);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/mixin/compat/compat19/plus/MixinClientPlayerEntityAccessor", "callSignArguments", "(" + map("Lnet/minecraft/class_7470;", "Lnet/minecraft/network/encryption/ChatMessageSigner;", "Lnet/minecraft/network/chat/MessageSigner;") + "Lcom/mojang/brigadier/ParseResults;)" + map("Lnet/minecraft/class_7450;", "Lnet/minecraft/network/encryption/ArgumentSignatures;", "Lnet/minecraft/commands/arguments/ArgumentSignatures;"), true);
methodVisitor.visitVarInsn(ASTORE, 7);
Label label6 = new Label();
methodVisitor.visitLabel(label6);
methodVisitor.visitLineNumber(70, label6);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_7472", "net/minecraft/network/packet/c2s/play/CommandExecutionC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatCommandPacket"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_7470", "net/minecraft/network/encryption/ChatMessageSigner", "net/minecraft/network/chat/MessageSigner"), map("comp_802", "timeStamp", "timeStamp"), "()Ljava/time/Instant;", false);
methodVisitor.visitVarInsn(ALOAD, 7);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_7472", "net/minecraft/network/packet/c2s/play/CommandExecutionC2SPacket", "net/minecraft/network/protocol/game/ServerboundChatCommandPacket"), "<init>", "(Ljava/lang/String;Ljava/time/Instant;" + map("Lnet/minecraft/class_7450;", "Lnet/minecraft/network/encryption/ArgumentSignatures;", "Lnet/minecraft/commands/arguments/ArgumentSignatures;") + ")V", false);
methodVisitor.visitInsn(ARETURN);
Label label7 = new Label();
methodVisitor.visitLabel(label7);
methodVisitor.visitLocalVariable("parseResults", "Lcom/mojang/brigadier/ParseResults;", "Lcom/mojang/brigadier/ParseResults<" + map("Lnet/minecraft/class_2172;", "Lnet/minecraft/command/CommandSource;", "Lnet/minecraft/commands/SharedSuggestionProvider;") + ">;", label5, label7, 6);
methodVisitor.visitLocalVariable("argumentSignatures", map("Lnet/minecraft/class_7450;", "Lnet/minecraft/network/encryption/ArgumentSignatures;", "Lnet/minecraft/commands/arguments/ArgumentSignatures;"), null, label6, label7, 7);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label7, 0);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_746;", "Lnet/minecraft/client/network/ClientPlayerEntity;", "Lnet/minecraft/client/player/LocalPlayer;"), null, label0, label7, 1);
methodVisitor.visitLocalVariable("message", "Ljava/lang/String;", null, label0, label7, 2);
methodVisitor.visitLocalVariable("forceChat", "Z", null, label0, label7, 3);
methodVisitor.visitLocalVariable("signer", map("Lnet/minecraft/class_7470;", "Lnet/minecraft/network/encryption/ChatMessageSigner;", "Lnet/minecraft/network/chat/MessageSigner;"), null, label1, label7, 4);
methodVisitor.visitLocalVariable("accessor", "Lcom/ptsmods/morecommands/mixin/compat/compat19/plus/MixinClientPlayerEntityAccessor;", null, label2, label7, 5);
methodVisitor.visitMaxs(6, 8);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "registerChatProcessListener", "(Ljava/util/function/Function;)V", "(Ljava/util/function/Function<" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ">;)V", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(76, label0);
methodVisitor.visitFieldInsn(GETSTATIC, "dev/architectury/event/events/client/ClientChatEvent", "PROCESS", "Ldev/architectury/event/Event;");
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitInvokeDynamicInsn("process", "(Ljava/util/function/Function;)Ldev/architectury/event/events/client/ClientChatEvent$Process;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(" + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_7436;", "Lnet/minecraft/network/MessageSender;", "Lnet/minecraft/network/chat/ChatSender;") + ")Ldev/architectury/event/CompoundEventResult;"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/client/ClientCompat19", "lambda$registerChatProcessListener$0", "(Ljava/util/function/Function;" + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_7436;", "Lnet/minecraft/network/MessageSender;", "Lnet/minecraft/network/chat/ChatSender;") + ")Ldev/architectury/event/CompoundEventResult;", false), Type.getType("(" + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_7436;", "Lnet/minecraft/network/MessageSender;", "Lnet/minecraft/network/chat/ChatSender;") + ")Ldev/architectury/event/CompoundEventResult;")});
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "dev/architectury/event/Event", "register", "(Ljava/lang/Object;)V", true);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(81, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/client/ClientCompat19;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("listener", "Ljava/util/function/Function;", "Ljava/util/function/Function<" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ">;", label0, label2, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$registerChatProcessListener$0", "(Ljava/util/function/Function;" + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_7436;", "Lnet/minecraft/network/MessageSender;", "Lnet/minecraft/network/chat/ChatSender;") + ")Ldev/architectury/event/CompoundEventResult;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(77, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_2561", "net/minecraft/text/Text", "net/minecraft/network/chat/Component"));
methodVisitor.visitVarInsn(ASTORE, 4);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(79, label1);
methodVisitor.visitVarInsn(ALOAD, 4);
Label label2 = new Label();
methodVisitor.visitJumpInsn(IFNULL, label2);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
Label label3 = new Label();
methodVisitor.visitJumpInsn(IFEQ, label3);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {map("net/minecraft/class_2561", "net/minecraft/text/Text", "net/minecraft/network/chat/Component")}, 0, null);
methodVisitor.visitMethodInsn(INVOKESTATIC, "dev/architectury/event/CompoundEventResult", "pass", "()Ldev/architectury/event/CompoundEventResult;", false);
Label label4 = new Label();
methodVisitor.visitJumpInsn(GOTO, label4);
methodVisitor.visitLabel(label3);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKESTATIC, "dev/architectury/event/CompoundEventResult", "interruptTrue", "(Ljava/lang/Object;)Ldev/architectury/event/CompoundEventResult;", false);
methodVisitor.visitLabel(label4);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"dev/architectury/event/CompoundEventResult"});
methodVisitor.visitInsn(ARETURN);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLocalVariable("listener", "Ljava/util/function/Function;", null, label0, label5, 0);
methodVisitor.visitLocalVariable("chatType", map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;"), null, label0, label5, 1);
methodVisitor.visitLocalVariable("message", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label0, label5, 2);
methodVisitor.visitLocalVariable("sender", map("Lnet/minecraft/class_7436;", "Lnet/minecraft/network/MessageSender;", "Lnet/minecraft/network/chat/ChatSender;"), null, label0, label5, 3);
methodVisitor.visitLocalVariable("output", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label1, label5, 4);
methodVisitor.visitMaxs(2, 5);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
