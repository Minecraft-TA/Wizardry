package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.common.potion.*;

/**
 * Created by LordSaad.
 */
public class ModPotions {

	public static PotionNullGrav NULLIFY_GRAVITY;
	public static PotionNullMovement NULL_MOVEMENT;
	public static PotionSteroid STEROID;
	public static PotionPhase PHASE;
	public static PotionTimeSlow TIME_SLOW;
	public static PotionSlippery SLIPPERY;
	public static PotionLowGrav LOW_GRAVITY;
	public static PotionCrash CRASH;

	public static void init() {
		NULLIFY_GRAVITY = new PotionNullGrav();
		STEROID = new PotionSteroid();
		PHASE = new PotionPhase();
		TIME_SLOW = new PotionTimeSlow();
		NULL_MOVEMENT = new PotionNullMovement();
		SLIPPERY = new PotionSlippery();
		LOW_GRAVITY = new PotionLowGrav();
		CRASH = new PotionCrash();
	}
}
