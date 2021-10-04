package io.github.hrtzee.BetterCauldrons.Recipes;

import io.github.hrtzee.BetterCauldrons.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum Recipes implements IRecipe{
    EMPTY(ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,0,0,false),
    MUSHROOM_STEW(Items.RED_MUSHROOM.getDefaultInstance(),Items.BROWN_MUSHROOM.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.MUSHROOM_STEW.getDefaultInstance(),3,600,true),
    RABBIT_STEW_R(Items.RED_MUSHROOM.getDefaultInstance(),Items.CARROT.getDefaultInstance(),Items.COOKED_RABBIT.getDefaultInstance(),Items.BAKED_POTATO.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.RABBIT_STEW.getDefaultInstance(),3,600,true),
    RABBIT_STEW_B(Items.BROWN_MUSHROOM.getDefaultInstance(),Items.CARROT.getDefaultInstance(),Items.COOKED_RABBIT.getDefaultInstance(),Items.BAKED_POTATO.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.RABBIT_STEW.getDefaultInstance(),3,600,true),
    STEAK(Items.BEEF.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_BEEF.getDefaultInstance(),0,300,false),
    BAKED_POTATO(Items.POTATO.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.BAKED_POTATO.getDefaultInstance(), 0,200,false),
    BEETROOT_SOUP(Items.BEETROOT.getDefaultInstance(),Items.BEETROOT.getDefaultInstance(),Items.BEETROOT.getDefaultInstance(),Items.BEETROOT.getDefaultInstance(),Items.BEETROOT.getDefaultInstance(),Items.BEETROOT.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.BEETROOT_SOUP.getDefaultInstance(),3,600,true),
    COOKED_MUTTON(Items.MUTTON.getDefaultInstance(), ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_MUTTON.getDefaultInstance(), 0,300,false),
    COOKED_PORKCHOP(Items.PORKCHOP.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_PORKCHOP.getDefaultInstance(), 0,300,false),
    COOKED_SALMON(Items.SALMON.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_SALMON.getDefaultInstance(), 2,300,false),
    COOKED_CHICKEN(Items.CHICKEN.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_CHICKEN.getDefaultInstance(), 0,300,false),
    COOKED_RABBIT(Items.RABBIT.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_RABBIT.getDefaultInstance(), 0,300,false),
    COOKED_COD(Items.COD.getDefaultInstance(),ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,ItemStack.EMPTY,Items.COOKED_COD.getDefaultInstance(), 0,300,false),



    ;

    private final ItemStack[] itemStacks = new ItemStack[9];
    private final ItemStack product;
    private final int consume;
    private final int cookTime;
    private final boolean needBowl;

    Recipes(ItemStack itemStack0, ItemStack itemStack1, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4, ItemStack itemStack5, ItemStack itemStack6, ItemStack itemStack7, ItemStack itemStack8, ItemStack product, int consume, int cookTime, boolean needBowl) {
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
        this.cookTime = cookTime;
        this.needBowl = needBowl;
    }

    @Override
    public int getConsume() {
        return this.consume;
    }

    @Override
    public int getCookTime() {
        return Math.max((int) Math.floor(this.cookTime * Config.COOK_RATE.get()),1);
    }

    @Override
    public ItemStack getProduct() {
        return this.product;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.itemStacks[index];
    }

    @Override
    public boolean isNeedBowl(){
        return this.needBowl;
    }


}
