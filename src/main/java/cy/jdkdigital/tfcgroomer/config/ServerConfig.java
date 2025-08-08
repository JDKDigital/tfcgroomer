package cy.jdkdigital.tfcgroomer.config;

import net.dries007.tfc.config.ConfigBuilder;
import net.dries007.tfc.util.Metal;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;

public class ServerConfig {

    // Breeding toggle controls
    public final ForgeConfigSpec.BooleanValue enableBreedingToggle;
    public final ForgeConfigSpec.BooleanValue breedingEnabledByDefault;

    // Misc
    public final ForgeConfigSpec.IntValue groomingStationTicks;
    public final ForgeConfigSpec.BooleanValue groomingStationEnableAutomation;

    // TODO: Animal blacklist

    public final EnumMap<Metal.Default, ForgeConfigSpec.IntValue> rangeBlocks;
//    public final ForgeConfigSpec.IntValue rangeCopper;
//    public final ForgeConfigSpec.IntValue rangeBronze;
//    public final ForgeConfigSpec.IntValue rangeBismuthBronze;
//    public final ForgeConfigSpec.IntValue rangeBlackBronze;
//    public final ForgeConfigSpec.IntValue rangeWroughtIron;
//    public final ForgeConfigSpec.IntValue rangeSteel;
//    public final ForgeConfigSpec.IntValue rangeBlackSteel;
//    public final ForgeConfigSpec.IntValue rangeBlueSteel;
//    public final ForgeConfigSpec.IntValue rangeRedSteel;

    // TODO: comparator outputs
    public final ForgeConfigSpec.BooleanValue groomingStationRedstoneOutput;

    ServerConfig(ConfigBuilder builder) {
        builder.push("general");

        enableBreedingToggle = builder.comment("Enable option to toggle automatic breeding of animals by a Grooming Station.").define("enableBreedingToggle", true);
        breedingEnabledByDefault = builder.comment("If true, grooming stations will automatically breed animals together when feeding.").define("breedingEnabledByDefault", false);

        groomingStationTicks = builder.comment("How often (in ticks) the Grooming Station checks for animals to feed.").define("groomingStationTicks", 20, 1200, 6000);

        builder.swap("range tiers");

        rangeBlocks = new EnumMap<>(Metal.class);
        for (Metal.Default metal : Metal.Default.values()) {
            if (metal.hasUtilities()) {
                final String valueName = String.format("%sRangeBlocks", metal.getSerializedName());
                rangeBlocks.put(metal, builder.comment(String.format("The maximum range a %s grooming station will scan for animals", metal.getSerializedName())).define(valueName, metal.metalTier().ordinal(), 1, Integer.MAX_VALUE));
            }
        }

//        rangeCopper = builder.comment("The maximum range a copper grooming station will scan for animals").define("rangeCopper", 1, 1, 100);
//        rangeBronze = builder.comment("The maximum range a bronze grooming station will scan for animals").define("rangeBronze", 2, 1, 100);
//        rangeBismuthBronze = builder.comment("The maximum range a bismuth bronze grooming station will scan for animals").define("rangeBismuthBronze", 2, 1, 100);
//        rangeBlackBronze = builder.comment("The maximum range a black bronze grooming station will scan for animals").define("rangeBlackBronze", 2, 1, 100);
//        rangeWroughtIron = builder.comment("The maximum range a wrought iron grooming station will scan for animals").define("rangeWroughtIron", 3, 1, 100);
//        rangeSteel = builder.comment("The maximum range a steel grooming station will scan for animals").define("rangeSteel", 4, 1, 100);
//        rangeBlackSteel = builder.comment("The maximum range a black steel grooming station will scan for animals").define("rangeBlackSteel", 5, 1, 100);
//        rangeBlueSteel = builder.comment("The maximum range a blue steel grooming station will scan for animals").define("rangeBlueSteel", 6, 1, 100);
//        rangeRedSteel = builder.comment("The maximum range a red steel grooming station will scan for animals").define("rangeRedSteel", 6, 1, 100);

        builder.swap("allowed animals");

        // TODO: Animal Blacklist

        builder.swap("automation");

        groomingStationEnableAutomation = builder.comment("If true, grooming stations will interact with in-world automation such as hoppers on a side-specific basis.").define("groomingStationEnableAutomation", true);

        groomingStationRedstoneOutput = builder.comment("If true, the Grooming Station emits a redstone signal proportional to how full it is.").define("groomingStationRedstoneOutput", true);

        builder.pop();
    }
}
