package io.github.hrtzee.BetterCauldrons.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum Recipes implements IRecipe{
    EMPTY(ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,0),
    MUSHROOM_STEW_RECIPE(Items.RED_MUSHROOM.getDefaultInstance(),Items.BROWN_MUSHROOM.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.MUSHROOM_STEW.getDefaultInstance(),3)
    ;

    private final ItemStack[] itemStacks = new ItemStack[9];
    private final ItemStack product;
    private final int consume;

    Recipes(ItemStack itemStack0, ItemStack itemStack1, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4, ItemStack itemStack5, ItemStack itemStack6, ItemStack itemStack7, ItemStack itemStack8, ItemStack product, int consume) {
        this.itemStacks[0]=itemStack0;
        this.itemStacks[1]=itemStack1;
        this.itemStacks[2]=itemStack2;
        this.itemStacks[3]=itemStack3;
        this.itemStacks[4]=itemStack4;
        this.itemStacks[5]=itemStack5;
        this.itemStacks[6]=itemStack6;
        this.itemStacks[7]=itemStack7;
        this.itemStacks[8]=itemStack8;
        this.product = product;
        this.consume = consume;
    }

    @Override
    public int getConsume() {
        return this.consume;
    }

    @Override
    public ItemStack getProduct() {
        return this.product;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.itemStacks[index];
    }


}
