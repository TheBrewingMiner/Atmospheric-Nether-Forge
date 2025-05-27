package net.thebrewingminer.atmosphericnether;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.thebrewingminer.atmosphericnether.custom.entity.ZoglinHelper;
import net.thebrewingminer.atmosphericnether.custom.events.EndermanAggroHandler;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AtmosphericNether.MODID)
public class AtmosphericNether {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "atmospheric_nether";

    public AtmosphericNether(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    EntityType.ZOGLIN,
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    ZoglinHelper::checkZoglinSpawnRules
            );
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){}
    }
}
