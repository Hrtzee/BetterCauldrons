package io.github.hrtzee.BetterCauldrons.Recipes;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface IStuff {
    ArrayList<ItemStack> getItemStacks();
    String getTag();
}
