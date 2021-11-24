package com.teamwizardry.wizardry.common.spell.component;

import net.minecraft.world.World;

/**
 * Shape effects have affectEntity and affectBlock to run their linked effects and then
 * run any attached shapes after them.
 */
public abstract class PatternShape extends Pattern
{

    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if (instance instanceof ShapeInstance) {
            ShapeInstance shape = (ShapeInstance) instance;
            shape.effects.forEach(effect -> effect.run(world, entity));
            if (shape.nextShape != null)
                shape.nextShape.run(world, entity);
        }
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        if (instance instanceof ShapeInstance) {
            ShapeInstance shape = (ShapeInstance) instance;
            shape.effects.forEach(effect -> effect.run(world, block));
            if (shape.nextShape != null)
                shape.nextShape.run(world, block);
        }
    }
}
