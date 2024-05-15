package cy.jdkdigital.tfcgroomer.inventory;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.CallbackSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GroomingStationContainer extends BlockEntityContainer<GroomingStationBlockEntity>
{
    public static GroomingStationContainer create(GroomingStationBlockEntity be, Inventory playerInventory, int windowId) {
        return new GroomingStationContainer(be, windowId).init(playerInventory);
    }

    public GroomingStationContainer(GroomingStationBlockEntity be, int windowId) {
        super(Groomer.GROOMING_STATION_MENU.get(), windowId, be);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex) {
        return switch (this.typeOf(slotIndex)) {
            case MAIN_INVENTORY, HOTBAR -> !this.moveItemStackTo(stack, 0, 9, false);
            case CONTAINER -> !this.moveItemStackTo(stack, this.containerSlots, this.slots.size(), false);
        };
    }

    @Override
    protected void addContainerSlots() {
        this.blockEntity.getCapability(Capabilities.ITEM).ifPresent((handler) -> {
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 0, 62, 19));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 1, 80, 19));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 2, 98, 19));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 3, 62, 37));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 4, 80, 37));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 5, 98, 37));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 6, 62, 55));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 7, 80, 55));
            this.addSlot(new CallbackSlot(this.blockEntity, handler, 8, 98, 55));
        });
    }
}
