package cy.jdkdigital.tfcgroomer.compat.jade;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.compat.jade.common.GroomingStationProvider;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;


@WailaPlugin
public class JadeIntegration implements IWailaPlugin {



    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(GroomingStationProvider.INSTANCE, GroomingStationBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(GroomingStationProvider.INSTANCE, GroomingStation.class);
    }

}
