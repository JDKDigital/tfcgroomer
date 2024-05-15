package cy.jdkdigital.tfcgroomer;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import cy.jdkdigital.tfcgroomer.common.block.GroomingStation;
import cy.jdkdigital.tfcgroomer.common.block.entity.GroomingStationBlockEntity;
import cy.jdkdigital.tfcgroomer.common.item.GroomingStationItem;
import cy.jdkdigital.tfcgroomer.inventory.GroomingStationContainer;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Groomer.MODID)
public class Groomer
{
    public static final String MODID = "tfcgroomer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<GroomingStationBlockEntity>> GROOMING_STATION_BLOCK_ENTITY;

    public static List<RegistryObject<GroomingStation>> GROOMING_STATIONS = new ArrayList<>();
    public static List<RegistryObject<GroomingStationItem>> GROOMING_STATION_ITEMS = new ArrayList<>();

    static {
        GROOMING_STATION_BLOCK_ENTITY = BLOCK_ENTITY.register("grooming_station", () -> new BlockEntityType.Builder<>(GroomingStationBlockEntity::new, ImmutableSet.copyOf(GROOMING_STATIONS.stream().map(RegistryObject::get).toList())).build(null));
        Arrays.stream(Metal.Default.values()).filter(Metal.Default::hasUtilities).forEach(metal -> {
            var GROOMING_STATION = BLOCKS.register(metal.getSerializedName() + "_grooming_station", () -> new GroomingStation(ExtendedProperties.of().instrument(NoteBlockInstrument.BANJO).mapColor(metal.mapColor()).noOcclusion().sound(SoundType.METAL).strength(10.0F, 10.0F).requiresCorrectToolForDrops().ticks(GroomingStationBlockEntity::tickServer).blockEntity(GROOMING_STATION_BLOCK_ENTITY), metal.metalTier().ordinal()));
            var GROOMING_STATION_ITEM = ITEMS.register(metal.getSerializedName() + "_grooming_station", () -> new GroomingStationItem(GROOMING_STATION.get(), new Item.Properties()));

            GROOMING_STATIONS.add(GROOMING_STATION);
            GROOMING_STATION_ITEMS.add(GROOMING_STATION_ITEM);
        });
    }

    public static final RegistryObject<MenuType<GroomingStationContainer>> GROOMING_STATION_MENU = CONTAINER_TYPES.register("grooming_station", () -> {
        return IForgeMenuType.create((windowId, playerInventory, buffer) -> {
            Level level = playerInventory.player.level();
            BlockPos pos = buffer.readBlockPos();
            GroomingStationBlockEntity entity = level.getBlockEntity(pos, GROOMING_STATION_BLOCK_ENTITY.get()).orElseThrow();
            return GroomingStationContainer.create(entity, playerInventory, windowId);
        });
    });

    public Groomer() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            GROOMING_STATION_ITEMS.forEach(item -> {
                event.accept(item.get());
            });
        }
    }
}