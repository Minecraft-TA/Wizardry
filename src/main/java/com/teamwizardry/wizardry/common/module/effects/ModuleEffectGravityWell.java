package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpHelix;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectGravityWell extends Module implements IlingeringModule {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_gravity_well";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Gravity Well";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will suck in all entities around the target.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		if (!processCost(strength / 100.0, spell)) return false;

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(position)).expand(strength, strength, strength))) {
			if (entity == null) continue;
			double dist = entity.getPositionVector().distanceTo(position);
			if (dist < 2) continue;
			if (dist > strength) continue;

			final double upperMag = (strength / 100);
			final double scale = 3.5;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);

			Vec3d dir = position.subtract(entity.getPositionVector()).normalize().scale(mag);

			entity.motionX += (dir.xCoord);
			entity.motionY += (dir.yCoord);
			entity.motionZ += (dir.zCoord);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

			spell.addData(ENTITY_HIT, entity);
			if (entity instanceof EntityPlayerMP)
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));

			runNextModule(spell);
		}

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(ThreadLocalRandom.current().nextInt(20, 30));
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		ParticleSpawner.spawn(glitter, spell.world, new StaticInterp<>(position), 5, ThreadLocalRandom.current().nextInt(0, 30), (aFloat, particleBuilder) -> {
			glitter.setScale((float) ThreadLocalRandom.current().nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) ThreadLocalRandom.current().nextDouble(0.6, 1)));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 40));
			glitter.setScaleFunction(new InterpScale(1, 0));
			if (ThreadLocalRandom.current().nextBoolean())
				glitter.setPositionFunction(new InterpHelix(
						new Vec3d(0, 0, 0),
						new Vec3d(0, 2, 0),
						0.5f, 0, 1, ThreadLocalRandom.current().nextFloat()
				));
			else glitter.setPositionFunction(new InterpHelix(
					new Vec3d(0, 0, 0),
					new Vec3d(0, -2, 0),
					0.5f, 0, 1, ThreadLocalRandom.current().nextFloat()
			));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectGravityWell());
	}

	@Override
	public int lingeringTime(SpellData spell) {
		int strength = 3000;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		return strength;
	}
}
