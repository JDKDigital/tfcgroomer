package cy.jdkdigital.tfcgroomer.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import cy.jdkdigital.tfcgroomer.Groomer;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModelProvider implements DataProvider
{
    protected final PackOutput packOutput;

    protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();

    public ModelProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<Block, BlockStateGenerator> blockModels = Maps.newHashMap();
        Consumer<BlockStateGenerator> blockStateOutput = (blockStateGenerator) -> {
            Block block = blockStateGenerator.getBlock();
            BlockStateGenerator blockstategenerator = blockModels.put(block, blockStateGenerator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> itemModels = Maps.newHashMap();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, elementSupplier) -> {
            Supplier<JsonElement> supplier = itemModels.put(resourceLocation, elementSupplier);
            if (supplier != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };

        ModelGenerator generator = new ModelGenerator();
        try {
            generator.registerStatesAndModels(blockStateOutput, modelOutput);
        } catch (Exception e) {
            Groomer.LOGGER.error("Error registering states and models", e);
        }

        PackOutput.PathProvider blockstatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        PackOutput.PathProvider modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");

        Groomer.GROOMING_STATIONS.forEach(block -> {
            addBlockItemParentModel(block.get(), itemModels);
        });

        List<CompletableFuture<?>> output = new ArrayList<>();
        blockModels.forEach((block, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), blockstatePathProvider.json(ForgeRegistries.BLOCKS.getKey(block))));
        });
        itemModels.forEach((rLoc, supplier) -> {
            output.add(DataProvider.saveStable(cache, supplier.get(), modelPathProvider.json(rLoc)));
        });

        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    private void generateFlatItem(Item item, String prefix, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), getFlatItemTextureMap(item, prefix), modelOutput);
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix) {
        return getFlatItemTextureMap(item, prefix, "");
    }

    private static TextureMapping getFlatItemTextureMap(Item item, String prefix, String suffix) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
        return (new TextureMapping()).put(TextureSlot.LAYER0, resourcelocation.withPrefix(prefix).withSuffix(suffix));
    }

    private void addItemModel(Item item, Supplier<JsonElement> supplier, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        if (item != null) {
            ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
            if (!itemModels.containsKey(resourcelocation)) {
                itemModels.put(resourcelocation, supplier);
            }
        }
    }

    private void addBlockItemModel(Block block, String base, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            addItemModel(item, new DelegatedModel(new ResourceLocation(Groomer.MODID, "block/" + base)), itemModels);
        }
    }

    private void addBlockItemParentModel(Block block, Map<ResourceLocation, Supplier<JsonElement>> itemModels) {
        Item item = Item.BY_BLOCK.get(block);
        if (item != null) {
            var rl = ForgeRegistries.BLOCKS.getKey(block);
            addItemModel(item, new DelegatedModel(new ResourceLocation(rl.getNamespace(), "block/" + rl.getPath())), itemModels);
        }
    }

    @Override
    public String getName() {
        return "TFC: Grooming Station Blockstate and Model generator";
    }

    static class ModelGenerator
    {
        Consumer<BlockStateGenerator> blockStateOutput;
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

        protected void registerStatesAndModels(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
            this.blockStateOutput = blockStateOutput;
            this.modelOutput = modelOutput;

            Groomer.GROOMING_STATIONS.forEach(block -> {
                createComposter(block.get(), block.getId().getPath());
            });
        }

        private void createComposter(Block block, String name) {
            var groomingStationEmpty = new ModelTemplate(Optional.of(new ResourceLocation(Groomer.MODID, "block/grooming_station")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.PARTICLE);
            var groomingStationLevel1 = new ModelTemplate(Optional.of(new ResourceLocation(Groomer.MODID, "block/grooming_station_level1")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.PARTICLE);
            var groomingStationLevel2 = new ModelTemplate(Optional.of(new ResourceLocation(Groomer.MODID, "block/grooming_station_level2")), Optional.empty(), TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.PARTICLE);

            var textureMap = (new TextureMapping())
                    .put(TextureSlot.BOTTOM, new ResourceLocation(Groomer.MODID, "block/" + name + "_side"))
                    .put(TextureSlot.TOP, new ResourceLocation(Groomer.MODID, "block/" + name + "_top"))
                    .put(TextureSlot.SIDE, new ResourceLocation(Groomer.MODID, "block/" + name + "_side"))
                    .copySlot(TextureSlot.SIDE, TextureSlot.PARTICLE);

            groomingStationEmpty.create(new ResourceLocation(Groomer.MODID, "block/" + name), textureMap, this.modelOutput);
            groomingStationLevel1.create(new ResourceLocation(Groomer.MODID, "block/" + name + "_half"), textureMap, this.modelOutput);
            groomingStationLevel2.create(new ResourceLocation(Groomer.MODID, "block/" + name + "_full"), textureMap, this.modelOutput);

            this.blockStateOutput.accept(
                    MultiPartGenerator.multiPart(block)
                            .with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block)))
                            .with(Condition.condition().term(GroomingStation.LEVEL, 0), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "")))
                            .with(Condition.condition().term(GroomingStation.LEVEL, 1), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "_half")))
                            .with(Condition.condition().term(GroomingStation.LEVEL, 2), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(block, "_full")))
            );
        }
    }
}
