package net.thebrewingminer.atmosphericnether.custom.feature;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;

public class ModConfiguredFeature {

        public static final DeferredRegister<Feature<?>> FEATURES =
        DeferredRegister.create(ForgeRegistries.FEATURES, AtmosphericNether.MODID);

        public static final RegistryObject<Feature<CustomizableLargeDripstoneConfiguration>> CUSTOMIZABLE_DRIPSTONE =
                FEATURES.register("customizable_dripstone", () ->
                        new CustomizableLargeDripstoneFeature(CustomizableLargeDripstoneConfiguration.CODEC));

        public static void register(IEventBus eventBus) {
                FEATURES.register(eventBus);
        }
}
