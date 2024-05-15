package cy.jdkdigital.tfcgroomer.data;

import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Groomer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new LanguageProvider(output));

        gen.addProvider(event.includeClient(), new ModelProvider(output));

        gen.addProvider(event.includeServer(), new BlockLootProvider(output, List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider.LootProvider::new, LootContextParamSets.BLOCK))));
//        gen.addProvider(event.includeServer(), new LootModifierProvider(output));
        gen.addProvider(event.includeServer(), new RecipeProvider(output));
//
        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        gen.addProvider(event.includeServer(), blockTags);
//        gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
    }
}
