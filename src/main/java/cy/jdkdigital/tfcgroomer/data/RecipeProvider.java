package cy.jdkdigital.tfcgroomer.data;

import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.data.builder.DamageInputsCraftingRecipeBuilder;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.DamageInputsCraftingRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        Groomer.GROOMING_STATIONS.forEach(block -> {
            String metalName = block.getId().getPath().replace("_grooming_station", "");
            var sheet = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tfc:metal/sheet/" + metalName));

            DamageInputsCraftingRecipeBuilder.shaped(RecipeCategory.MISC, block.get(), 1)
                    .unlockedBy(getHasName(sheet), has(sheet))
                    .unlockedBy(getHasName(TFCBlocks.WATTLE.get()), has(TFCBlocks.WATTLE.get()))
                    .pattern("#H#").pattern("###").pattern("W W")
                    .define('H', TFCTags.Items.HAMMERS)
                    .define('#', sheet)
                    .define('W', TFCBlocks.WATTLE.get())
                    .save(consumer, block.getId());
        });
    }
}
