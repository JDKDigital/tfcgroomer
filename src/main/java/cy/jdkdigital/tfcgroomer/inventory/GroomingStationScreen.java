package cy.jdkdigital.tfcgroomer.inventory;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.client.gui.widgets.MiniCheckbox;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.config.GroomerConfig;
import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GroomingStationScreen extends BlockEntityScreen<GroomingStationBlockEntity, GroomingStationContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Groomer.MODID, "textures/gui/grooming_station_small.png");
    private static final Component TOGGLE_BREED = Component.translatable("gui.tfcgroomer.enable_breeding");
    private static final String TOGGLE_BREEDING_KEY = "tfcgroomer.tooltip.toggleBreeding";
    private MiniCheckbox checkbox;



    public GroomingStationScreen(GroomingStationContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn, GUI_TEXTURE);
    }

    @Override
    protected void init() {
        super.init();
        if (GroomerConfig.SERVER.enableBreedingToggle.get()) {
            createMiniCheckbox(leftPos + this.imageWidth - 17, topPos + 71, GroomingStationContainer.TOGGLE_BREED_ID, TOGGLE_BREEDING_KEY);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void createMiniCheckbox(int x, int y, int packetButtonId, @Nullable String translationKey) {
        checkbox = new MiniCheckbox(x, y, menu.getBlockEntity().breedingEnabled, btn -> {
            checkbox.setSelected(!checkbox.isSelected());
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(packetButtonId, null));});
        if (translationKey != null) {
            checkbox.setTooltip(Tooltip.create(Component.translatable(translationKey)));
        }
        addRenderableWidget(checkbox);
    }

    @Override
    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(font, this.playerInventoryTitle, 8, (this.getYSize() - 96 + 2), 4210752, false);
        if (GroomerConfig.SERVER.enableBreedingToggle.get()) {
            guiGraphics.drawString(font, TOGGLE_BREED, this.imageWidth - 99, 72, 4210752, false);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());
    }

}
