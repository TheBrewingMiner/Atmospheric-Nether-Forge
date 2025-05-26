package net.thebrewingminer.atmosphericnether.custom.events;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;

@Mod.EventBusSubscriber(modid = AtmosphericNether.MODID)
public class EndermanAggroHandler {

    @SubscribeEvent
    public static void onEndermanSpawn(EntityJoinLevelEvent spawnEvent){

        Entity entity = spawnEvent.getEntity();

        if (!(entity instanceof EnderMan enderman)) return;
        if (!(spawnEvent.getLevel() instanceof ServerLevel serverLevel)) return;

        ResourceKey<Biome> biomeKey = serverLevel.getBiome(enderman.blockPosition()).unwrapKey().orElse(null);
        ResourceLocation dispiritedForest = new ResourceLocation("tbm_nether", "dispirited_forest");
        ResourceLocation oldDispiritedForest = new ResourceLocation("tbm_nether", "old_growth_dispirited_forest");

        if (biomeKey != null){
            ResourceLocation key = biomeKey.location();
            if (key.equals(dispiritedForest) || key.equals(oldDispiritedForest)){
                enderman.getPersistentData().putBoolean("SpawnedInDisturbedBiome", true);
            }
        }
    }


}
