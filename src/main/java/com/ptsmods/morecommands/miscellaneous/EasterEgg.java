package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class EasterEgg extends PositionedSoundRecord {

	private static Minecraft mc = Minecraft.getMinecraft();

	public EasterEgg() {
		super(new ResourceLocation("morecommands:easteregg"), SoundCategory.AMBIENT, Float.MAX_VALUE, 0F, true, 190, AttenuationType.LINEAR, mc.player.getPosition().getX(), mc.player.getPosition().getY(), mc.player.getPosition().getZ());
	}

	private EasterEgg(ResourceLocation soundId, SoundCategory categoryIn, float volumeIn, float pitchIn,
			boolean repeatIn, int repeatDelayIn, AttenuationType attenuationTypeIn, float xIn, float yIn, float zIn) {
		this();
	}

}
