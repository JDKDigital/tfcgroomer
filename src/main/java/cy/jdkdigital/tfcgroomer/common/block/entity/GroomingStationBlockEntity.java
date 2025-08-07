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
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GroomingStationBlockEntity extends TickableInventoryBlockEntity<GroomingStationBlockEntity.GroomingStationInventory>
{
    private static final Component NAME = Component.translatable("block.tfcgroomer.grooming_station"); // TODO: add localization
    static final UUID PLAYER_UUID = UUID.nameUUIDFromBytes("grooming_station".getBytes(StandardCharsets.UTF_8));
    private double range = 1;
    int counter = 1200;

    public static void tickServer(Level level, BlockPos pos, BlockState state, GroomingStationBlockEntity gstation) {
        if (gstation.counter-- <= 0 && level instanceof ServerLevel serverLevel) {
            gstation.counter = 200;
            // Inventory updates
            List<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < gstation.inventory.getSlots(); i++) {
                var stack = gstation.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            // Animal feeding
            List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(pos).inflate(gstation.range, 1d, gstation.range))).stream().toList();
            if (!entities.isEmpty()) {
                Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(PLAYER_UUID, "grooming_station"));
                entities.forEach(animal -> {
                    if (animal instanceof TFCAnimalProperties tfcAnimal) {
                        float animalFamiliarity = tfcAnimal.getFamiliarity();
                        boolean isChild = tfcAnimal.getAgeType() == TFCAnimalProperties.Age.CHILD;
                        for (ItemStack stack : stacks) {
                            // Feeding logic
                            if (!stack.isEmpty() && tfcAnimal.isHungry() && isFood(tfcAnimal, stack)) {
                                if ((isChild && animalFamiliarity < 1.0f) || (animalFamiliarity < tfcAnimal.getAdultFamiliarityCap())) {
                                    tfcAnimal.eatFood(stack, InteractionHand.MAIN_HAND, fakePlayer);
                                    break;
                                }

                            }
                        }
                    }
                });
            }
        }
    }

    protected final ContainerData syncData;
    public boolean breedingEnabled; // Can Grooming Station feed animals capable of breeding
    private boolean breedToggleEnabled; // TODO: Is the option to toggle breeding enabled - controlled by config

    public GroomingStationBlockEntity(BlockPos pos, BlockState state) {
//        this(Groomer.GROOMING_STATION_BLOCK_ENTITY.get(), pPos, pBlockState);
        super(Groomer.GROOMING_STATION_BLOCK_ENTITY.get(), pos, state, GroomingStationInventory::new, NAME);

        breedingEnabled = false;
        breedToggleEnabled = true;
        syncData = new IntArrayBuilder().add(() -> toInt(this.breedingEnabled), value -> breedingEnabled = toBool(value));

        // TODO: if (GroomerConfig.groomingStationEnableAutomation.get())
//        {
            sidedInventory
                    .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), d -> d != Direction.DOWN)
                    .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), Direction.DOWN);
//        }

        if (state.getBlock() instanceof GroomingStation groomingStation) {
            this.range = groomingStation.range;
        }
    }

    public ContainerData getSyncData() {
        return this.syncData;
    }

    public void setBreedingEnabled(boolean b) {
        this.breedingEnabled = b;
    }

    public boolean isBreedToggleEnabled() {
        return breedToggleEnabled;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInv, @NotNull Player player) {
        return GroomingStationContainer.create(this, playerInv, containerId);
    }

    @Override
    public void loadAdditional(CompoundTag nbt) {
//        this.breedToggleEnabled = nbt.getBoolean("isBreedToggleEnabled");
        this.breedingEnabled = nbt.getBoolean("isBreedingEnabled");

        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("breedToggleEnabled", this.breedToggleEnabled);
        nbt.putBoolean("breedingEnabled", this.breedingEnabled);

        super.saveAdditional(nbt);
    }

    static boolean isFood(TFCAnimalProperties tfcAnimal, ItemStack stack) {
        return (tfcAnimal.eatsRottenFood() || !FoodCapability.isRotten(stack)) && Helpers.isItem(stack, tfcAnimal.getFoodTag());
    }

    public static class GroomingStationInventory extends InventoryItemHandler implements INBTSerializable<CompoundTag>
    {
        private final InventoryBlockEntity<?> entity;

        GroomingStationInventory(InventoryBlockEntity<?> entity) {
            super(entity, 4);
            this.entity = entity;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return FoodCapability.has(stack) || stack.is(ItemTags.create(new ResourceLocation("tfc:seeds")));
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



    private static int toInt(boolean b) {return b ? 1 : 0;}

    private static boolean toBool(int i) {return i >= 1;}
}
