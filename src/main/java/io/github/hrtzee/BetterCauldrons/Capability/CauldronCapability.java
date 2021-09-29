package io.github.hrtzee.BetterCauldrons.Capability;

import net.minecraft.nbt.CompoundNBT;

public class CauldronCapability implements ICauldronCapability {

    private int duration;
    private boolean addable;
    private byte day;

    public CauldronCapability(){
        this.duration = 0;
        this.day = 0;
        this.addable = true;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = Math.min(duration,20);
    }

    @Override
    public void addDuration() {
        setDuration(this.duration+1);
    }

    @Override
    public byte getDay() {
        return this.day;
    }

    @Override
    public void setDay(byte day) {
        this.day = day;
    }

    @Override
    public void turnDay() {
        if (this.day == 1)setAddable(true);
        setDay((byte) ((this.day==0)?1:0));
    }

    @Override
    public boolean isAddable() {
        return this.addable;
    }

    @Override
    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("duration",this.duration);
        compoundNBT.putByte("day",this.day);
        compoundNBT.putBoolean("addable",this.addable);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.duration = nbt.getInt("duration");
        this.day = nbt.getByte("day");
        this.addable = nbt.getBoolean("addable");
    }

}
