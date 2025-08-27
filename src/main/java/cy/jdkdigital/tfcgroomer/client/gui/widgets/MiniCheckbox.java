package cy.jdkdigital.tfcgroomer.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MiniCheckbox extends ImageButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Groomer.MODID, "textures/gui/widgets/mini_checkbox.png");
    private static final int xTexStart = 0;
    private static final int yTexStart = 0;

    private static final int pWidth = 10;
    private static final int pHeight = 10;

    private static final int xDiffTex = 10; // Selected highlight
    private static final int yDiffTex = 10; // Check offset

    private static final int textureWidth = 32;
    private static final int textureHeight = 32;

    private boolean selected;

    public MiniCheckbox(int pX, int pY, boolean selected, Button.OnPress onPress) {
        super(pX, pY, 10, 10, xTexStart, yTexStart, TEXTURE, onPress);
        this.selected = selected;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int x, int y, float pPartialTick) {
        RenderSystem.enableDepthTest();
        guiGraphics.blit(TEXTURE,
                this.getX(),
                this.getY(),
                pWidth,
                pHeight,
                this.isHoveredOrFocused() ? xDiffTex : 0.0F,
                this.isSelected() ? yDiffTex : 0.0F,
                pWidth,
                pHeight,
                textureWidth,
                textureHeight);
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }
}
