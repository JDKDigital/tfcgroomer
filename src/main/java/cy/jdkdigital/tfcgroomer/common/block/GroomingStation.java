package cy.jdkdigital.tfcgroomer.common.block;

import cy.jdkdigital.tfcgroomer.Groomer;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;
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

public class GroomingStation extends DeviceBlock
{
    protected static final VoxelShape SHAPE = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 2);
    public final double range;

    public GroomingStation(ExtendedProperties pProperties, double range) {
        super(pProperties, InventoryRemoveBehavior.DROP);
        this.range = range;
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        level.getBlockEntity(pos, Groomer.GROOMING_STATION_BLOCK_ENTITY.get()).ifPresent((groomingStation) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                Helpers.openScreen(serverPlayer, groomingStation, pos);
            }
        });

        return InteractionResult.SUCCESS;
    }
}
