package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.unelevated.PowerToolCommand;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow private CompoundTag tag;
    @Shadow private int count;

    @Overwrite
    public void addEnchantment(Enchantment enchantment, int level) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9))
            this.tag.put("Enchantments", new ListTag());
        ListTag listTag = this.tag.getList("Enchantments", 10);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        // By default the lvl tag is read as an int (see EnchantmentHelper#getLevel).
        compoundTag.putInt("lvl", level); // <-- Change here (removed byte and short cast so the level limit of enchants is now Integer#MAX_VALUE instead of 255)
        listTag.add(compoundTag);
    }

    @Shadow
    public abstract CompoundTag getOrCreateTag();

    @Inject(at = @At("RETURN"), method = "getTranslationKey()Ljava/lang/String;")
    public String getTranslationKey(CallbackInfoReturnable<String> cbi) {
        ItemStack thiz = ReflectionHelper.cast(this);
        if (thiz.getItem() == Items.SPAWNER) return "block.minecraft.spawner_" + Registry.ENTITY_TYPE.get(thiz.getTag() != null && thiz.getTag().contains("BlockEntityTag", 10) && thiz.getTag().getCompound("BlockEntityTag").contains("SpawnData", 10) ? new Identifier(thiz.getTag().getCompound("BlockEntityTag").getCompound("SpawnData").getString("id")) : Registry.ENTITY_TYPE.getDefaultId()).getTranslationKey();
        else return cbi.getReturnValue();
    }

    @Inject(at = @At("TAIL"), method = "getName()Lnet/minecraft/text/Text;")
    public Text getName(CallbackInfoReturnable<Text> cbi) {
        ItemStack thiz = ReflectionHelper.cast(this);
        CompoundTag compoundTag = thiz.getSubTag("display");
        if ((compoundTag == null || !compoundTag.contains("Name", 8)) && thiz.getItem() == Items.SPAWNER) return new TranslatableText(thiz.getTranslationKey()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW));
        return cbi.getReturnValue();
    }

    @Inject(at = @At("RETURN"), method = "hasGlint()Z")
    public boolean hasGlint(CallbackInfoReturnable<Boolean> cbi) {
        return ClientOptions.Rendering.powertoolsGlint && PowerToolCommand.isPowerTool(ReflectionHelper.cast(this)) || cbi.getReturnValue();
    }

}
