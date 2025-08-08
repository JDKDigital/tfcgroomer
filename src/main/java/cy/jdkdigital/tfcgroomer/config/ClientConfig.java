package cy.jdkdigital.tfcgroomer.config;

import net.dries007.tfc.config.ConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public final ForgeConfigSpec.BooleanValue showBreedingToggleStateInInfoMods;
    public final ForgeConfigSpec.BooleanValue showInventoryInInfoMods;

    ClientConfig(ConfigBuilder builder) {
        builder.push("display");

        showBreedingToggleStateInInfoMods = builder.comment("If true, mods like Jade that add info when hovering on a block will display the grooming station's current breeding toggle state.").define("showBreedingToggleStateInInfoMods", false);

        showInventoryInInfoMods = builder.comment("If true, mods like Jade that add info when hovering on a block will display a count of the grooming station's current food inventory.").define("showInventoryInInfoMods", false);

        builder.pop();
    }
}
