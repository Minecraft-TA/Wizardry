package com.teamwizardry.wizardry.common.spell.shape;

import static com.teamwizardry.wizardry.common.spell.component.Attributes.INTENSITY;
import static com.teamwizardry.wizardry.common.spell.component.Attributes.RANGE;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.init.ModSounds;
import com.teamwizardry.wizardry.common.spell.component.Instance;
import com.teamwizardry.wizardry.common.spell.component.Interactor;
import com.teamwizardry.wizardry.common.spell.component.PatternShape;
import com.teamwizardry.wizardry.common.utils.ColorUtils;
import com.teamwizardry.wizardry.common.utils.MathUtils;
import com.teamwizardry.wizardry.common.utils.RandUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShapeZone extends PatternShape {
    @Override
    public void run(World world, Instance instance, Interactor target) {
        Vec3d center = target.getPos();
        double range = instance.getAttributeValue(RANGE);
        double procFraction = MathHelper.clamp(instance.getAttributeValue(INTENSITY), 0, 1);
        Box region = new Box(new BlockPos(center)).expand(range - 1);
        double rangeSq = range * range;

        final NbtCompound pointsTag = new NbtCompound();
        final List<Interactor> interactors = new ArrayList<>();

        Wizardry.LOGGER.debug("Range: " + rangeSq);

        // Run on entities
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
                region,
                entity -> entity.getPos().squaredDistanceTo(center) <= rangeSq);

        int numEntityProcs = (int)Math.ceil(entities.size() * procFraction);

        Wizardry.LOGGER.debug("Entities: " + entities.size() + "\nprocFrac: " + procFraction + "\nnumProcs: " + numEntityProcs);

        while (entities.size() > numEntityProcs) {
            entities.remove((int) (Math.random() * entities.size()));
        }

        for (LivingEntity entity : entities) {
            Interactor interactor = new Interactor(entity);
            Vec3d point = interactor.getPos();
            interactors.add(interactor);

            NbtCompound pointNBT = new NbtCompound();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            pointsTag.put(UUID.randomUUID().toString(), pointNBT);
        }

        // Run on blocks
        List<BlockPos> blocks = new LinkedList<>();

        for (int x = (int) Math.floor(region.minX); x < Math.ceil(region.maxX); x++) {
            for (int y = (int) Math.floor(region.minY); y < Math.ceil(region.maxY); y++) {
                for (int z = (int) Math.floor(region.minZ); z < Math.ceil(region.maxZ); z++) {
                    if (center.squaredDistanceTo(x, y, z) <= rangeSq) {
                        blocks.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        double numBlockProcs = blocks.size() * procFraction;

        while (blocks.size() > numBlockProcs) {
            blocks.remove((int) (Math.random() * blocks.size()));
        }

        for (BlockPos pos : blocks) {
            Vec3d direction = new Vec3d(pos.getX() + 0.5 - center.x,
                    pos.getY() + 0.5 - center.y,
                    pos.getZ() + 0.5 - center.z);

            Interactor interactor = new Interactor(pos, Direction.getFacing(direction.x, direction.y, direction.z));
            Vec3d point = interactor.getPos();
            interactors.add(interactor);

            NbtCompound pointNBT = new NbtCompound();
            pointNBT.putDouble("x", point.x);
            pointNBT.putDouble("y", point.y);
            pointNBT.putDouble("z", point.z);
            pointsTag.put(UUID.randomUUID().toString(), pointNBT);
        }

        instance.getExtraData().putBoolean("exploded", false);
        sendRenderPacket(world, instance, target);
        ModSounds.playSound(world, instance.getCaster(), target, ModSounds.HIGH_PITCHED_SOLO_BLEEP, 0.5f, 2f);

//        SequenceEventLoop.createSequence(new Sequence(world, 10)
//                .event(0f, (seq) -> {
//                    instance.getExtraData().put("points", pointsTag);
//                    instance.getExtraData().putBoolean("exploded", true);
//                    sendRenderPacket(world, instance, target);
//                }).event(1f, (seq) -> {
//                    Sequence sequence = new Sequence(world, interactors.size());
//                    Collections.shuffle(interactors);
//                    for (int i = 0, interactorsSize = interactors.size(); i < interactorsSize; i++) {
//                        Interactor interactor = interactors.get(i);
//                        if (interactor.getPos().squareDistanceTo(center) <= range * range) {
//                            sequence.event((i / (float) interactorsSize),
//                                    (seq1) -> super.run(world, instance, interactor));
//                        }
//                    }
//
//                    SequenceEventLoop.createSequence(sequence);
//
//                }));

    }

    @Override
    public boolean disableAutomaticRenderPacket() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void runClient(World world, Instance instance, Interactor target) {

        final NbtCompound nbt = instance.getExtraData();

        double range = instance.getAttributeValue(RANGE);
        Color[] colors = ColorUtils.mergeColorSets(instance.getEffectColors());

        if (nbt.contains("exploded") && !nbt.getBoolean("exploded")) {
            for (int i = 0; i < 60; i++) {
                double a = i / 60.0;
                Vec2d dot = MathUtils.genCirclePerimeterDot((float) range,
                        (float) (360f * RandUtil.nextFloat(-10, 10) * a * Math.PI / 180.0f));
                Vec3d circleDotPos = target.getPos().add(dot.getX(), 0, dot.getY());
//                Wizardry.PROXY.spawnKeyedParticle(
//                        new KeyFramedGlitterBox(RandUtil.nextInt(40, 50))
//                                .pos(target.getPos(), Easing.easeOutQuart)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .pos(circleDotPos)
//                                .color(colors[0])
//                                .color(colors[1])
//                                .size(0, Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(RandUtil.nextDouble(0.1, 0.2), Easing.easeOutQuart)
//                                .size(0)
//                                .alpha(1)
//                                .alpha(1)
//                );
            }
        }

        if (nbt.contains("points")) {
            final NbtCompound points = nbt.getCompound("points");
            for (String pointKey : points.getKeys()) {
                NbtCompound pointTag = points.getCompound(pointKey);
                Vec3d point = new Vec3d(pointTag.getDouble("x"), pointTag.getDouble("y"), pointTag.getDouble("z"));
                double yDist = Math.abs(target.getPos().y - point.getY());
                for (int i = 0; i < 5; i++) {
                    Vec3d to = point.add(0, RandUtil.nextDouble(-yDist, yDist), 0);
//                    Wizardry.PROXY.spawnKeyedParticle(
//                            new KeyFramedGlitterBox(20)
//                                    .pos(point)
//                                    .pos(point, Easing.easeOutQuart)
//                                    .pos(to)
//                                    .color(colors[0])
//                                    .color(colors[1])
//                                    .size(0, Easing.easeOutQuart)
//                                    .size(0.3, Easing.linear)
//                                    .size(0.1, Easing.linear)
//                                    .size(0)
//                                    .alpha(1)
//                                    .alpha(1)
//                    );
                }
            }
        }
    }
}
