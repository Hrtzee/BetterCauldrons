package io.github.hrtzee.BetterCauldrons.Capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistryHandler {
    @CapabilityInject(ICauldronCapability.class)
    public static Capability<ICauldronCapability> CAU;
    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CapabilityManager.INSTANCE.register(
                ICauldronCapability.class,
                new Capability.IStorage<ICauldronCapability>() {
                    @Nullable
                    @Override
                    public INBT writeNBT(Capability<ICauldronCapability> capability, ICauldronCapability instance, Direction side) {
                        return null;
                    }

                    @Override
                    public void readNBT(Capability<ICauldronCapability> capability, ICauldronCapability instance, Direction side, INBT nbt) {

                    }
                },
                () -> null
        ));
    }
}
