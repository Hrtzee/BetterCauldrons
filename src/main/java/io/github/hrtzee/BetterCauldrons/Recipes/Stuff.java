package io.github.hrtzee.BetterCauldrons.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.Arrays;

public enum Stuff implements IStuff{
    SUGAR("speed",Items.SUGAR.getDefaultInstance(),Items.SUGAR_CANE.getDefaultInstance()),
    RABBITS_FOOT("jump",Items.RABBIT_FOOT.getDefaultInstance()),
    BLAZE_POWDER("strength",Items.BLAZE_POWDER.getDefaultInstance(),Items.BLAZE_ROD.getDefaultInstance()),
    GLISTERING_MELON_SLICE("heal",Items.GLISTERING_MELON_SLICE.getDefaultInstance()),
    SPIDER_EYE("poison",Items.SPIDER_EYE.getDefaultInstance()),
    GHAST_TEAR("regeneration",Items.GHAST_TEAR.getDefaultInstance()),
    MAGMA_CREAM("fire",Items.MAGMA_CREAM.getDefaultInstance()),
    PUFFER_FISH("water",Items.PUFFERFISH.getDefaultInstance()),
    GOLDEN_CARROT("night",Items.GOLDEN_CARROT.getDefaultInstance()),
    TURTLE_SHELL("turtle",Items.TURTLE_HELMET.getDefaultInstance(),Items.TURTLE_EGG.getDefaultInstance()),
    PHANTOM_MEMBRANE("slow_falling",Items.PHANTOM_MEMBRANE.getDefaultInstance()),
    REDSTONE_POWDER("long",Items.REDSTONE.getDefaultInstance(),Items.REDSTONE_BLOCK.getDefaultInstance(),Items.REDSTONE_ORE.getDefaultInstance(),Items.REDSTONE_TORCH.getDefaultInstance()),
    GLOWSTONE_DUST("plus",Items.GLOWSTONE_DUST.getDefaultInstance(),Items.GLOWSTONE.getDefaultInstance()),
    NETHER_WART("null", Items.NETHER_WART.getDefaultInstance()),
    GUN_POWDER("explosion",Items.GUNPOWDER.getDefaultInstance()),
    FERMENTED_SPIDER_EYE("sp",Items.SPIDER_EYE.getDefaultInstance()),
    ;
    private final String tag;
    private final ArrayList<ItemStack> itemStacks = new ArrayList<>();

    Stuff(String tag,ItemStack... itemStacks){
        this.tag = tag;
        this.itemStacks.addAll(Arrays.asList(itemStacks));
    }

    public ArrayList<ItemStack> getItemStacks(){
        return this.itemStacks;
    }

    public String getTag(){
        return this.tag;
    }
}
