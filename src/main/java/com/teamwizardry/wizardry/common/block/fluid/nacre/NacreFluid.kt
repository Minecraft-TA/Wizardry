package com.teamwizardry.wizardry.common.block.fluid.nacre

import com.teamwizardry.wizardry.common.init.ModBlocks
import com.teamwizardry.wizardry.common.init.ModFluids
import com.teamwizardry.wizardry.common.init.ModItems
import net.minecraft.block.*
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

abstract class NacreFluid : FlowableFluid() {
    val flowing: Fluid
        get() = ModFluids.FLOWING_NACRE
    val still: Fluid
        get() = ModFluids.STILL_NACRE
    protected val isInfinite: Boolean
        protected get() = false

    protected override fun beforeBreakingBlock(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val entity: BlockEntity? = if (state.hasBlockEntity()) world.getBlockEntity(pos) else null
        Block.dropStacks(state, world, pos, entity)
    }

    protected override fun getFlowSpeed(world: WorldView): Int {
        return 4
    }

    protected override fun getLevelDecreasePerBlock(world: WorldView): Int {
        return 1
    }

    val bucketItem: Item
        get() = ModItems.nacreBucket!!

    protected override fun canBeReplacedWith(
        state: FluidState,
        world: BlockView,
        pos: BlockPos,
        fluid: Fluid,
        direction: Direction
    ): Boolean {
        return false
    }

    override fun getTickRate(world: WorldView): Int {
        return 200
    }

    protected val blastResistance: Float
        protected get() = 100.0f

    protected override fun toBlockState(state: FluidState): BlockState {
        return ModBlocks.liquidNacre.defaultState.with(Properties.LEVEL_15, FlowableFluid.getBlockStateLevel(state))
    }

    open class Flowing : NacreFluid() {
        protected override fun appendProperties(builder: StateManager.Builder<Fluid, FluidState>) {
            super.appendProperties(builder)
            builder.add(FlowableFluid.LEVEL)
        }

        override fun getLevel(state: FluidState): Int {
            return state.get<Int>(FlowableFluid.LEVEL)
        }

        override fun isStill(state: FluidState): Boolean {
            return false
        }
    }

    class Still : NacreFluid() {
        override fun getLevel(state: FluidState): Int {
            return 8
        }

        override fun isStill(state: FluidState): Boolean {
            return true
        }
    }
}