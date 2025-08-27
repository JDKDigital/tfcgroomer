package cy.jdkdigital.tfcgroomer.common.block;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.config.GroomerConfig;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GroomingStation extends DeviceBlock
{
    protected static final VoxelShape SHAPE = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 2);

    public final Metal.Default metal;

    public GroomingStation(ExtendedProperties pProperties, Metal.Default metal) {
        super(pProperties, InventoryRemoveBehavior.DROP);
        this.metal = metal;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @SuppressWarnings("deprecation") @NotNull @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        level.getBlockEntity(pos, Groomer.GROOMING_STATION_BLOCK_ENTITY.get()).ifPresent((groomingStation) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                Helpers.openScreen(serverPlayer, groomingStation, pos);
            }
        });

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return GroomerConfig.SERVER.groomingStationRedstoneOutput.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos, Groomer.GROOMING_STATION_BLOCK_ENTITY.get())
                .map(GroomingStationBlockEntity::getAnalogOutputSignal)
                .orElse(0);
    }

    /**
     * range is controlled by server config instead of by block definition
     */
    public int getRange() {
        return GroomerConfig.SERVER.rangeBlocks.get(this.metal).get();
    }
}
