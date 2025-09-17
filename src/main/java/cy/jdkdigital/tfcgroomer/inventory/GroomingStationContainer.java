package cy.jdkdigital.tfcgroomer.inventory;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ButtonHandlerContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroomingStationContainer extends BlockEntityContainer<GroomingStationBlockEntity> implements ButtonHandlerContainer
{
    public static final int TOGGLE_BREED_ID = 0;

    // client
    public static GroomingStationContainer create(GroomingStationBlockEntity groomStation, Inventory playerInventory, int windowId) {
        return new GroomingStationContainer(groomStation, windowId).init(playerInventory);
    }

    // server
    public GroomingStationContainer(GroomingStationBlockEntity groomStation, int windowId) {
        super(Groomer.GROOMING_STATION_MENU.get(), windowId, groomStation);
        addDataSlots(groomStation.getSyncData());
    }


    @Override
    protected boolean moveStack(@NotNull ItemStack stack, int slotIndex) {
        return switch (this.typeOf(slotIndex)) {
            case MAIN_INVENTORY, HOTBAR -> !this.moveItemStackTo(stack, 0, 4, false);
            case CONTAINER -> !this.moveItemStackTo(stack, this.containerSlots, this.slots.size(), false);
        };
    }

    @Override
    protected void addContainerSlots() {
        this.blockEntity.getCapability(Capabilities.ITEM).ifPresent((handler) -> {
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 0, 71, 28));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 1, 89, 28));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 2, 71, 46));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 3, 89, 46));
        });
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT) {
        if (buttonID == TOGGLE_BREED_ID) {
            this.blockEntity.setBreedingEnabled(!this.blockEntity.breedingEnabled);
        }
    }

    public boolean getBreedToggleState(GroomingStationBlockEntity groomStation) {
        return groomStation.breedingEnabled;
    }
}
