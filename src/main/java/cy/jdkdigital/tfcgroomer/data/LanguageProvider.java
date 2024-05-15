package cy.jdkdigital.tfcgroomer.data;

import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraft.data.PackOutput;
import net.minecraftforge.registries.ForgeRegistries;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider
{
    public LanguageProvider(PackOutput output) {
        super(output, Groomer.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("tfcgroomer.grooming_station.range", "Range: %s");
        Groomer.GROOMING_STATIONS.forEach(block -> {
            add(block.get(), capName(ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
        });
    }

    @Override
    public String getName() {
        return "Groomer translation provider";
    }

    private String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }
}
