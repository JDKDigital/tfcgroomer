package cy.jdkdigital.tfcgroomer.event;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.inventory.GroomingStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Groomer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void clientSetupEvent(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Groomer.GROOMING_STATION_MENU.get(), GroomingStationScreen::new);
        });
    }
}
