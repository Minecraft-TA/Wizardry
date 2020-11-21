package com.teamwizardry.wizardry.common.spell.effect;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Instance;
import com.teamwizardry.wizardry.api.spell.Interactor;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.Attributes.DURATION;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.BLOCK;
import static com.teamwizardry.wizardry.api.spell.Interactor.InteractorType.ENTITY;

public class EffectBurn extends PatternEffect {
    @Override
    public void affectEntity(World world, Interactor entity, Instance instance) {
        if (entity.getType() != ENTITY)
            return;

        entity.getEntity().setFire((int) instance.getAttributeValue(DURATION));
    }

    @Override
    public void affectBlock(World world, Interactor block, Instance instance) {
        if (block.getType() != BLOCK)
            return;

        BlockPos pos = block.getBlockPos();
        BlockPos off = pos.offset(block.getDir().getOpposite());
        if (FlintAndSteelItem.canSetFire(world.getBlockState(pos), world, pos)) {
            BlockState posFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos);
            world.setBlockState(pos, posFire);
        } else if (FlintAndSteelItem.canSetFire(world.getBlockState(off), world, off)) {
            BlockState offFire = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, off);
            world.setBlockState(off, offFire);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void runClient(World world, Instance instance, Interactor target) {

        for (int i = 0; i < 100; i++)
            Wizardry.PROXY.spawnParticle(
                    new GlitterBox.GlitterBoxFactory()
                            .setOrigin(target.getPos()
                                    .add(RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15),
                                            RandUtil.nextDouble(-0.15, 0.15)))
                            .setTarget(RandUtil.nextDouble(-0.25, 0.25),
                                    RandUtil.nextDouble(-0.25, 0.25),
                                    RandUtil.nextDouble(-0.25, 0.25))
                            .setDrag(RandUtil.nextFloat(0.03f, 0.05f))
                            .setGravity(RandUtil.nextFloat(-0.01f, -0.03f))
                            .setInitialColor(RandUtil.nextBoolean() ? Color.ORANGE : Color.RED)
                            .setGoalColor(RandUtil.nextBoolean() ? Color.ORANGE : Color.RED)
                            .setInitialSize(RandUtil.nextFloat(0.1f, 0.3f))
                            .setGoalSize(0)
                            .createGlitterBox(RandUtil.nextInt(5, 20)));
        world.playSound((PlayerEntity) instance.getCaster().getEntity(),
                target.getBlockPos().getX(),
                target.getBlockPos().getY(),
                target.getBlockPos().getZ(),
                SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.BLOCKS, 1, 1);

        super.runClient(world, instance, target);
    }
}
