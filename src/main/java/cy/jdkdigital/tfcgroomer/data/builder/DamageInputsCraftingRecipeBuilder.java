package cy.jdkdigital.tfcgroomer.data.builder;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DamageInputsCraftingRecipeBuilder
{
    public static ShapedRecipeBuilder shaped(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new Shaped(pCategory, pResult, pCount);
    }

    public static class Shaped extends ShapedRecipeBuilder
    {
        public Shaped(RecipeCategory pCategory, ItemLike pResult, int pCount) {
            super(pCategory, pResult, pCount);
        }

        @Override
        public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
            this.ensureValid(pRecipeId);
            this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
            pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, determineBookCategory(this.category), this.rows, this.key, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), true));
        }

        static class Result extends ShapedRecipeBuilder.Result
        {
            public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, CraftingBookCategory pCategory, List<String> pPattern, Map<Character, Ingredient> pKey, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, boolean pShowNotification) {
                super(pId, pResult, pCount, pGroup, pCategory, pPattern, pKey, pAdvancement, pAdvancementId, pShowNotification);
            }

            @Override
            public void serializeRecipeData(JsonObject pJson) {
                JsonObject recipe = new JsonObject();
                super.serializeRecipeData(recipe);
                recipe.addProperty("type", "minecraft:crafting_shaped");
                pJson.add("recipe", recipe);
            }

            @Override
            public RecipeSerializer<?> getType() {
                return TFCRecipeSerializers.DAMAGE_INPUT_SHAPED_CRAFTING.get();
            }
        }
    }
}
