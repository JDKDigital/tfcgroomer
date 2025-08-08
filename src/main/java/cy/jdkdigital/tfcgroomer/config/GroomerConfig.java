package cy.jdkdigital.tfcgroomer.config;

import net.dries007.tfc.config.ConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public final class GroomerConfig {
    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;

    public static void init() {
    }

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory) {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getKey();
    }

    static {
        SERVER = register(ModConfig.Type.SERVER, ServerConfig::new);
        CLIENT = register(ModConfig.Type.CLIENT, ClientConfig::new);

    }


}
