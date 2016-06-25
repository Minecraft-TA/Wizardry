package com.teamwizardry.wizardry.spells.modules.modifiers;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier.Operation;
import com.teamwizardry.wizardry.api.modules.attribute.AttributeModifier.Priority;
import com.teamwizardry.wizardry.spells.modules.ModuleType;

public class ModuleCritChance extends Module implements IModifier
{
	public ModuleCritChance()
	{
		canHaveChildren = false;
	}
	
	@Override
	public ModuleType getType()
	{
		return ModuleType.MODIFIER;
	}
	
	@Override
	public void apply(AttributeMap map)
	{
		map.putModifier(Attribute.CRIT_CHANCE, new AttributeModifier(Operation.ADD, 0.1));
		
		// 100% Crit Chance ~ 
		map.putModifier(Attribute.COST, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
		map.putModifier(Attribute.BURNOUT, new AttributeModifier(Operation.MULTIPLY, 1.5, Priority.LOW));
	}
}