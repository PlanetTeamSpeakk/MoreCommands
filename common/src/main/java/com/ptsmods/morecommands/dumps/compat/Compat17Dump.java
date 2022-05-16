package com.ptsmods.morecommands.dumps.compat;
import static com.ptsmods.morecommands.dumps.ASMDump.map
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;

public class Compat17Dump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ptsmods/morecommands/compat/Compat17", null, "com/ptsmods/morecommands/compat/Compat16", null);

classWriter.visitSource("Compat17.java", null);

classWriter.visitInnerClass(map("net/minecraft/class_1297$class_5529", "net/minecraft/entity/Entity$RemovalReason", "net/minecraft/world/entity/Entity$RemovalReason"), map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("class_5529", "RemovalReason", "RemovalReason"), ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

classWriter.visitInnerClass(map("net/minecraft/class_2703$class_5893", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action"), map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"), map("class_5893", "Action", "Action"), ACC_PUBLIC | ACC_STATIC | ACC_ENUM | ACC_ABSTRACT);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(27, label0);
methodVisitor.visitVarInsn(ALOAD, 0);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/ptsmods/morecommands/compat/Compat16", "<init>", "()V", false);
methodVisitor.visitInsn(RETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitMaxs(1, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "isRemoved", "(" + map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;") + ")Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(31, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("method_31481", "isRemoved", "isRemoved"), "()Z", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("entity", map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setRemoved", "(" + map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;") + "I)V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(36, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ILOAD, 2);
Label label1 = new Label();
methodVisitor.visitJumpInsn(IFGE, label1);
methodVisitor.visitInsn(ACONST_NULL);
Label label2 = new Label();
methodVisitor.visitJumpInsn(GOTO, label2);
methodVisitor.visitLabel(label1);
methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity")});
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_1297$class_5529", "net/minecraft/entity/Entity$RemovalReason", "net/minecraft/world/entity/Entity$RemovalReason"), "values", "()[" + map("Lnet/minecraft/class_1297$class_5529;", "Lnet/minecraft/entity/Entity$RemovalReason;", "Lnet/minecraft/world/entity/Entity$RemovalReason;"), false);
methodVisitor.visitVarInsn(ILOAD, 2);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitLabel(label2);
methodVisitor.visitFrame(Opcodes.F_FULL, 3, new Object[] {"com/ptsmods/morecommands/compat/Compat17", map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), Opcodes.INTEGER}, 2, new Object[] {map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("net/minecraft/class_1297$class_5529", "net/minecraft/entity/Entity$RemovalReason", "net/minecraft/world/entity/Entity$RemovalReason")});
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1297", "net/minecraft/entity/Entity", "net/minecraft/world/entity/Entity"), map("method_31745", "setRemoved", "setRemoved"), "(" + map("Lnet/minecraft/class_1297$class_5529;", "Lnet/minecraft/entity/Entity$RemovalReason;", "Lnet/minecraft/world/entity/Entity$RemovalReason;") + ")V", false);
Label label3 = new Label();
methodVisitor.visitLabel(label3);
methodVisitor.visitLineNumber(37, label3);
methodVisitor.visitInsn(RETURN);
Label label4 = new Label();
methodVisitor.visitLabel(label4);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label4, 0);
methodVisitor.visitLocalVariable("entity", map("Lnet/minecraft/class_1297;", "Lnet/minecraft/entity/Entity;", "Lnet/minecraft/world/entity/Entity;"), null, label0, label4, 1);
methodVisitor.visitLocalVariable("reason", "I", null, label0, label4, 2);
methodVisitor.visitMaxs(3, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getInventory", "(" + map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;") + ")" + map("Lnet/minecraft/class_1661;", "Lnet/minecraft/entity/player/PlayerInventory;", "Lnet/minecraft/world/entity/player/Inventory;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(41, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1657", "net/minecraft/entity/player/PlayerEntity", "net/minecraft/world/entity/player/Player"), map("method_31548", "getInventory", "getInventory"), "()" + map("Lnet/minecraft/class_1661;", "Lnet/minecraft/entity/player/PlayerInventory;", "Lnet/minecraft/world/entity/player/Inventory;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("player", map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "isInBuildLimit", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")Z", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(46, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1937", "net/minecraft/world/World", "net/minecraft/world/level/Level"), map("method_24794", "isInBuildLimit", "isInWorldBounds"), "(" + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + ")Z", false);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 2);
methodVisitor.visitMaxs(2, 3);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "toText", "(" + map("Lnet/minecraft/class_2520;", "Lnet/minecraft/nbt/NbtElement;", "Lnet/minecraft/nbt/Tag;") + ")" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(51, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2512", "net/minecraft/nbt/NbtHelper", "net/minecraft/nbt/NbtUtils"), map("method_32270", "toPrettyPrintedText", "toPrettyComponent"), "(" + map("Lnet/minecraft/class_2520;", "Lnet/minecraft/nbt/NbtElement;", "Lnet/minecraft/nbt/Tag;") + ")" + map("Lnet/minecraft/class_2561;", "Lnet/minecraft/text/Text;", "Lnet/minecraft/network/chat/Component;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("tag", map("Lnet/minecraft/class_2520;", "Lnet/minecraft/nbt/NbtElement;", "Lnet/minecraft/nbt/Tag;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newServerPlayerEntity", "(" + map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + "Lcom/mojang/authlib/GameProfile;)" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(56, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"), "<init>", "(" + map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + "Lcom/mojang/authlib/GameProfile;)V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("server", map("Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;", "Lnet/minecraft/server/MinecraftServer;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("profile", "Lcom/mojang/authlib/GameProfile;", null, label0, label1, 3);
methodVisitor.visitMaxs(5, 4);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeSpawnerLogicNbt", "(" + map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;") + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(61, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1917", "net/minecraft/world/MobSpawnerLogic", "net/minecraft/world/level/BaseSpawner"), map("method_8272", "writeNbt", "save"), "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")" + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("logic", map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label1, 3);
methodVisitor.visitLocalVariable("nbt", map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, label0, label1, 4);
methodVisitor.visitMaxs(4, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "readSpawnerLogicNbt", "(" + map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;") + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(66, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(ALOAD, 3);
methodVisitor.visitVarInsn(ALOAD, 4);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1917", "net/minecraft/world/MobSpawnerLogic", "net/minecraft/world/level/BaseSpawner"), map("method_8280", "readNbt", "load"), "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;") + map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(67, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label2, 0);
methodVisitor.visitLocalVariable("logic", map("Lnet/minecraft/class_1917;", "Lnet/minecraft/world/MobSpawnerLogic;", "Lnet/minecraft/world/level/BaseSpawner;"), null, label0, label2, 1);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label2, 2);
methodVisitor.visitLocalVariable("pos", map("Lnet/minecraft/class_2338;", "Lnet/minecraft/util/math/BlockPos;", "Lnet/minecraft/core/BlockPos;"), null, label0, label2, 3);
methodVisitor.visitLocalVariable("nbt", map("Lnet/minecraft/class_2487;", "Lnet/minecraft/nbt/NbtCompound;", "Lnet/minecraft/nbt/CompoundTag;"), null, label0, label2, 4);
methodVisitor.visitMaxs(4, 5);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setSignEditor", "(" + map("Lnet/minecraft/class_2625;", "Lnet/minecraft/block/entity/SignBlockEntity;", "Lnet/minecraft/world/level/block/entity/SignBlockEntity;") + map("Lnet/minecraft/class_1657;", "Lnet/minecraft/entity/player/PlayerEntity;", "Lnet/minecraft/world/entity/player/Player;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(71, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_1657", "net/minecraft/entity/player/PlayerEntity", "net/minecraft/world/entity/player/Player"), map("method_5667", "getUuid", "getUUID"), "()Ljava/util/UUID;", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_2625", "net/minecraft/block/entity/SignBlockEntity", "net/minecraft/world/level/block/entity/SignBlockEntity"), map("method_11306", "setEditor", "setAllowedPlayerEditor"), "(Ljava/util/UUID;)V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(72, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label2, 0);
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
methodVisitor.visitLineNumber(76, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_5455", "net/minecraft/util/registry/DynamicRegistryManager", "net/minecraft/core/RegistryAccess"), map("method_30530", "get", "registryOrThrow"), "(" + map("Lnet/minecraft/class_5321;", "Lnet/minecraft/util/registry/RegistryKey;", "Lnet/minecraft/resources/ResourceKey;") + ")" + map("Lnet/minecraft/class_2378;", "Lnet/minecraft/util/registry/Registry;", "Lnet/minecraft/core/Registry;"), false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
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
methodVisitor.visitLineNumber(81, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, map("net/minecraft/class_1922", "net/minecraft/world/BlockView", "net/minecraft/world/level/BlockGetter"), map("method_31605", "getHeight", "getHeight"), "()I", true);
methodVisitor.visitInsn(IRETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1922;", "Lnet/minecraft/world/BlockView;", "Lnet/minecraft/world/level/BlockGetter;"), null, label0, label1, 1);
methodVisitor.visitMaxs(1, 2);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newFireballEntity", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;") + "DDDI)" + map("Lnet/minecraft/class_1674;", "Lnet/minecraft/entity/projectile/FireballEntity;", "Lnet/minecraft/world/entity/projectile/LargeFireball;"), null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(86, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_1674", "net/minecraft/entity/projectile/FireballEntity", "net/minecraft/world/entity/projectile/LargeFireball"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitVarInsn(DLOAD, 3);
methodVisitor.visitVarInsn(DLOAD, 5);
methodVisitor.visitVarInsn(DLOAD, 7);
methodVisitor.visitVarInsn(ILOAD, 9);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_1674", "net/minecraft/entity/projectile/FireballEntity", "net/minecraft/world/entity/projectile/LargeFireball"), "<init>", "(" + map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;") + map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;") + "DDDI)V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("world", map("Lnet/minecraft/class_1937;", "Lnet/minecraft/world/World;", "Lnet/minecraft/world/level/Level;"), null, label0, label1, 1);
methodVisitor.visitLocalVariable("owner", map("Lnet/minecraft/class_1309;", "Lnet/minecraft/entity/LivingEntity;", "Lnet/minecraft/world/entity/LivingEntity;"), null, label0, label1, 2);
methodVisitor.visitLocalVariable("velocityX", "D", null, label0, label1, 3);
methodVisitor.visitLocalVariable("velocityY", "D", null, label0, label1, 5);
methodVisitor.visitLocalVariable("velocityZ", "D", null, label0, label1, 7);
methodVisitor.visitLocalVariable("explosionPower", "I", null, label0, label1, 9);
methodVisitor.visitMaxs(11, 10);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getProcessorString", "()Ljava/lang/String;", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(91, label0);
methodVisitor.visitTypeInsn(NEW, "oshi/SystemInfo");
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESPECIAL, "oshi/SystemInfo", "<init>", "()V", false);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "oshi/SystemInfo", "getHardware", "()Loshi/hardware/HardwareAbstractionLayer;", false);
methodVisitor.visitMethodInsn(INVOKEINTERFACE, "oshi/hardware/HardwareAbstractionLayer", "getProcessor", "()Loshi/hardware/CentralProcessor;", true);
methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitMaxs(2, 1);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "playerSetWorld", "(" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;") + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + ")V", null, null);
methodVisitor.visitCode();
Label label0 = new Label();
methodVisitor.visitLabel(label0);
methodVisitor.visitLineNumber(96, label0);
methodVisitor.visitVarInsn(ALOAD, 1);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKEVIRTUAL, map("net/minecraft/class_3222", "net/minecraft/server/network/ServerPlayerEntity", "net/minecraft/server/level/ServerPlayer"), map("method_32747", "setWorld", "setLevel"), "(" + map("Lnet/minecraft/class_3218;", "Lnet/minecraft/server/world/ServerWorld;", "Lnet/minecraft/server/level/ServerLevel;") + ")V", false);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLineNumber(97, label1);
methodVisitor.visitInsn(RETURN);
Label label2 = new Label();
methodVisitor.visitLabel(label2);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label2, 0);
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
methodVisitor.visitLineNumber(101, label0);
methodVisitor.visitTypeInsn(NEW, map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"));
methodVisitor.visitInsn(DUP);
methodVisitor.visitMethodInsn(INVOKESTATIC, map("net/minecraft/class_2703$class_5893", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action"), "values", "()[" + map("Lnet/minecraft/class_2703$class_5893;", "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action;"), false);
methodVisitor.visitVarInsn(ILOAD, 1);
methodVisitor.visitInsn(AALOAD);
methodVisitor.visitVarInsn(ALOAD, 2);
methodVisitor.visitMethodInsn(INVOKESPECIAL, map("net/minecraft/class_2703", "net/minecraft/network/packet/s2c/play/PlayerListS2CPacket", "net/minecraft/network/protocol/game/ClientboundPlayerInfoPacket"), "<init>", "(" + map("Lnet/minecraft/class_2703$class_5893;", "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;", "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoPacket$Action;") + "[" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;") + ")V", false);
methodVisitor.visitInsn(ARETURN);
Label label1 = new Label();
methodVisitor.visitLabel(label1);
methodVisitor.visitLocalVariable("this", "Lcom/ptsmods/morecommands/compat/Compat17;", null, label0, label1, 0);
methodVisitor.visitLocalVariable("action", "I", null, label0, label1, 1);
methodVisitor.visitLocalVariable("players", "[" + map("Lnet/minecraft/class_3222;", "Lnet/minecraft/server/network/ServerPlayerEntity;", "Lnet/minecraft/server/level/ServerPlayer;"), null, label0, label1, 2);
methodVisitor.visitMaxs(4, 3);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
