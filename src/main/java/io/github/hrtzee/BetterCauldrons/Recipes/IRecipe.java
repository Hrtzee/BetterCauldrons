package io.github.hrtzee.BetterCauldrons.Recipes;

import net.minecraft.item.ItemStack;

public interface IRecipe {
    ItemStack getItem(int index);
    ItemStack getProduct();
    int getConsume();
}
