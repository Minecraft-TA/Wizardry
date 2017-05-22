package com.teamwizardry.wizardry.common.module.modifiers;

import com.teamwizardry.wizardry.api.spell.*;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleModifierExtend extends Module implements IModifier {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.MODIFIER;
	}

	@Nonnull
	@Override
	public String getID() {
		return "modifier_extend";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Extend";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Can increase range or time on shapes and effects.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Override
	public void apply(Module module) {
		int power = 2;
		module.attributes.setDouble(Attributes.EXTEND, module.attributes.getDouble(Attributes.EXTEND) + power);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleModifierExtend());
	}
}
