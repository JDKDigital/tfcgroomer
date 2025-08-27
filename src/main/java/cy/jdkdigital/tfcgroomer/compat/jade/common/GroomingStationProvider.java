package cy.jdkdigital.tfcgroomer.compat.jade.common;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.config.GroomerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GroomingStationProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation NAME = new ResourceLocation(Groomer.MODID, "grooming_station");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!GroomerConfig.CLIENT.showBreedingToggleStateInJade.get()) return;

        if (accessor.getServerData().contains("breedingEnabled") && accessor.getPlayer().isShiftKeyDown()) {
            boolean isBreedingEnabled = accessor.getServerData().getBoolean("breedingEnabled");
            if (isBreedingEnabled) {
                tooltip.add(1, Component.translatable("tfcgroomer.tooltip.breedEnabled").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(1, Component.translatable("tfcgroomer.tooltip.breedDisabled").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        GroomingStationBlockEntity gStation = (GroomingStationBlockEntity) accessor.getBlockEntity();
        data.putBoolean("breedingEnabled", gStation.breedingEnabled);
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }
}
