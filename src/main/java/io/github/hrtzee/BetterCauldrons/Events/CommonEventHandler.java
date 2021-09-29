package io.github.hrtzee.BetterCauldrons.Events;

import io.github.hrtzee.BetterCauldrons.Capability.CapabilityRegistryHandler;
import io.github.hrtzee.BetterCauldrons.Capability.CauldronCapabilityProvider;
import io.github.hrtzee.BetterCauldrons.Capability.ICauldronCapability;
import io.github.hrtzee.BetterCauldrons.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class CommonEventHandler {
    @SubscribeEvent
    public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event){
        Entity entity = event.getObject();
        if(entity instanceof PlayerEntity){
            event.addCapability(new ResourceLocation(Utils.MOD_ID,"mage"),new CauldronCapabilityProvider());
        }
    }
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof PlayerEntity&&!event.getWorld().isClientSide){
            PlayerEntity player = (PlayerEntity) entity;
            LazyOptional<ICauldronCapability> capability = player.getCapability(CapabilityRegistryHandler.CAU);
            player.sendMessage(new StringTextComponent((capability.isPresent())?"Loading Capability System successfully":"Loading Capability System failed"),player.getUUID());
            capability.ifPresent(l->{
                l.setDuration(l.getDuration());
            });
        }
    }
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        PlayerEntity player = event.getPlayer();
        PlayerEntity original = event.getOriginal();

        LazyOptional<ICauldronCapability> newCap = player.getCapability(CapabilityRegistryHandler.CAU);
        LazyOptional<ICauldronCapability> oldCap = original.getCapability(CapabilityRegistryHandler.CAU);

        if(newCap.isPresent()&&oldCap.isPresent()){
            newCap.ifPresent((cap)-> oldCap.ifPresent(originalCap->{
                cap.setDuration(originalCap.getDuration());
            }));
        }
    }
}
