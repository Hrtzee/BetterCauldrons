package io.github.hrtzee.BetterCauldrons.Capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CauldronCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private ICauldronCapability cauldronCapability;
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return (cap == CapabilityRegistryHandler.CAU)? LazyOptional.of(()->this.cauldronCapability).cast() : LazyOptional.empty();
    }

    @Nonnull
    ICauldronCapability getOrCreateCapability() {
        if (cauldronCapability == null) {
            this.cauldronCapability = new CauldronCapability();
        }
        return this.cauldronCapability;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return getOrCreateCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        getOrCreateCapability().deserializeNBT(nbt);
    }
}
