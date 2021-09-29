package io.github.hrtzee.BetterCauldrons.Capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICauldronCapability extends INBTSerializable<CompoundNBT> {
    int getDuration();
    void setDuration(int duration);
    void addDuration();
    byte getDay();
    void setDay(byte day);
    void turnDay();
    boolean isAddable();
    void setAddable(boolean addable);

}
