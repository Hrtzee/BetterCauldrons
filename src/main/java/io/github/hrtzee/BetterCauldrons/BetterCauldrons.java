package io.github.hrtzee.BetterCauldrons;


import io.github.hrtzee.BetterCauldrons.Items.ItemRegistry;
import io.github.hrtzee.BetterCauldrons.Networking.NetWorking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Utils.MOD_ID)
public class BetterCauldrons {

    public BetterCauldrons() {
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetUp);
    }
    public void onCommonSetUp(FMLCommonSetupEvent event){
        NetWorking.registerMessage();
    }
}
