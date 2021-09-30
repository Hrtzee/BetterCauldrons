package io.github.hrtzee.BetterCauldrons;


import io.github.hrtzee.BetterCauldrons.Items.ItemRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Utils.MOD_ID)
public class BetterCauldrons {

    public BetterCauldrons() {
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
