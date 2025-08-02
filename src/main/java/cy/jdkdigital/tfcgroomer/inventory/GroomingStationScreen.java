package cy.jdkdigital.tfcgroomer.inventory;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.client.gui.widgets.MiniCheckbox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class GroomingStationScreen extends AbstractContainerScreen<GroomingStationContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Groomer.MODID, "textures/gui/grooming_station.png");
    private static final Component TOGGLE_BREED = Component.translatable("gui.tfcgroomer.enable_breeding");
    private MiniCheckbox checkbox;


    @Override
    protected void init() {
        super.init();
        this.checkbox = addRenderableWidget(new MiniCheckbox(this.getGuiLeft() + 75, this.getGuiTop() + 71, TOGGLE_BREED, false, false));
    }

    public GroomingStationScreen(GroomingStationContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
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
        guiGraphics.drawString(font, TOGGLE_BREED, 88, 72, 4210752, false);

    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Draw main screen
        guiGraphics.blit(GUI_TEXTURE, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.getXSize() + 26, this.getYSize());
    }

    private void handleCheckbox(Checkbox checkbox) {

    }
}
