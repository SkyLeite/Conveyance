package com.zundrel.conveyance.common.registries;

import com.zundrel.conveyance.Conveyance;
import com.zundrel.conveyance.common.blocks.entities.ConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.DownVerticalConveyorBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.InserterBlockEntity;
import com.zundrel.conveyance.common.blocks.entities.VerticalConveyorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ConveyanceBlockEntities {
    public static BlockEntityType INSERTER = register("inserter", InserterBlockEntity::new, ConveyanceBlocks.INSERTER);
    public static BlockEntityType CONVEYOR = register("conveyor", ConveyorBlockEntity::new, ConveyanceBlocks.CONVEYOR, ConveyanceBlocks.FAST_CONVEYOR, ConveyanceBlocks.EXPRESS_CONVEYOR);
    public static BlockEntityType VERTICAL_CONVEYOR = register("vertical_conveyor", VerticalConveyorBlockEntity::new, ConveyanceBlocks.VERTICAL_CONVEYOR, ConveyanceBlocks.VERTICAL_FAST_CONVEYOR, ConveyanceBlocks.VERTICAL_EXPRESS_CONVEYOR);
    public static BlockEntityType DOWN_VERTICAL_CONVEYOR = register("down_vertical_conveyor", DownVerticalConveyorBlockEntity::new, ConveyanceBlocks.DOWN_VERTICAL_CONVEYOR, ConveyanceBlocks.DOWN_VERTICAL_FAST_CONVEYOR, ConveyanceBlocks.DOWN_VERTICAL_EXPRESS_CONVEYOR);

    public static void init() {
        // NO-OP
    }

    private static BlockEntityType register(String name, Supplier<BlockEntity> blockEntity, Block... block) {
        return Registry.register(Registry.BLOCK_ENTITY, new Identifier(Conveyance.MODID, name), BlockEntityType.Builder.create(blockEntity, block).build(null));
    }
}
