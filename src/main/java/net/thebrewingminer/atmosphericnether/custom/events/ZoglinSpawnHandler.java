package net.thebrewingminer.atmosphericnether.custom.events;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;
import net.thebrewingminer.atmosphericnether.custom.entity.ZoglinSpawnCheck;

@Mod.EventBusSubscriber(modid = AtmosphericNether.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ZoglinSpawnHandler {

    @SubscribeEvent
    public static void onZoglinSpawn(SpawnPlacementRegisterEvent event) {
        event.register(
                EntityType.ZOGLIN,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ZoglinSpawnCheck::checkZoglinSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}