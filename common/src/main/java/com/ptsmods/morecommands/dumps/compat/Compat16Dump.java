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

public class Compat16Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/Compat16", null, "java/lang/Object", new String[] { "com/ptsmods/morecommands/api/util/compat/Compat" });

classWriter.visitSource("Compat16.java", null);

classWriter.visitInnerClass(map("net/minecraft/class_2703$class_2704", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action"), map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"), map("class_2704", "Action", "Action"), ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

classWriter.visitInnerClass("java/util/Map$Entry", "java/util/Map", "Entry", ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);

classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "blockTags", "Ljava/util/Map;", "Ljava/util/Map<" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + "Ljava/lang/Object;>;", null);
fieldVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(61, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "isRemoved", "(" + map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;") + ")Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(66, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("field_5988", "removed", "removed"), "Z");
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("entity", map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setRemoved", "(" + map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;") + "I)V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(71, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ILOAD, 2);
Label label1 = new Label();
methodVisitor.visitJumpInsn(IFLT, label1);
methodVisitor.visitInsn(ICONST_1);
Label label2 = new Label();
methodVisitor.visitJumpInsn(GOTO, label2);
methodVisitor.visitLabel(label1);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity")});
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_FULL, 3, new Object[] {"com/ptsmods/morecommands/compat/Compat16", map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), Opcodes.INTEGER}, 2, new Object[] {map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), Opcodes.INTEGER});
methodVisitor.visitFieldInsn(PUTFIELD, map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("field_5988", "removed", "removed"), "Z");
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(72, label3);
methodVisitor.visitInsn(RETURN);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label4, 0);
methodVisitor.visitLocalVariable("entity", map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;"), null, label0, label4, 1);
methodVisitor.visitLocalVariable("reason", "I", null, label0, label4, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getInventory", "(" + map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;") + ")" + map("Lnet/minecraft/class_1661;", "Lnet/minecraft/entity/player/PlayerInventory;", "Lnet/minecraft/world/entity/player/Inventory;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(76, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_1657", "net/minecraft/entity/player/PlayerEntity", "net/minecraft/world/entity/player/Player"), map("field_7514", "inventory", "inventory"), map("Lnet/minecraft/class_1661;", "Lnet/minecraft/entity/player/PlayerInventory;", "Lnet/minecraft/world/entity/player/Inventory;"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "isInBuildLimit", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(81, label0);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_1937", "net/minecraft/world/World", "net/minecraft/world/level/Level"), map("method_24794", "isInBuildLimit", "isInWorldBounds"), "(" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")Z", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 2);
methodVisitor.visitMaxs(1, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "toText", "(" + map("Lnet/minecraft/class_2520;", "Lnet/minecraft/nbt/NbtElement;", "Lnet/minecraft/nbt/Tag;") + ")" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(86, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_2520", "net/minecraft/nbt/NbtElement", "net/minecraft/nbt/Tag"), map("method_10715", "toText", "getPrettyDisplay"), "()" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), true);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("tag", map("Lnet/minecraft/class_2520;", "Lnet/minecraft/nbt/NbtElement;", "Lnet/minecraft/nbt/Tag;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newServerPlayerEntity", "(" + map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + "Lcom/mojang/authlib/GameProfile;)" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(91, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_3225", "net/minecraft/server/network/ServerPlayerInteractionManager", "net/minecraft/server/level/ServerPlayerGameMode"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_3225", "net/minecraft/server/network/ServerPlayerInteractionManager", "net/minecraft/server/level/ServerPlayerGameMode"), "<init>", "(" + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + ")V", false);
methodVisitor.visitVarInsn(ASTORE, 4);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(92, label1);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"), "<init>", "(" + map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + "Lcom/mojang/authlib/GameProfile;" + map("Lnet/minecraft/class_3225;", "Lnet/minecraft/server/network/ServerPlayerInteractionManager;", "Lnet/minecraft/server/level/ServerPlayerGameMode;") + ")V", false);
methodVisitor.visitVarInsn(ASTORE, 5);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(93, label2);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitFieldInsn(PUTFIELD, map("net/minecraft/class_3225", "net/minecraft/server/network/ServerPlayerInteractionManager", "net/minecraft/server/level/ServerPlayerGameMode"), map("field_14008", "player", "player"), map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"));
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(94, label3);
methodVisitor.visitVarInsn(ALOAD, 5);
methodVisitor.visitInsn(ARETURN);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label4, 0);
methodVisitor.visitLocalVariable("server", map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;"), null, label0, label4, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;"), null, label0, label4, 2);
methodVisitor.visitLocalVariable("profile", "Lcom/mojang/authlib/GameProfile;", null, label0, label4, 3);
methodVisitor.visitLocalVariable("interactionManager", map("Lnet/minecraft/class_3225;", "Lnet/minecraft/server/network/ServerPlayerInteractionManager;", "Lnet/minecraft/server/level/ServerPlayerGameMode;"), null, label1, label4, 4);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, label2, label4, 5);
methodVisitor.visitMaxs(6, 6);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeSpawnerLogicNbt", "(" + map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;") + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(99, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1917", "net/minecraft/world/MobSpawnerLogic", "net/minecraft/world/level/BaseSpawner"), map("method_8272", "toTag", "save"), "(" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("logic", map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 3);
methodVisitor.visitLocalVariable("nbt", map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, label0, label1, 4);
methodVisitor.visitMaxs(2, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "readSpawnerLogicNbt", "(" + map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;") + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(104, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1917", "net/minecraft/world/MobSpawnerLogic", "net/minecraft/world/level/BaseSpawner"), map("method_8280", "fromTag", "load"), "(" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(105, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("logic", map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label2, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label2, 3);
methodVisitor.visitLocalVariable("nbt", map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, label0, label2, 4);
methodVisitor.visitMaxs(2, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setSignEditor", "(" + map("Lnet/minecraft/class_2625;", "Lnet/minecraft/block/entity/SignBlockEntity;", "Lnet/minecraft/world/level/block/entity/SignBlockEntity;") + map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(109, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2625", "net/minecraft/block/entity/SignBlockEntity", "net/minecraft/world/level/block/entity/SignBlockEntity"), map("method_11306", "setEditor", "setAllowedPlayerEditor"), "(" + map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(110, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("sbe", map("Lnet/minecraft/class_2625;", "Lnet/minecraft/block/entity/SignBlockEntity;", "Lnet/minecraft/world/level/block/entity/SignBlockEntity;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;"), null, label0, label2, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getRegistry", "(" + map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;") + map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;") + ")" + map("Lnet/minecraft/class_2378;", "Lnet/minecraft/util/registry/Registry;", "Lnet/minecraft/core/Registry;"), "<E:Ljava/lang/Object;>(" + map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;") + map("Lnet/minecraft/class_5321", "Lnet/minecraft/util/registry/RegistryKey", "Lnet/minecraft/resources/ResourceKey") + "<+" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;>;)" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(114, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_5455", "net/minecraft/util/registry/DynamicRegistryManager", "net/minecraft/core/RegistryAccess"), map("method_30530", "get", "registryOrThrow"), "(" + map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;") + ")" + map("Lnet/minecraft/class_2385;", "Lnet/minecraft/util/registry/MutableRegistry;", "Lnet/minecraft/core/WritableRegistry;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("manager", map("Lnet/minecraft/class_5455;", "Lnet/minecraft/util/registry/DynamicRegistryManager;", "Lnet/minecraft/core/RegistryAccess;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("key", map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;"), map("Lnet/minecraft/class_5321", "Lnet/minecraft/util/registry/RegistryKey", "Lnet/minecraft/resources/ResourceKey") + "<+" + map("Lnet/minecraft/class_2378", "Lnet/minecraft/util/registry/Registry", "Lnet/minecraft/core/Registry") + "<TE;>;>;", label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getWorldHeight", "(" + map("Lnet/minecraft/class_1922;", "Lnet/minecraft/world/BlockView;", "Lnet/minecraft/world/level/BlockGetter;") + ")I", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(119, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_1922", "net/minecraft/world/BlockView", "net/minecraft/world/level/BlockGetter"), map("method_8322", "getHeight", "getMaxBuildHeight"), "()I", true);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1922;", "Lnet/minecraft/world/BlockView;", "Lnet/minecraft/world/level/BlockGetter;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newFireballEntity", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;") + "DDDI)" + map("Lnet/minecraft/class_1674;", "Lnet/minecraft/entity/projectile/FireballEntity;", "Lnet/minecraft/world/entity/projectile/LargeFireball;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(124, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_1674", "net/minecraft/entity/projectile/FireballEntity", "net/minecraft/world/entity/projectile/LargeFireball"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(DLOAD, 3);
methodVisitor.visitVarInsn(DLOAD, 5);
methodVisitor.visitVarInsn(DLOAD, 7);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_1674", "net/minecraft/entity/projectile/FireballEntity", "net/minecraft/world/entity/projectile/LargeFireball"), "<init>", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;") + "DDD)V", false);
methodVisitor.visitVarInsn(ASTORE, 10);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(125, label1);
methodVisitor.visitVarInsn(ALOAD, 10);
methodVisitor.visitVarInsn(ILOAD, 9);
methodVisitor.visitFieldInsn(PUTFIELD, map("net/minecraft/class_1674", "net/minecraft/entity/projectile/FireballEntity", "net/minecraft/world/entity/projectile/LargeFireball"), map("field_7624", "explosionPower", "explosionPower"), "I");
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(126, label2);
methodVisitor.visitVarInsn(ALOAD, 10);
methodVisitor.visitInsn(ARETURN);
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label3, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label3, 1);
methodVisitor.visitLocalVariable("owner", map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;"), null, label0, label3, 2);
methodVisitor.visitLocalVariable("velocityX", "D", null, label0, label3, 3);
methodVisitor.visitLocalVariable("velocityY", "D", null, label0, label3, 5);
methodVisitor.visitLocalVariable("velocityZ", "D", null, label0, label3, 7);
methodVisitor.visitLocalVariable("explosionPower", "I", null, label0, label3, 9);
methodVisitor.visitLocalVariable("fireball", map("Lnet/minecraft/class_1674;", "Lnet/minecraft/entity/projectile/FireballEntity;", "Lnet/minecraft/world/entity/projectile/LargeFireball;"), null, label1, label3, 10);
methodVisitor.visitMaxs(10, 11);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getProcessorString", "()Ljava/lang/String;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(131, label0);
methodVisitor.visitTypeInsn(NEW, "oshi/SystemInfo");
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "oshi/SystemInfo", "<init>", "()V", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "oshi/SystemInfo", "getHardware", "()Loshi/hardware/HardwareAbstractionLayer;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "oshi/hardware/HardwareAbstractionLayer", "getProcessors", "()[Loshi/hardware/Processor;", true);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitMaxs(2, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "registryContainsId", "(" + map("Lnet/minecraft/class_2370;", "Lnet/minecraft/util/registry/SimpleRegistry;", "Lnet/minecraft/core/MappedRegistry;") + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Z", "<T:Ljava/lang/Object;>(" + map("Lnet/minecraft/class_2370", "Lnet/minecraft/util/registry/SimpleRegistry", "Lnet/minecraft/core/MappedRegistry") + "<TT;>;" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Z", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(136, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2370", "net/minecraft/util/registry/SimpleRegistry", "net/minecraft/core/MappedRegistry"), map("method_10223", "get", "get"), "(" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ")Ljava/lang/Object;", false);
Label label1 = new Label();
methodVisitor.visitJumpInsn(IFNULL, label1);
methodVisitor.visitInsn(ICONST_1);
Label label2 = new Label();
methodVisitor.visitJumpInsn(GOTO, label2);
methodVisitor.visitLabel(label1);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitInsn(ICONST_0);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
methodVisitor.visitInsn(IRETURN);
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label3, 0);
methodVisitor.visitLocalVariable("registry", map("Lnet/minecraft/class_2370;", "Lnet/minecraft/util/registry/SimpleRegistry;", "Lnet/minecraft/core/MappedRegistry;"), map("Lnet/minecraft/class_2370", "Lnet/minecraft/util/registry/SimpleRegistry", "Lnet/minecraft/core/MappedRegistry") + "<TT;>;", label0, label3, 1);
methodVisitor.visitLocalVariable("id", map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"), null, label0, label3, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "playerSetWorld", "(" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(141, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"), map("method_5866", "setWorld", "setLevel"), "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(142, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;"), null, label0, label2, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_VARARGS, "newPlayerListS2CPacket", "(I[" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;") + ")" + map("Lnet/minecraft/class_2703;", "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoPacket;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(146, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2703$class_2704", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action"), "values", "()[" + map("Lnet/minecraft/class_2703$class_2704;", "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action;"), false);
methodVisitor.visitVarInsn(ILOAD, 1);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"), "<init>", "(" + map("Lnet/minecraft/class_2703$class_2704;", "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action;") + "[" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;") + ")V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("action", "I", null, label0, label1, 1);
methodVisitor.visitLocalVariable("players", "[" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, label0, label1, 2);
methodVisitor.visitMaxs(4, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeBENBT", "(" + map("Lnet/minecraft/class_2586;", "Lnet/minecraft/block/entity/BlockEntity;", "Lnet/minecraft/world/level/block/entity/BlockEntity;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(151, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2487", "net/minecraft/nbt/NbtCompound", "net/minecraft/nbt/CompoundTag"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2487", "net/minecraft/nbt/NbtCompound", "net/minecraft/nbt/CompoundTag"), "<init>", "()V", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2586", "net/minecraft/block/entity/BlockEntity", "net/minecraft/world/level/block/entity/BlockEntity"), map("method_11007", "writeNbt", "save"), "(" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("be", map("Lnet/minecraft/class_2586;", "Lnet/minecraft/block/entity/BlockEntity;", "Lnet/minecraft/world/level/block/entity/BlockEntity;"), null, label0, label1, 1);
methodVisitor.visitMaxs(3, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "registerArgumentType", "(Ldev/architectury/registry/registries/DeferredRegister;Ljava/lang/String;Ljava/lang/Class;Lcom/ptsmods/morecommands/api/arguments/ArgumentTypeSerialiser;)V", "<A::Lcom/ptsmods/morecommands/api/arguments/CompatArgumentType<TA;TT;TP;>;T:Ljava/lang/Object;P::Lcom/ptsmods/morecommands/api/arguments/ArgumentTypeProperties<TA;TT;TP;>;>(Ldev/architectury/registry/registries/DeferredRegister<*>;Ljava/lang/String;Ljava/lang/Class<TA;>;Lcom/ptsmods/morecommands/api/arguments/ArgumentTypeSerialiser<TA;TT;TP;>;)V", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(158, label0);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/ptsmods/morecommands/api/arguments/ArgumentTypeSerialiser", "toLegacyVanillaSerialiser", "()Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_2314", "net/minecraft/command/argument/serialize/ArgumentSerializer", "net/minecraft/commands/synchronization/ArgumentSerializer"));
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2316", "net/minecraft/command/argument/ArgumentTypes", "net/minecraft/commands/synchronization/ArgumentTypes"), map("method_10017", "register", "register"), "(Ljava/lang/String;Ljava/lang/Class;" + map("Lnet/minecraft/class_2314;", "Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", "Lnet/minecraft/commands/synchronization/ArgumentSerializer;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(159, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("registry", "Ldev/architectury/registry/registries/DeferredRegister;", "Ldev/architectury/registry/registries/DeferredRegister<*>;", label0, label2, 1);
methodVisitor.visitLocalVariable("identifier", "Ljava/lang/String;", null, label0, label2, 2);
methodVisitor.visitLocalVariable("clazz", "Ljava/lang/Class;", "Ljava/lang/Class<TA;>;", label0, label2, 3);
methodVisitor.visitLocalVariable("serialiser", "Lcom/ptsmods/morecommands/api/arguments/ArgumentTypeSerialiser;", "Lcom/ptsmods/morecommands/api/arguments/ArgumentTypeSerialiser<TA;TT;TP;>;", label0, label2, 4);
methodVisitor.visitMaxs(3, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "tagContains", "(Ljava/lang/Object;Ljava/lang/Object;)Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(164, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_3494", "net/minecraft/tag/Tag", "net/minecraft/tags/Tag"));
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_3494", "net/minecraft/tag/Tag", "net/minecraft/tags/Tag"), map("method_15141", "contains", "contains"), "(Ljava/lang/Object;)Z", true);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("tag", "Ljava/lang/Object;", null, label0, label1, 1);
methodVisitor.visitLocalVariable("obj", "Ljava/lang/Object;", null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getBiome", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")" + map("Lnet/minecraft/class_1959;", "Lnet/minecraft/world/biome/Biome;", "Lnet/minecraft/world/level/biome/Biome;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(169, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1937", "net/minecraft/world/World", "net/minecraft/world/level/Level"), map("method_23753", "getBiome", "getBiome"), "(" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")" + map("Lnet/minecraft/class_1959;", "Lnet/minecraft/world/biome/Biome;", "Lnet/minecraft/world/level/biome/Biome;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "createBlockStateArgumentType", "()" + map("Lnet/minecraft/class_2257;", "Lnet/minecraft/command/argument/BlockStateArgumentType;", "Lnet/minecraft/commands/arguments/blocks/BlockStateArgument;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(174, label0);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2257", "net/minecraft/command/argument/BlockStateArgumentType", "net/minecraft/commands/arguments/blocks/BlockStateArgument"), map("method_9653", "blockState", "block"), "()" + map("Lnet/minecraft/class_2257;", "Lnet/minecraft/command/argument/BlockStateArgumentType;", "Lnet/minecraft/commands/arguments/blocks/BlockStateArgument;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "randomDirection", "()" + map("Lnet/minecraft/class_2350;", "Lnet/minecraft/util/math/Direction;", "Lnet/minecraft/core/Direction;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(179, label0);
methodVisitor.visitTypeInsn(NEW, "java/util/Random");
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Random", "<init>", "()V", false);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2350", "net/minecraft/util/math/Direction", "net/minecraft/core/Direction"), map("method_10162", "random", "getRandom"), "(Ljava/util/Random;)" + map("Lnet/minecraft/class_2350;", "Lnet/minecraft/util/math/Direction;", "Lnet/minecraft/core/Direction;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitMaxs(2, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getBlockTags", "()Ljava/util/Map;", "()Ljava/util/Map<" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + "Ljava/lang/Object;>;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(185, label0);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat16", "blockTags", "Ljava/util/Map;");
Label label1 = new Label();
methodVisitor.visitJumpInsn(IFNONNULL, label1);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_3481", "net/minecraft/tag/BlockTags", "net/minecraft/tags/BlockTags"), map("method_15073", "getTagGroup", "getAllTags"), "()" + map("Lnet/minecraft/class_5414;", "Lnet/minecraft/tag/TagGroup;", "Lnet/minecraft/tags/TagCollection;"), false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_5414", "net/minecraft/tag/TagGroup", "net/minecraft/tags/TagCollection"), map("method_30204", "getTags", "getAllTags"), "()Ljava/util/Map;", true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;", true);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "stream", "()Ljava/util/stream/Stream;", true);
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/Compat16", "lambda$getBlockTags$0", "(Ljava/util/Map$Entry;)" + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;"), false), Type.getType("(Ljava/util/Map$Entry;)" + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;"))});
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLineNumber(186, label2);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKEVIRTUAL, map("net/minecraft/class_3545", "net/minecraft/util/Pair", "net/minecraft/util/Tuple"), map("method_15442", "getLeft", "getA"), "()Ljava/lang/Object;", false), Type.getType("(" + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;") + ")" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;"))});
methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKEVIRTUAL, map("net/minecraft/class_3545", "net/minecraft/util/Pair", "net/minecraft/util/Tuple"), map("method_15441", "getRight", "getB"), "()Ljava/lang/Object;", false), Type.getType("(" + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;") + ")Ljava/lang/Object;")});
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(187, label3);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/google/common/collect/ImmutableMap", "toImmutableMap", "(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/util/stream/Collector;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, "java/util/Map");
methodVisitor.visitInsn(DUP);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat16", "blockTags", "Ljava/util/Map;");
Label label4 = new Label();
methodVisitor.visitJumpInsn(GOTO, label4);
methodVisitor.visitLabel(label1);
methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
methodVisitor.visitFieldInsn(GETSTATIC, "com/ptsmods/morecommands/compat/Compat16", "blockTags", "Ljava/util/Map;");
methodVisitor.visitLabel(label4);
methodVisitor.visitLineNumber(185, label4);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/util/Map"});
methodVisitor.visitInsn(ARETURN);
Label label5 = new Label();
methodVisitor.visitLabel(label5);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label5, 0);
methodVisitor.visitMaxs(3, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "doubleStream", "(Lit/unimi/dsi/fastutil/doubles/DoubleList;)Ljava/util/stream/DoubleStream;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(192, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "it/unimi/dsi/fastutil/doubles/DoubleList", "stream", "()Ljava/util/stream/Stream;", true);
methodVisitor.visitInvokeDynamicInsn("applyAsDouble", "()Ljava/util/function/ToDoubleFunction;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("(Ljava/lang/Object;)D"), new Handle(Opcodes.H_INVOKESTATIC, "com/ptsmods/morecommands/compat/Compat16", "lambda$doubleStream$1", "(Ljava/lang/Double;)D", false), Type.getType("(Ljava/lang/Double;)D")});
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "mapToDouble", "(Ljava/util/function/ToDoubleFunction;)Ljava/util/stream/DoubleStream;", true);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("doubles", "Lit/unimi/dsi/fastutil/doubles/DoubleList;", null, label0, label1, 1);
methodVisitor.visitMaxs(2, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getPaintingVariant", "(" + map("Lnet/minecraft/class_1534;", "Lnet/minecraft/entity/decoration/painting/PaintingEntity;", "Lnet/minecraft/world/entity/decoration/Painting;") + ")Ljava/lang/Object;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(197, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitFieldInsn(GETFIELD, map("net/minecraft/class_1534", "net/minecraft/entity/decoration/painting/PaintingEntity", "net/minecraft/world/entity/decoration/Painting"), map("field_7134", "motive", "motive"), map("Lnet/minecraft/class_1535;", "Lnet/minecraft/entity/decoration/painting/PaintingMotive;", "Lnet/minecraft/world/entity/decoration/Motive;"));
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("painting", map("Lnet/minecraft/class_1534;", "Lnet/minecraft/entity/decoration/painting/PaintingEntity;", "Lnet/minecraft/world/entity/decoration/Painting;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setPaintingVariant", "(" + map("Lnet/minecraft/class_1534;", "Lnet/minecraft/entity/decoration/painting/PaintingEntity;", "Lnet/minecraft/world/entity/decoration/Painting;") + "Ljava/lang/Object;)V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(202, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_1535", "net/minecraft/entity/decoration/painting/PaintingMotive", "net/minecraft/world/entity/decoration/Motive"));
methodVisitor.visitFieldInsn(PUTFIELD, map("net/minecraft/class_1534", "net/minecraft/entity/decoration/painting/PaintingEntity", "net/minecraft/world/entity/decoration/Painting"), map("field_7134", "motive", "motive"), map("Lnet/minecraft/class_1535;", "Lnet/minecraft/entity/decoration/painting/PaintingMotive;", "Lnet/minecraft/world/entity/decoration/Motive;"));
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(203, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("entity", map("Lnet/minecraft/class_1534;", "Lnet/minecraft/entity/decoration/painting/PaintingEntity;", "Lnet/minecraft/world/entity/decoration/Painting;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("variant", "Ljava/lang/Object;", null, label0, label2, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "buildText", "(Lcom/ptsmods/morecommands/api/util/text/LiteralTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(207, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/compat/PrivateCompat16", "buildText", "(Lcom/ptsmods/morecommands/api/util/text/LiteralTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("builder", "Lcom/ptsmods/morecommands/api/util/text/LiteralTextBuilder;", null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "buildText", "(Lcom/ptsmods/morecommands/api/util/text/TranslatableTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(212, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/compat/PrivateCompat16", "buildText", "(Lcom/ptsmods/morecommands/api/util/text/TranslatableTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("builder", "Lcom/ptsmods/morecommands/api/util/text/TranslatableTextBuilder;", null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "buildText", "(Lcom/ptsmods/morecommands/api/util/text/EmptyTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(217, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/compat/PrivateCompat16", "buildText", "(Lcom/ptsmods/morecommands/api/util/text/EmptyTextBuilder;)" + map("Lnet/minecraft/class_5250;", "Lnet/minecraft/text/MutableText;", "Lnet/minecraft/network/chat/MutableComponent;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("builder", "Lcom/ptsmods/morecommands/api/util/text/EmptyTextBuilder;", null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "builderFromText", "(" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")Lcom/ptsmods/morecommands/api/util/text/TextBuilder;", "(" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")Lcom/ptsmods/morecommands/api/util/text/TextBuilder<*>;", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(222, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, "com/ptsmods/morecommands/compat/PrivateCompat16", "builderFromText", "(" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")Lcom/ptsmods/morecommands/api/util/text/TextBuilder;", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("text", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "broadcast", "(" + map("Lnet/minecraft/class_3324;", "Lnet/minecraft/server/PlayerManager;", "Lnet/minecraft/server/players/PlayerList;") + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;") + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")V", "(" + map("Lnet/minecraft/class_3324;", "Lnet/minecraft/server/PlayerManager;", "Lnet/minecraft/server/players/PlayerList;") + map("Lnet/minecraft/class_3545", "Lnet/minecraft/util/Pair", "Lnet/minecraft/util/Tuple") + "<Ljava/lang/Integer;" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ">;" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + ")V", null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(227, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2556", "net/minecraft/network/MessageType", "net/minecraft/network/chat/ChatType"), "values", "()[" + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;"), false);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3545", "net/minecraft/util/Pair", "net/minecraft/util/Tuple"), map("method_15442", "getLeft", "getA"), "()Ljava/lang/Object;", false);
methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitFieldInsn(GETSTATIC, map("net/minecraft/class_156", "net/minecraft/util/Util", "net/minecraft/Util"), map("field_25140", "NIL_UUID", "NIL_UUID"), "Ljava/util/UUID;");
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3324", "net/minecraft/server/PlayerManager", "net/minecraft/server/players/PlayerList"), map("method_14616", "broadcastChatMessage", "broadcastMessage"), "(" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;") + map("Lnet/minecraft/class_2556;", "Lnet/minecraft/network/MessageType;", "Lnet/minecraft/network/chat/ChatType;") + "Ljava/util/UUID;)V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(228, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("playerManager", map("Lnet/minecraft/class_3324;", "Lnet/minecraft/server/PlayerManager;", "Lnet/minecraft/server/players/PlayerList;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("type", map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;"), map("Lnet/minecraft/class_3545", "Lnet/minecraft/util/Pair", "Lnet/minecraft/util/Tuple") + "<Ljava/lang/Integer;" + map("Lnet/minecraft/class_2960;", "Lnet/minecraft/util/Identifier;", "Lnet/minecraft/resources/ResourceLocation;") + ">;", label0, label2, 2);
methodVisitor.visitLocalVariable("message", map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, label0, label2, 3);
methodVisitor.visitMaxs(4, 4);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "onStacksDropped", "(" + map("Lnet/minecraft/class_2680;", "Lnet/minecraft/block/BlockState;", "Lnet/minecraft/world/level/block/state/BlockState;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_1799;", "Lnet/minecraft/item/ItemStack;", "Lnet/minecraft/world/item/ItemStack;") + "Z)V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(232, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2680", "net/minecraft/block/BlockState", "net/minecraft/world/level/block/state/BlockState"), map("method_26180", "onStacksDropped", "spawnAfterBreak"), "(" + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_1799;", "Lnet/minecraft/item/ItemStack;", "Lnet/minecraft/world/item/ItemStack;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(233, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("state", map("Lnet/minecraft/class_2680;", "Lnet/minecraft/block/BlockState;", "Lnet/minecraft/world/level/block/state/BlockState;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;"), null, label0, label2, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label2, 3);
methodVisitor.visitLocalVariable("stack", map("Lnet/minecraft/class_1799;", "Lnet/minecraft/item/ItemStack;", "Lnet/minecraft/world/item/ItemStack;"), null, label0, label2, 4);
methodVisitor.visitLocalVariable("b", "Z", null, label0, label2, 5);
methodVisitor.visitMaxs(4, 6);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getWorldSpawnPos", "(" + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + ")" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(237, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3218", "net/minecraft/server/world/ServerWorld", "net/minecraft/server/level/ServerLevel"), map("method_27911", "getSpawnPos", "getSharedSpawnPos"), "()" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat16;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$doubleStream$1", "(Ljava/lang/Double;)D", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(192, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
methodVisitor.visitInsn(DRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("d", "Ljava/lang/Double;", null, label0, label1, 0);
methodVisitor.visitMaxs(2, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, "lambda$getBlockTags$0", "(Ljava/util/Map$Entry;)" + map("Lnet/minecraft/class_3545;", "Lnet/minecraft/util/Pair;", "Lnet/minecraft/util/Tuple;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(186, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_3545", "net/minecraft/util/Pair", "net/minecraft/util/Tuple"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true);
methodVisitor.visitTypeInsn(CHECKCAST, map("net/minecraft/class_2960", "net/minecraft/util/Identifier", "net/minecraft/resources/ResourceLocation"));
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_3545", "net/minecraft/util/Pair", "net/minecraft/util/Tuple"), "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("entry", "Ljava/util/Map$Entry;", null, label0, label1, 0);
methodVisitor.visitMaxs(4, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(62, label0);
methodVisitor.visitInsn(ACONST_NULL);
methodVisitor.visitFieldInsn(PUTSTATIC, "com/ptsmods/morecommands/compat/Compat16", "blockTags", "Ljava/util/Map;");
methodVisitor.visitInsn(RETURN);
methodVisitor.visitMaxs(1, 0);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
