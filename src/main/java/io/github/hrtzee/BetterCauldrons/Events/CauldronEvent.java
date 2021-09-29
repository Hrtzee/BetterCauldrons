package io.github.hrtzee.BetterCauldrons.Events;

import io.github.hrtzee.BetterCauldrons.Capability.CapabilityRegistryHandler;
import io.github.hrtzee.BetterCauldrons.Recipes.Recipes;
import io.github.hrtzee.BetterCauldrons.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class CauldronEvent {
    @SubscribeEvent
    public static void CauldronCookEvent(PlayerInteractEvent.RightClickBlock event){
        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
        World world = event.getWorld();
        boolean flag = false;
        boolean flag_ = true;
        boolean bowl = false;
        boolean[] progress = {false,false,false,false,false,false,false,false,false};
        BlockPos pos1 = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());
        BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());
        Recipes recipe = Recipes.EMPTY;
        ItemStack product = new ItemStack(Items.STONE_SWORD);
        if (!(world.getBlockState(pos).is(Blocks.CAULDRON)))return;
        if (!(world.getBlockState(pos1).is(Blocks.FIRE)))return;
        if (!(world.getBlockState(pos2).getBlock() instanceof TrapDoorBlock))return;//以上初始化&判断方块状态
        if (itemStack.getItem() instanceof ShovelItem && !world.getBlockState(pos2).getValue(BlockStateProperties.OPEN)){
            List<Entity> entities = world.getEntities(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ()), AxisAlignedBB.of(new MutableBoundingBox(pos.getX(),pos.getY(),pos.getZ(),pos.getX(),pos.getY(),pos.getZ())));
            for (Recipes recipes : Recipes.values()) {//循环判断是否满足Recipes，否 则返回
                if (recipes.equals(Recipes.EMPTY)) continue;
                if (!(entities.size()>20)){//防止实体过多卡顿
                    for (Entity entity : entities) {//循环判断实体类型，满足recipe则继续
                        if (entity instanceof ItemEntity) {
                            ItemEntity itemEntity = (ItemEntity) entity;
                            {
                                for (int i = 0; i < 9; i++) {
                                    if (recipes.getItem(i).isEmpty()) {
                                        progress[i] = true;
                                        continue;
                                    }
                                    if (itemEntity.getItem().getItem().equals(recipes.getItem(i).getItem())) {
                                        progress[i] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 9; i++) {//循环判断progress元素是否全为true
                    if (!progress[i]) {
                        flag_ = false;
                        for (int j = 0; j < 9; j++) progress[j] = false;//重置progress
                        break;
                    }
                }
                if (flag_) {
                    recipe = recipes;
                    product = recipe.getProduct();
                    flag = true;
                    break;
                } else {
                    flag_ = true;
                }
            }
            if (flag){//执行配方
                if (recipe.isNeedBowl()){
                    for (Entity entity:entities){
                        if (entity instanceof ItemEntity) {
                            ItemEntity itemEntity = (ItemEntity) entity;
                            if (itemEntity.getItem().sameItem(Items.BOWL.getDefaultInstance())) {
                                bowl = true;
                                break;
                            }
                        }
                    }
                }
                if (!(!recipe.isNeedBowl()||bowl))return;
                player.swing(Hand.MAIN_HAND,true);
                itemStack.setDamageValue(itemStack.getDamageValue()+3);
                if (recipe.getConsume()>world.getBlockState(pos).getValue(BlockStateProperties.LEVEL_CAULDRON))return;
                for (Entity entity:entities) {if (entity instanceof ItemEntity) entity.addTag("dyn");}
                final ItemStack finalProduct = product;
                new Object() {
                    private int ticks = 0;
                    private float waitTicks;
                    public void start(int waitTicks) {
                        this.waitTicks = waitTicks;
                        MinecraftForge.EVENT_BUS.register(this);
                    }
                    @SubscribeEvent
                    public void tick(TickEvent.ServerTickEvent event) {
                        if (event.phase == TickEvent.Phase.END) {
                            this.ticks += 1;
                            if (ticks%2==0){
                                world.playSound(null, pos, SoundEvents.CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1, 1);
                                world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
                                world.addParticle(ParticleTypes.EFFECT, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 5, 5, 5);
                            }
                            if (!world.getBlockState(pos).is(Blocks.CAULDRON)||!(world.getBlockState(pos2).getBlock() instanceof TrapDoorBlock)||!world.getBlockState(pos1).is(Blocks.FIRE)||world.getBlockState(pos2).getValue(BlockStateProperties.OPEN)){
                                MinecraftForge.EVENT_BUS.unregister(this);//如果中途破坏方块或掀开盖子，return
                                for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.removeTag("dyn");}
                                return;
                            }
                            if (this.ticks >= this.waitTicks)
                                run();
                        }
                    }
                    private void run() {
                        world.addParticle(ParticleTypes.EXPLOSION,pos.getX(),pos.getY(),pos.getZ(),5,5,5);
                        world.playSound(null,pos, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS,1,1);
                        world.playSound(null,pos, SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 1,1);
                        BlockPos blockPos1 = new BlockPos(pos.getX()+1, pos.getY(), pos.getZ());
                        BlockPos blockPos2 = new BlockPos(pos.getX()-1, pos.getY(), pos.getZ());
                        BlockPos blockPos3 = new BlockPos(pos.getX(), pos.getY(), pos.getZ()+1);
                        BlockPos blockPos4 = new BlockPos(pos.getX(), pos.getY(), pos.getZ()-1);
                        BlockPos[] blockPos = {blockPos1,blockPos2,blockPos3,blockPos4};
                        boolean flag = true;
                        ItemStack getItem = new ItemStack(finalProduct.getItem());
                        getItem.getOrCreateTag().putBoolean("cauldrons",true);
                        for (BlockPos pos3:blockPos){
                            if (!(world.getBlockEntity(pos3) instanceof IInventory))continue;
                            IInventory inventory = (IInventory) world.getBlockEntity(pos3);
                            if (inventory == null)continue;
                            int i = 0 ;
                            for ( ; i<=inventory.getContainerSize(); i++){
                                if (i == inventory.getContainerSize())break;
                                if (inventory.getItem(i).isEmpty()||(inventory.getItem(i).sameItem(getItem)&&inventory.getItem(i).getCount()<64))break;
                            }
                            if (i == inventory.getContainerSize())continue;
                            getItem.setCount(inventory.getItem(i).getCount()+1);
                            inventory.setItem(i,getItem);
                            flag = false;
                        }
                        if (flag)InventoryHelper.dropItemStack(world,pos.getX(),pos.getY()+1,pos.getZ(), getItem);
                        world.setBlock(pos,world.getBlockState(pos).setValue(BlockStateProperties.LEVEL_CAULDRON,0),3);
                        world.setBlock(pos2,world.getBlockState(pos2).setValue(BlockStateProperties.OPEN,true),3);
                        for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.remove();}
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }.start((int) recipe.getCookTime());
            }

        }
    }
    @SubscribeEvent
    public static void onPlayerPickUpItemEvent(EntityItemPickupEvent event){
        ItemEntity itemEntity = event.getItem();
        if (itemEntity.getTags().contains("dyn")){
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void playerEatEvent(LivingEntityUseItemEvent.Finish event){
        ItemStack itemStack = event.getItem();
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof PlayerEntity))return;
        PlayerEntity player = (PlayerEntity) entity;
        if (!itemStack.getOrCreateTag().contains("cauldrons"))return;
        if (!itemStack.getOrCreateTag().getBoolean("cauldrons"))return;
        if (itemStack.getItem().getFoodProperties()==null)return;
        int nutrition = itemStack.getItem().getFoodProperties().getNutrition();
        float saturationModifier = itemStack.getItem().getFoodProperties().getSaturationModifier();
        player.getCapability(CapabilityRegistryHandler.CAU).ifPresent(cap->{
            int x = cap.getDuration();
            float nutrition_ = (float) (nutrition*((-20/(x+10.76))+2));
            float saturation = (float) (saturationModifier*((-20/(x+10.76))+2));
            int tickPs = 20;
            int time = Math.max(0,Math.min(tickPs*30*x,600*tickPs));
            int effectLevel = (int) Math.max(0,Math.floor((-72D/(x+7D))+10D));
            int foodLevel = (int) Math.max(player.getFoodData().getFoodLevel(),Math.min(Math.floor(nutrition_ + player.getFoodData().getFoodLevel()),20));
            float saturationLevel = Math.min(saturation/5 + player.getFoodData().getSaturationLevel(),20);
            player.getFoodData().eat(foodLevel,saturationLevel);
            if (time != 0){
                player.addEffect(new EffectInstance(Effects.LUCK, time, effectLevel));
                player.addEffect(new EffectInstance(Effects.HEALTH_BOOST, effectLevel));
            }
            if (cap.isAddable())cap.addDuration();
            TranslationTextComponent component = new TranslationTextComponent("message."+Utils.MOD_ID+".eat");
            if (cap.isAddable())
                player.sendMessage(component.append(Integer.toString(x)),player.getUUID());
            cap.setAddable(false);
        });
    }
    @SubscribeEvent
    public static void dayCycleEvent(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.END){
            PlayerEntity player = event.player;
            World world = player.level;
            boolean isDay = world.isDay();
            player.getCapability(CapabilityRegistryHandler.CAU).ifPresent(cap->{
                byte day = cap.getDay();
                if (!isDay&&day==0)cap.turnDay();
                else if (isDay&&day==1)cap.turnDay();
            });
        }
    }
}
