package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class EasterEgg extends PositionedSoundRecord {

	public EasterEgg() {
		this(new ResourceLocation("morecommands:easteregg"), SoundCategory.AMBIENT, Float.MAX_VALUE, 0F, true, 190, AttenuationType.LINEAR, Minecraft.getMinecraft().player.getPosition().getX(), Minecraft.getMinecraft().player.getPosition().getY(), Minecraft.getMinecraft().player.getPosition().getZ());
	}

	private EasterEgg(ResourceLocation soundId, SoundCategory categoryIn, float volumeIn, float pitchIn,
			boolean repeatIn, int repeatDelayIn, AttenuationType attenuationTypeIn, float xIn, float yIn, float zIn) {
		super(soundId, categoryIn, volumeIn, pitchIn, repeatIn, repeatDelayIn, attenuationTypeIn, xIn, yIn, zIn);
	}

}
