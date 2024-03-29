package com.zundrel.conveyance.common.blocks.conveyors;

import com.zundrel.conveyance.api.ConveyorType;
import com.zundrel.conveyance.api.IConveyor;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.utilities.RotationUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class VerticalConveyorBlock extends HorizontalFacingBlock implements BlockEntityProvider, IConveyor {
    private int speed;

    public VerticalConveyorBlock(Settings settings, int speed) {
        super(settings);

        this.speed = speed;
        setDefaultState(getDefaultState().with(ConveyorProperties.FRONT, false).with(ConveyorProperties.CONVEYOR, false));
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public ConveyorType getType() {
        return ConveyorType.VERTICAL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new VerticalConveyorBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ConveyorBlockEntity blockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        if (!playerEntity.getStackInHand(hand).isEmpty() && Block.getBlockFromItem(playerEntity.getStackInHand(hand).getItem()) instanceof IConveyor) {
            return ActionResult.PASS;
        } else if (!playerEntity.getStackInHand(hand).isEmpty() && blockEntity.isEmpty()) {
            blockEntity.setStack(playerEntity.getStackInHand(hand));
            playerEntity.setStackInHand(hand, ItemStack.EMPTY);

            return ActionResult.SUCCESS;
        } else if (!blockEntity.isEmpty()) {
            playerEntity.inventory.offerOrDrop(world, blockEntity.getStack());
            blockEntity.removeStack();

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        Direction facing = blockState.get(FACING);

        world.updateNeighbors(blockPos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighbor(blockPos.offset(direction).up(), this, blockPos);
        }
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean boolean_1) {
        Direction facing = blockState.get(FACING);
        if (blockState.getBlock() != blockState2.getBlock()) {
            BlockEntity blockEntity_1 = world.getBlockEntity(blockPos);
            if (blockEntity_1 instanceof VerticalConveyorBlockEntity) {
                ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((VerticalConveyorBlockEntity) blockEntity_1).getStack());world.updateHorizontalAdjacent(blockPos, this);
            }

            world.updateNeighbors(blockPos, this);
            for (Direction direction : Direction.values()) {
                world.updateNeighbor(blockPos.offset(direction).up(), this, blockPos);
            }

            super.onBlockRemoved(blockState, world, blockPos, blockState2, boolean_1);
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean boolean_1) {
        BlockState newState = blockState.getStateForNeighborUpdate(null, blockState, world, blockPos, blockPos2);
        Direction direction = newState.get(FACING);
        ConveyorBlockEntity conveyorBlockEntity = (ConveyorBlockEntity) world.getBlockEntity(blockPos);

        BlockPos upPos = blockPos.up();

        if (world.getBlockState(upPos).getBlock() instanceof VerticalConveyorBlock && !world.getBlockState(upPos).get(ConveyorProperties.FRONT) && world.getBlockState(upPos).get(FACING) == direction)
            ((VerticalConveyorBlockEntity) conveyorBlockEntity).setUp(true);
        else
            ((VerticalConveyorBlockEntity) conveyorBlockEntity).setUp(false);

        world.setBlockState(blockPos, newState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction fromDirection, BlockState fromState, IWorld world, BlockPos blockPos, BlockPos fromPos) {
        BlockState newState = blockState;
        Direction direction = newState.get(FACING);

        BlockPos frontPos = blockPos.offset(direction.getOpposite());
        BlockPos upPos = blockPos.up();
        BlockPos conveyorPos = blockPos.offset(direction).up();

        if (world.getBlockState(frontPos).getBlock() instanceof ConveyorBlock && world.getBlockState(frontPos).get(FACING) == direction) {
            newState = newState.with(ConveyorProperties.FRONT, true);
        } else
            newState = newState.with(ConveyorProperties.FRONT, false);

        if (world.getBlockState(upPos).isAir() && world.getBlockState(conveyorPos).getBlock() instanceof ConveyorBlock && world.getBlockState(conveyorPos).get(FACING) != direction.getOpposite())
            newState = newState.with(ConveyorProperties.CONVEYOR, true);
        else
            newState = newState.with(ConveyorProperties.CONVEYOR, false);

        return newState;
    }

    @Override
    public boolean hasComparatorOutput(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
        return ((ConveyorBlockEntity) world.getBlockEntity(blockPos)).isEmpty() ? 0 : 15;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManagerBuilder) {
        stateManagerBuilder.add(new Property[]{FACING, ConveyorProperties.FRONT, ConveyorProperties.CONVEYOR});
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing());
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return true;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape box1 = RotationUtilities.getRotatedShape(new Box(0, 0, 0, 1, 1, (4F / 16F)), blockState.get(FACING));
        VoxelShape box2 = RotationUtilities.getRotatedShape(new Box(0, 0, 0, 1, (4F / 16F), 1), blockState.get(FACING));

        if (blockState.get(ConveyorProperties.FRONT)) {
            return VoxelShapes.union(box1, box2);
        } else {
            return box1;
        }
    }
}
