package net.thebrewingminer.atmosphericnether.custom.events;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;

import java.util.List;

@Mod.EventBusSubscriber(modid = AtmosphericNether.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EndermanAggroHandler {

    @SubscribeEvent
    public static void onEndermanSpawn(EntityJoinLevelEvent spawnEvent){

        Entity entity = spawnEvent.getEntity();

        if (!(entity instanceof EnderMan enderman)) return;
        if (!(spawnEvent.getLevel() instanceof ServerLevel serverLevel)) return;

        ResourceKey<Biome> biomeKey = serverLevel.getBiome(enderman.blockPosition()).unwrapKey().orElse(null);
        ResourceLocation dispiritedForest = ResourceLocation.tryParse("tbm_nether:forests/dispirited_forest");
        ResourceLocation oldDispiritedForest = ResourceLocation.tryParse("tbm_nether:forests/old_growth_dispirited_forest");

        if (biomeKey != null){
            ResourceLocation key = biomeKey.location();
            if (key.equals(dispiritedForest) || key.equals(oldDispiritedForest)){
                enderman.getPersistentData().putBoolean("SpawnedInDisturbedBiome", true);
            }
        }
    }

    @SubscribeEvent
    public static void onEndermenTick(LivingEvent.LivingTickEvent event){

        // Base cases (Not enderman nor disturbed one, or already hostile one).
        if (!(event.getEntity() instanceof EnderMan enderman)) return;
        if (!(event.getEntity().level() instanceof ServerLevel)) return;
        if (!enderman.getPersistentData().getBoolean("SpawnedInDisturbedBiome")) return;

        final int cooldownCount = 180;
        if (enderman.getTarget() instanceof Player) {
            enderman.getPersistentData().putInt("AggroCooldown", cooldownCount);  // Set a cooldown if targeting a player
            return;                                                                         // Stop logic here.
        }

        int cooldown = enderman.getPersistentData().getInt("AggroCooldown");
        if (cooldown > 0) {
            enderman.getPersistentData().putInt("AggroCooldown", cooldown - 1); // Count down when nobody is targeted
            return;
        }

        enderman.setAggressive(true);
        int horizontalOffset = 12;
        int verticalOffset = horizontalOffset/2;
        double closestDist = Double.MAX_VALUE;
        Player closestPlayer = null;

        // Check around the entity for the nearest valid player to target.
        List<Player> players = enderman.level().getEntitiesOfClass(Player.class, new AABB(
            enderman.getX() - horizontalOffset, enderman.getY() - verticalOffset, enderman.getZ() - horizontalOffset,
            enderman.getX() + horizontalOffset, enderman.getY() + verticalOffset, enderman.getZ() + horizontalOffset));

        // Calculate closest player within the enderman's bounding box.
        for (Player player : players) {
            if (player instanceof ServerPlayer serverPlayer) {
                GameType gameType = serverPlayer.gameMode.getGameModeForPlayer();
                if (gameType != GameType.SPECTATOR && gameType != GameType.CREATIVE) {
                    double dist = enderman.distanceToSqr(player);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestPlayer = player;
                    }
                }
            }
        }

        // Play aggressive sounds at the enderman and set its target to the closest player.
        if (closestPlayer != null) {
            enderman.playStareSound();
            enderman.level().playSound(
                    null,
                    enderman.getX(),
                    enderman.getY(),
                    enderman.getZ(),
                    SoundEvents.ENDERMAN_SCREAM,
                    enderman.getSoundSource(),
                    1.0F,
                    1.0F
            );
            enderman.setTarget(closestPlayer);
        }
    }
}