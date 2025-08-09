package cy.jdkdigital.tfcgroomer.config;

import cy.jdkdigital.tfcgroomer.Groomer;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Shamelessly cribbed from {@link net.dries007.tfc.config.ConfigBuilder}
 */
public class ConfigBuilderWrapper {
    private final ForgeConfigSpec.Builder builder;
    private String translationKeyPrefix;
    private boolean emptyLineAdded;

    public ConfigBuilderWrapper(ForgeConfigSpec.Builder builder, String translationKeyPrefix) {
        this.builder = builder;
        this.translationKeyPrefix = translationKeyPrefix;
        this.emptyLineAdded = false;
    }

    public ConfigBuilderWrapper push(String path) { builder.push(path); return this; }
    public ConfigBuilderWrapper swap(String path) { builder.pop().push(path); return this; }
    public ConfigBuilderWrapper pop() { builder.pop(); return this; }
    public ConfigBuilderWrapper pop(int n)
    {
        for (int i = 0; i < n; i++) pop();
        return this;
    }

    public ConfigBuilderWrapper comment(String... path)
    {
        // NightConfig's Toml config formatting is AWFUL
        // - Insert a blank comment as the first comment line
        // - Insert a space before the comment body
        if (!emptyLineAdded)
        {
            builder.comment("");
            emptyLineAdded = true;
        }
        for (String line : path)
        {
            builder.comment(" " + line);
        }
        return this;
    }

    public ForgeConfigSpec.BooleanValue define(String path, boolean value) { return begin(path).define(path, value); }
    public ForgeConfigSpec.IntValue define(String path, int value, int min, int max) { return begin(path).defineInRange(path, value, min, max); }
    public ForgeConfigSpec.DoubleValue define(String path, double value, double min, double max) { return begin(path).defineInRange(path, value, min, max); }
    public ForgeConfigSpec.ConfigValue<String> define(String path, String value) { return begin(path).define(path, value); }
    public <E extends Enum<E>> ForgeConfigSpec.EnumValue<E> define(String path, E value) { return begin(path).defineEnum(path, value); }
    public ForgeConfigSpec.ConfigValue<List<? extends String>> define(String path, List<? extends String> value, Predicate<String> predicate) { return begin(path).defineListAllowEmpty(path, new ArrayList<>(value), o -> o instanceof String s && predicate.test(s)); }

    private ForgeConfigSpec.Builder begin(String path)
    {
        builder.translation(Groomer.MODID + ".config." + translationKeyPrefix + "." + path);
        emptyLineAdded = false;
        return builder;
    }
}
