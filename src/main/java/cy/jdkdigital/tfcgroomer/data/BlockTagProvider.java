package cy.jdkdigital.tfcgroomer.data;

import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, Groomer.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var pickaxeMineable = tag(BlockTags.MINEABLE_WITH_PICKAXE);

        Groomer.GROOMING_STATIONS.forEach(block -> {
            pickaxeMineable.add(block.get());
        });
    }

    @Override
    public String getName() {
        return "TFC: Grooming Station Block Tags Provider";
    }
}
