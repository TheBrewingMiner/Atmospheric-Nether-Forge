package net.thebrewingminer.atmosphericnether.custom.feature;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;
import net.thebrewingminer.atmosphericnether.custom.feature.speleothem.CustomizableLargeDripstoneConfiguration;
import net.thebrewingminer.atmosphericnether.custom.feature.speleothem.CustomizableLargeDripstoneFeature;

public class ModConfiguredFeature {

        public static final DeferredRegister<Feature<?>> FEATURES =
        DeferredRegister.create(ForgeRegistries.FEATURES, AtmosphericNether.MODID);

        public static final RegistryObject<Feature<CustomizableLargeDripstoneConfiguration>> CUSTOMIZABLE_LARGE_DRIPSTONE =
                FEATURES.register("customizable_large_dripstone", () ->
                        new CustomizableLargeDripstoneFeature(CustomizableLargeDripstoneConfiguration.CODEC));

        public static void register(IEventBus eventBus) {
                FEATURES.register(eventBus);
        }
}
