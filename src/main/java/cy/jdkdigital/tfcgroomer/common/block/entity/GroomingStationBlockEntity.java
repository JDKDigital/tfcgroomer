package cy.jdkdigital.tfcgroomer.common.block.entity;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import cy.jdkdigital.tfcgroomer.config.GroomerConfig;
import cy.jdkdigital.tfcgroomer.inventory.GroomingStationContainer;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.IntArrayBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
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
    int counter;

    public static void tickServer(Level level, BlockPos pos, GroomingStationBlockEntity groomStation) {
        if (groomStation.counter-- > 0 || !(level instanceof ServerLevel)) return;
        groomStation.counter = GroomerConfig.SERVER.groomingStationTicks.get();

        // Inventory updates
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < groomStation.inventory.getSlots(); i++) {
            var stack = groomStation.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        // Animal feeding
        List<Animal> entities = level.getEntitiesOfClass(Animal.class, (new AABB(pos).inflate(groomStation.range, 1d, groomStation.range))).stream().toList();
        if (entities.isEmpty()) return;

        Player fakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(PLAYER_UUID, "grooming_station"));

        entities.forEach(animal -> feedAnimalIfConditionsMet(animal, stacks, fakePlayer, groomStation.breedingEnabled));


    }

    private static void feedAnimalIfConditionsMet(Animal animal, List<ItemStack> currentStacks, Player fakePlayer, boolean breedingEnabled) {
        if (animal instanceof TFCAnimalProperties tfcAnimal) {

            float animalFamiliarity = tfcAnimal.getFamiliarity();
            boolean isChild = tfcAnimal.getAgeType() == TFCAnimalProperties.Age.CHILD;
            boolean animalHungry = tfcAnimal.isHungry();

            for (ItemStack stack : currentStacks) {
                boolean stackHasItems = !stack.isEmpty();
                boolean stackIsEdible = tfcAnimal.isFood(stack);
                // Feeding logic
                if (stackHasItems && animalHungry && stackIsEdible) {
                    if (breedingEnabled) {
                        tfcAnimal.eatFood(stack, InteractionHand.MAIN_HAND, fakePlayer);
                        break;
                    } else {
                        if ((isChild && animalFamiliarity < 1.0f) || (animalFamiliarity < tfcAnimal.getAdultFamiliarityCap())) {
                            tfcAnimal.eatFood(stack, InteractionHand.MAIN_HAND, fakePlayer);
                            break;
                        }
                    }
                }
            }
        }
    }

    protected final ContainerData syncData;
    public boolean breedingEnabled; // Can Grooming Station feed animals capable of breeding

    public GroomingStationBlockEntity(BlockPos pos, BlockState state) {
//        this(Groomer.GROOMING_STATION_BLOCK_ENTITY.get(), pPos, pBlockState);
        super(Groomer.GROOMING_STATION_BLOCK_ENTITY.get(), pos, state, GroomingStationInventory::new, NAME);

        breedingEnabled = GroomerConfig.SERVER.breedingEnabledByDefault.get();
        syncData = new IntArrayBuilder().add(() -> toInt(this.breedingEnabled), value -> breedingEnabled = toBool(value));

         if (GroomerConfig.SERVER.groomingStationEnableAutomation.get()) {
            sidedInventory
                    .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), d -> d != Direction.DOWN)
                    .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), Direction.DOWN);
        }

        if (state.getBlock() instanceof GroomingStation groomingStation) {
            this.range = groomingStation.getRange();
        }
    }

    public ContainerData getSyncData() {
        return this.syncData;
    }

    public void setBreedingEnabled(boolean b) {
        if (!GroomerConfig.SERVER.enableBreedingToggle.get()) {
            Groomer.LOGGER.info("Breeding toggling is disabled by server config. How did you even call this?");
            return;
        }
        this.breedingEnabled = b;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInv, @NotNull Player player) {
        return GroomingStationContainer.create(this, playerInv, containerId);
    }

    @Override
    public void loadAdditional(CompoundTag nbt) {
        if (nbt.contains("breedingEnabled")) {
            this.breedingEnabled = nbt.getBoolean("breedingEnabled");
        } else {
            this.breedingEnabled = GroomerConfig.SERVER.breedingEnabledByDefault.get();
        }

        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("breedingEnabled", this.breedingEnabled);

        super.saveAdditional(nbt);
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

    // TFC-friendly version of standard Minecraft comparator output function
    public int getAnalogOutputSignal() {
        int i = 0;
        float f = 0.0F;

        for(int j = 0; j < inventory.getSlots(); ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                f += (float)itemstack.getCount() / (float)Math.min(inventory.getSlotStackLimit(i), itemstack.getMaxStackSize());
                ++i;
            }
        }

        f /= (float)inventory.getSlots();
        return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }


    private static int toInt(boolean b) {return b ? 1 : 0;}

    private static boolean toBool(int i) {return i >= 1;}
}
