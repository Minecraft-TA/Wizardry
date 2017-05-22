package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.fluid.FluidBlockMana;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@TileRegister("mana_battery")
public class TileManaBattery extends TileMod implements ITickable, IManaSink {

	public int maxMana = 21000;
	@Save
	public int currentMana;

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		int count = 0;
		for (int i = -4; i < 4; i++)
			for (int j = -4; j < 4; j++)
				if (world.getBlockState(getPos().add(i, -3, j)) == FluidBlockMana.instance.getDefaultState())
					count++;

		if (count < 21) return;
		if (maxMana <= currentMana) return;

		PosUtils.ManaBatteryPositions positions = new PosUtils.ManaBatteryPositions(world, pos);
		ArrayList<BlockPos> poses = new ArrayList<>(positions.takenPoses);
		if (poses.isEmpty()) return;
		for (BlockPos target : poses) {
			IBlockState state = world.getBlockState(target);
			if (state.getBlock() == ModBlocks.PEARL_HOLDER) {
				TilePearlHolder holder = (TilePearlHolder) world.getTileEntity(target);
				if (holder != null && !holder.pearl.isEmpty() && holder.pearl.getItem() == ModItems.MANA_ORB && ItemNBTHelper.getInt(holder.pearl, "orb_tick", 0) >= 50000) {
					holder.pearl = new ItemStack(ModItems.GLASS_ORB);
					currentMana += 1000;
				}
			}
		}
	}
}
