package cy.jdkdigital.tfcgroomer.compat.jade.common;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.compat.jade.JadeIntegration;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum GroomingStationProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation NAME = new ResourceLocation(Groomer.MODID, "grooming_station");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
//        tooltip.add(Component.literal("hello!"));
//        tooltip.remove(accessor.getServerData().getCompound("JadeItemStorage").);
        if (accessor.getServerData().contains("breedingEnabled") && accessor.getPlayer().isShiftKeyDown()) {
            Boolean isBreedingEnabled = accessor.getServerData().getBoolean("breedingEnabled");
//            System.out.println(isBreedingEnabled);
            tooltip.add(1,
                    Component.translatable(
                    "jade.tfcgroomer.breedToggleToolTip",
                    isBreedingEnabled.toString()
                )
            );
        }
        if (true) {

        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        GroomingStationBlockEntity gStation = (GroomingStationBlockEntity) accessor.getBlockEntity();
        data.putBoolean("breedingEnabled", gStation.breedingEnabled);
        if (true) {
            data.remove("JadeItemStorage");
            data.remove("JadeItemStorageUid");
        }
        gStation.getCapability(Capabilities.ITEM).ifPresent(inventory -> {

        });
    }

    @Override
    public ResourceLocation getUid() {
        return NAME;
    }

    @Override
    public int getDefaultPriority() {
        return 4999;
    }


}
