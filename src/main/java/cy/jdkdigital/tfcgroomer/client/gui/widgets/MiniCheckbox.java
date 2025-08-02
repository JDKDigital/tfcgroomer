package cy.jdkdigital.tfcgroomer.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MiniCheckbox extends Checkbox {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Groomer.MODID, "textures/gui/widgets/mini_checkbox.png");
    private static final int TEXT_COLOR = 4210752;
    private boolean selected;
    private final boolean showLabel;

    public MiniCheckbox(int pX, int pY, Component pMessage, boolean selected, boolean showLabel) {
        super(pX, pY, 10, 10, pMessage, selected, showLabel);
        this.showLabel = showLabel;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int x, int y, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        guiGraphics.blit(TEXTURE,
                this.getX(),
                this.getY(),
                !this.isHovered() ? 0.0F : 10.0F,
                !this.selected ? 0.0F : 10.0F,
                10,
                10,
                32,
                32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.showLabel) guiGraphics.drawString(font,
                this.getMessage(),
                this.getX() + 13,
                this.getY() + 1,
                TEXT_COLOR | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
