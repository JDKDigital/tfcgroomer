package cy.jdkdigital.tfcgroomer.common.block.entity;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import cy.jdkdigital.tfcgroomer.inventory.GroomingStationContainer;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroomingStationBlockEntity extends TickableInventoryBlockEntity<GroomingStationBlockEntity.GroomingStationInventory>
{
    private static final Component NAME = Component.translatable("block.tfcgroomer.grooming_station");
    static final UUID PLAYER_UUID = UUID.nameUUIDFromBytes("grooming_station".getBytes(StandardCharsets.UTF_8));
    private double range = 1;
    int counter = 1200;

    public GroomingStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(Groomer.GROOMING_STATION_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public GroomingStationBlockEntity(BlockEntityType<? extends GroomingStationBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, GroomingStationInventory::new, NAME);

        sidedInventory.on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5, 6, 7, 8), d -> d != Direction.DOWN);
        sidedInventory.on(new PartialItemHandler(inventory).extract(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.DOWN);

        if (state.getBlock() instanceof GroomingStation groomingStation) {
            this.range = groomingStation.range;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInv, Player player) {
        return GroomingStationContainer.create(this, playerInv, windowID);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, GroomingStationBlockEntity e) {
        if (e.counter-- <= 0 && level instanceof ServerLevel serverLevel) {
            e.counter = 1200;

            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < e.inventory.getSlots(); i++) {
                var stack = e.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            List<TFCAnimal> entities = level.getEntitiesOfClass(TFCAnimal.class, (new AABB(pos).inflate(e.range, 1d, e.range))).stream().toList();
            if (entities.size() > 0) {
                Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(PLAYER_UUID, "grooming_station"));
                entities.forEach(tfcAnimal -> {
                    for (ItemStack stack: stacks) {
                        if (!stack.isEmpty() && tfcAnimal.isHungry() && tfcAnimal.isFood(stack)) {
                            tfcAnimal.eatFood(stack, InteractionHand.MAIN_HAND, fakePlayer);
                            break;
                        }
                    }
                });
            }
        }
    }

    public static class GroomingStationInventory extends InventoryItemHandler implements INBTSerializable<CompoundTag>
    {
        private final InventoryBlockEntity<?> entity;

        GroomingStationInventory(InventoryBlockEntity<?> entity) {
            super(entity, 9);
            this.entity = entity;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return FoodCapability.has(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            int itemCount = 0;
            for (int i = 0; i < getSlots(); i++) {
                var stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    itemCount += stack.getCount();
                }
            }
            int level = itemCount >= 160 ? 2 : (itemCount > 0 ? 1 : 0);
            if (this.entity.getBlockState().hasProperty(GroomingStation.LEVEL) && this.entity.getLevel() instanceof ServerLevel serverLevel) {
                var currentLevel = this.entity.getBlockState().getValue(GroomingStation.LEVEL);
                if (currentLevel != level) {
                    serverLevel.setBlockAndUpdate(this.entity.getBlockPos(), this.entity.getBlockState().setValue(GroomingStation.LEVEL, level));
                }
            }
        }
    }
}
