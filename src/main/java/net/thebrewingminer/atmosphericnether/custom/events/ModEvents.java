package net.thebrewingminer.atmosphericnether.custom.events;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.thebrewingminer.atmosphericnether.custom.entity.ZoglinHelper;

public class ModEvents {

    @SubscribeEvent
    public static void naturalZoglinSpawning(SpawnPlacementRegisterEvent event) {
        event.register(
            EntityType.ZOGLIN,
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            ZoglinHelper::checkZoglinSpawnRules,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}