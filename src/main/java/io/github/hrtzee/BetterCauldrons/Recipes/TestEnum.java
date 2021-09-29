package io.github.hrtzee.BetterCauldrons.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum TestEnum {
    TEST_ENUM(Items.ACACIA_DOOR.getDefaultInstance());

    private ItemStack itemStack;

    TestEnum(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
