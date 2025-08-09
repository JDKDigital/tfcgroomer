package cy.jdkdigital.tfcgroomer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public final ForgeConfigSpec.BooleanValue showBreedingToggleStateInJade;
    public final ForgeConfigSpec.BooleanValue showFoodRotInJade;

    ClientConfig(ConfigBuilderWrapper builder) {
        builder.push("display");

        showBreedingToggleStateInJade = builder.comment("If true, Jade (if installed) will display the grooming station's current breeding toggle state.").define("showBreedingToggleStateInJade", false);

        showFoodRotInJade = builder.comment("If true, Jade (if installed) will display a count of the grooming station's current food inventory.").define("showFoodRotInJade", false);

        builder.pop();
    }
}
