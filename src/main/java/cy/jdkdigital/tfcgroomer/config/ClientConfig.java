package cy.jdkdigital.tfcgroomer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public final ForgeConfigSpec.BooleanValue showBreedingToggleStateInJade;

    ClientConfig(ConfigBuilderWrapper builder) {
        builder.push("display");

        showBreedingToggleStateInJade = builder.comment("If true, Jade (if installed) will display the grooming station's current breeding toggle state when crouching").define("showBreedingToggleStateInJade", false);

        builder.pop();
    }
}
