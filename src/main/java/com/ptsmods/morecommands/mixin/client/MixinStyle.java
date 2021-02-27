package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Style.class)
public class MixinStyle {

    @Shadow @Final private TextColor color;
    @Shadow @Final private Boolean bold;
    @Shadow @Final private Boolean italic;
    @Shadow @Final private Boolean underlined;
    @Shadow @Final private Boolean strikethrough;
    @Shadow @Final private Boolean obfuscated;
    @Shadow @Final private ClickEvent clickEvent;
    @Shadow @Final private HoverEvent hoverEvent;
    @Shadow @Final private String insertion;
    @Shadow @Final private Identifier font;

    @Inject(at = @At("RETURN"), method = "getColor()Lnet/minecraft/text/TextColor;")
    public TextColor getColor(CallbackInfoReturnable<TextColor> cbi) {
        return ClientOptions.EasterEggs.rainbows ? Rainbow.RAINBOW_TC : cbi.getReturnValue();
    }

    @Overwrite
    public Style withFormatting(Formatting formatting) {
        TextColor textColor = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        if (formatting == Rainbow.RAINBOW) textColor = TextColor.fromFormatting(formatting);
        else switch(formatting) {
            case OBFUSCATED:
                boolean5 = true;
                break;
            case BOLD:
                boolean_ = true;
                break;
            case STRIKETHROUGH:
                boolean3 = true;
                break;
            case UNDERLINE:
                boolean4 = true;
                break;
            case ITALIC:
                boolean2 = true;
                break;
            case RESET:
                return Style.EMPTY;
            default:
                textColor = TextColor.fromFormatting(formatting);
        }

        return mc_getStyle(textColor, boolean_, boolean2, boolean3, boolean4, boolean5);
    }

    private Style mc_getStyle(TextColor textColor, Boolean boolean_, Boolean boolean2, Boolean boolean3, Boolean boolean4, Boolean boolean5) {
        return ReflectionHelper.newInstance(Objects.requireNonNull(ReflectionHelper.getConstructor(Style.class, TextColor.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, ClickEvent.class, HoverEvent.class, String.class, Identifier.class)), textColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    @Overwrite
    public Style withExclusiveFormatting(Formatting formatting) {
        TextColor textColor = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        // Switch-statements and enums work using the enum's index which could sometimes cause ArrayIndexOutOfBounds exceptions or something of the likes.
        // So to avoid that, we check for it here.
        if (formatting == Rainbow.RAINBOW) {
            boolean5 = false;
            boolean_ = false;
            boolean3 = false;
            boolean4 = false;
            boolean2 = false;
            textColor = TextColor.fromFormatting(formatting);
        } else switch (formatting) {
            case OBFUSCATED:
                boolean5 = true;
                break;
            case BOLD:
                boolean_ = true;
                break;
            case STRIKETHROUGH:
                boolean3 = true;
                break;
            case UNDERLINE:
                boolean4 = true;
                break;
            case ITALIC:
                boolean2 = true;
                break;
            case RESET:
                return Style.EMPTY;
            default:
                boolean5 = false;
                boolean_ = false;
                boolean3 = false;
                boolean4 = false;
                boolean2 = false;
                textColor = TextColor.fromFormatting(formatting);
        }
        return mc_getStyle(textColor, boolean_, boolean2, boolean3, boolean4, boolean5);
    }

}
