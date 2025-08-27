package cy.jdkdigital.tfcgroomer.config;

 import net.dries007.tfc.util.Metal;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;

public class ServerConfig {

    // Breeding toggle controls
    public final ForgeConfigSpec.BooleanValue enableBreedingToggle;
    public final ForgeConfigSpec.BooleanValue breedingEnabledByDefault;

    public final ForgeConfigSpec.IntValue groomingStationTicks;

    // per-tier ranges
    public final EnumMap<Metal.Default, ForgeConfigSpec.IntValue> rangeBlocks;

    // Misc
    public final ForgeConfigSpec.BooleanValue groomingStationEnableAutomation;
    public final ForgeConfigSpec.BooleanValue groomingStationRedstoneOutput;

    // TODO: Animal blacklist


    ServerConfig(ConfigBuilderWrapper builder) {
        builder.push("general");

        enableBreedingToggle = builder.comment("Enable option to toggle automatic breeding of animals by a Grooming Station on a block-to-block basis.").define("enableBreedingToggle", true);
        breedingEnabledByDefault = builder.comment("If true, grooming stations will automatically breed animals together when feeding.").define("breedingEnabledByDefault", false);

        groomingStationTicks = builder.comment("How much time (in ticks) the Grooming Station waits before checking for animals to feed.").define("groomingStationTicks", 1200, 20, 6000);

        builder.swap("tier_ranges");

        rangeBlocks = new EnumMap<>(Metal.Default.class);
        for (Metal.Default metal : Metal.Default.values()) {
            if (metal.hasUtilities()) {
                rangeBlocks.put(metal, builder
                        .comment(String.format("The maximum distance in blocks a %s grooming station will scan for animals to feed", metal.getSerializedName().replace("_", " ")))
                        .define(metal.getSerializedName(), metal.metalTier().ordinal(), 1, Integer.MAX_VALUE)
                );
            }
        }

        builder.swap("whitelist");

        // TODO: Animal Blacklist

        builder.swap("automation");

        groomingStationEnableAutomation = builder.comment("If true, grooming stations will interact with in-world automation such as hoppers on a side-specific basis.").define("groomingStationEnableAutomation", true);

        groomingStationRedstoneOutput = builder.comment("If true, the Grooming Station will emit a redstone signal proportional to how full it is.").define("groomingStationRedstoneOutput", true);

        builder.pop();
    }

}
