package io.github.hrtzee.BetterCauldrons.Events;

import io.github.hrtzee.BetterCauldrons.Capability.CapabilityRegistryHandler;
import io.github.hrtzee.BetterCauldrons.Config;
import io.github.hrtzee.BetterCauldrons.Recipes.Recipes;
import io.github.hrtzee.BetterCauldrons.Recipes.Stuff;
import io.github.hrtzee.BetterCauldrons.Utils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
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
        int[] pro = {0,0,0,0,0,0,0,0,0};
        int rate = 64;
        BlockPos pos1 = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());
        BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());
        Recipes recipe = Recipes.EMPTY;
        ItemStack product = new ItemStack(Items.STONE_SWORD);
        if (!(world.getBlockState(pos).is(Blocks.CAULDRON)))return;
        if (!(world.getBlockState(pos1).getBlock() instanceof AbstractFireBlock||world.getBlockState(pos1).getBlock() instanceof CampfireBlock))return;
        if (world.getBlockState(pos1).getBlock() instanceof CampfireBlock)if (!world.getBlockState(pos1).getValue(BlockStateProperties.LIT))return;
        if (!(world.getBlockState(pos2).getBlock() instanceof TrapDoorBlock))return;
        if (itemStack.getItem() instanceof ShovelItem && !world.getBlockState(pos2).getValue(BlockStateProperties.OPEN)){
            List<Entity> entities = world.getEntities(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ()), AxisAlignedBB.of(new MutableBoundingBox(pos.getX(),pos.getY(),pos.getZ(),pos.getX(),pos.getY(),pos.getZ())));
            for (Recipes recipes : Recipes.values()) {
                if (recipes.equals(Recipes.EMPTY)) continue;
                if (!(entities.size()>20)){
                    for (Entity entity : entities) {
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
                                        itemEntity.addTag("dyn");
                                        pro[i] = itemEntity.getItem().getCount();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 9; i++) {
                    if (!progress[i]) {
                        flag_ = false;
                        for (int j = 0; j < 9; j++) progress[j] = false;//重置progress
                        for (Entity entity:entities)if (entity.getTags().contains("dyn"))entity.removeTag("dyn");
                        break;
                    }
                }
                if (flag_) {
                    recipe = recipes;
                    product = recipe.getProduct();
                    flag = true;
                    for (int i=0; i<9; i++){
                        if (pro[i]==0)continue;
                        rate = Math.min(rate,pro[i]);
                    }
                    break;
                } else {
                    flag_ = true;
                }
            }
            if (flag){
                if (recipe.isNeedBowl()){
                    for (Entity entity:entities){
                        if (entity instanceof ItemEntity) {
                            ItemEntity itemEntity = (ItemEntity) entity;
                            if (itemEntity.getItem().sameItem(Items.BOWL.getDefaultInstance())) {
                                itemEntity.addTag("dyn");
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
                final ItemStack finalProduct = product;
                int finalRate = rate;
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
                            if (!world.getBlockState(pos).is(Blocks.CAULDRON)||!(world.getBlockState(pos2).getBlock() instanceof TrapDoorBlock)||(world.getBlockState(pos1).getBlock() instanceof AbstractFireBlock || (!(world.getBlockState(pos1).getBlock() instanceof CampfireBlock)) || !world.getBlockState(pos1).getValue(BlockStateProperties.LIT))||world.getBlockState(pos2).getValue(BlockStateProperties.OPEN)){
                                MinecraftForge.EVENT_BUS.unregister(this);
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
                        getItem.setCount(finalRate);
                        getItem.getOrCreateTag().putBoolean("cauldrons",true);
                        getItem.getOrCreateTag().putBoolean("rec",true);
                        for (BlockPos pos3:blockPos){
                            if (!(world.getBlockEntity(pos3) instanceof IInventory))continue;
                            IInventory inventory = (IInventory) world.getBlockEntity(pos3);
                            if (inventory == null)continue;
                            int i = 0 ;
                            for ( ; i<=inventory.getContainerSize(); i++){
                                if (i == inventory.getContainerSize())break;
                                if (inventory.getItem(i).isEmpty()||(inventory.getItem(i).sameItem(getItem)&&inventory.getItem(i).getCount()<(64- finalRate)))break;
                            }
                            if (i == inventory.getContainerSize())continue;
                            getItem.setCount(inventory.getItem(i).getCount()+ finalRate);
                            inventory.setItem(i,getItem);
                            flag = false;
                        }
                        if (flag)InventoryHelper.dropItemStack(world,pos.getX(),pos.getY()+1,pos.getZ(), getItem);
                        world.setBlock(pos,world.getBlockState(pos).setValue(BlockStateProperties.LEVEL_CAULDRON,0),3);
                        world.setBlock(pos2,world.getBlockState(pos2).setValue(BlockStateProperties.OPEN,true),3);
                        for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.remove();}
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }.start(recipe.getCookTime() *rate);
            }else{
                ArrayList<ItemStack> itemStacks = new ArrayList<>();
                for (Entity entity:entities){
                    if (!(entity instanceof ItemEntity))continue;
                    ItemEntity itemEntity = (ItemEntity) entity;
                    itemStacks.add(itemEntity.getItem());
                    entity.addTag("dyn");
                }
                for (Entity entity:entities){
                    if (entity instanceof ItemEntity) {
                        ItemEntity itemEntity = (ItemEntity) entity;
                        if (itemEntity.getItem().sameItem(Items.BOWL.getDefaultInstance())) {
                            itemEntity.addTag("dyn");
                            bowl = true;
                            break;
                        }
                    }
                }
                if (!bowl)return;
                if (itemStacks.isEmpty())return;
                ArrayList<String> tags = new ArrayList<>();
                for (ItemStack itemStack1:itemStacks){
                    for (Stuff stuff:Stuff.values()){
                        if (stuff.getItemStacks().contains(itemStack1)){
                            if (!tags.contains(stuff.getTag())){
                                tags.add(stuff.getTag());
                            }
                        }
                    }
                }
                int finalRate = tags.size() + 1;
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
                            if (Math.random()<0.001&&tags.contains("explosion")){
                                world.destroyBlock(pos,true);
                                world.explode(entities.get(0),pos.getX(), pos.getY(),pos.getZ(),4.0F, Explosion.Mode.BREAK);
                                for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.removeTag("dyn");}
                                MinecraftForge.EVENT_BUS.unregister(this);
                                return;
                            }
                            if (!world.getBlockState(pos).is(Blocks.CAULDRON)||!(world.getBlockState(pos2).getBlock() instanceof TrapDoorBlock)||(world.getBlockState(pos1).getBlock() instanceof AbstractFireBlock || (!(world.getBlockState(pos1).getBlock() instanceof CampfireBlock)) || !world.getBlockState(pos1).getValue(BlockStateProperties.LIT))||world.getBlockState(pos2).getValue(BlockStateProperties.OPEN)){
                                MinecraftForge.EVENT_BUS.unregister(this);
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
                        ItemStack getItem = new ItemStack(Items.SUSPICIOUS_STEW);
                        getItem.getOrCreateTag().putBoolean("cauldrons",true);
                        getItem.getOrCreateTag().putBoolean("rec",false);
                        for (Stuff stuff:Stuff.values()){
                            getItem.getOrCreateTag().putBoolean(stuff.getTag(),tags.contains(stuff.getTag()));
                        }
                        for (BlockPos pos3:blockPos){
                            if (!(world.getBlockEntity(pos3) instanceof IInventory))continue;
                            IInventory inventory = (IInventory) world.getBlockEntity(pos3);
                            if (inventory == null)continue;
                            int i = 0 ;
                            for ( ; i<=inventory.getContainerSize(); i++){
                                if (i == inventory.getContainerSize())break;
                                if (inventory.getItem(i).isEmpty()||(inventory.getItem(i).sameItem(getItem)&&inventory.getItem(i).getCount()<=64))break;
                            }
                            if (i == inventory.getContainerSize())continue;
                            getItem.setCount(inventory.getItem(i).getCount()+ 1);
                            inventory.setItem(i,getItem);
                            flag = false;
                        }
                        if (flag)InventoryHelper.dropItemStack(world,pos.getX(),pos.getY()+1,pos.getZ(), getItem);
                        world.setBlock(pos,world.getBlockState(pos).setValue(BlockStateProperties.LEVEL_CAULDRON,0),3);
                        world.setBlock(pos2,world.getBlockState(pos2).setValue(BlockStateProperties.OPEN,true),3);
                        for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.remove();}
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }.start((int) (100 * finalRate * Config.COOK_RATE.get()));
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
            if (cap.isAddable())cap.addDuration();
            TranslationTextComponent component = new TranslationTextComponent("message."+Utils.MOD_ID+".eat");
            if (cap.isAddable())
                player.sendMessage(component.append(Integer.toString(cap.getDuration())),player.getUUID());
            cap.setAddable(false);
            int x = cap.getDuration();
            int x_ = x - 3;
            int x__ = x_ - 3;
            int x___ = x__ - 3;
            float nutrition_ = (float) (nutrition*((-20/(x+10.76))+2));
            float saturation = (float) (saturationModifier*((-20/(x+10.76))+2));
            int tickPs = 20;
            int time = Math.max(0,Math.min(tickPs*30*x,600*tickPs));
            int effectLevel = (int) Math.max(0,Math.floor((-72D/(x+7D))+10D));
            int foodLevel = (int) Math.max(player.getFoodData().getFoodLevel(),Math.min(Math.floor(nutrition_ + player.getFoodData().getFoodLevel())/3,20));
            float saturationLevel = Math.min(saturation/5 + player.getFoodData().getSaturationLevel()/3,20);
            player.getFoodData().eat(foodLevel,saturationLevel);
            if (itemStack.getOrCreateTag().getBoolean("rec")){
                if (time != 0) {
                    int time_ = Math.max(0,Math.min(tickPs*30*x_,600*tickPs));
                    int time__ = Math.max(0,Math.min(tickPs*30*x__,600*tickPs));
                    int time___ = Math.max(0,Math.min(tickPs*30*x___,600*tickPs));
                    int effectLevel_ = (int) Math.max(0,Math.floor((-72D/(x_+7D))+10D));
                    int effectLevel__ = (int) Math.max(0,Math.floor((-72D/(x__+7D))+10D));
                    int effectLevel___ = (int) Math.max(0,Math.floor((-72D/(x___+7D))+10D));
                    if (x > 0) player.addEffect(new EffectInstance(Effects.LUCK, Math.min(time,1800), Math.min(effectLevel,3)));
                    if (x > 0) player.addEffect(new EffectInstance(Effects.NIGHT_VISION, Math.min(time,1800), Math.min(effectLevel,3)));
                    if (x_ > 0) player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, Math.min(time_,1800), Math.min(effectLevel_,3)));
                    if (x_ > 0) player.addEffect(new EffectInstance(Effects.JUMP, Math.min(time_,1800), Math.min(effectLevel_,3)));
                    if (x__ > 0) player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, Math.min(time__,1800), Math.min(effectLevel__,3)));
                    if (x__ > 0) player.addEffect(new EffectInstance(Effects.DIG_SPEED, Math.min(time__,1800), Math.min(effectLevel__,3)));
                    if (x___ > 0) player.addEffect(new EffectInstance(Effects.ABSORPTION, Math.min(time___,1800), Math.min(effectLevel___,3)));
                    if (x___ > 0) player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, Math.min(time___,1800), Math.min(effectLevel___,3)));

                }
            }
            if (!itemStack.getOrCreateTag().getBoolean("rec")){
                if (time != 0){
                    CompoundNBT tag = itemStack.getOrCreateTag();
                    boolean turn = tag.getBoolean("sp");
                    effectLevel = Math.max(0,(int) Math.floor(effectLevel/3D));
                    time = Math.max(0,(int) Math.floor(time/1.5D));
                    if (tag.getBoolean("plus")){
                        effectLevel = effectLevel*2;
                    }
                    if (tag.getBoolean("long")){
                        time = time*2;
                    }
                    boolean flag = false;
                    if (turn){
                        for (Stuff stuff:Stuff.values()){
                            String s = stuff.getTag();
                            if (s.equals("plus")||s.equals("long")||s.equals("null")||s.equals("explosion")){
                                continue;
                            }
                            if (tag.getBoolean(s)){
                                flag=true;
                                break;
                            }
                        }
                    }
                    if (turn&&flag){
                        effectLevel = Math.max(0,effectLevel/4);
                        time = Math.max(30,time/3);
                    }
                    if (flag||!turn){
                        for (Stuff stuff : Stuff.values()) {
                            if (tag.getBoolean(stuff.getTag())) {
                                switch (stuff.getTag()) {
                                    case "speed":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.MOVEMENT_SPEED):(Effects.MOVEMENT_SLOWDOWN),time,effectLevel));
                                        break;
                                    case "jump":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.JUMP):(Effects.MOVEMENT_SLOWDOWN),time,effectLevel));
                                        break;
                                    case "strength":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.DAMAGE_BOOST):(Effects.WEAKNESS),time,effectLevel));
                                        break;
                                    case "heal":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.HEAL):(Effects.HARM),time,effectLevel));
                                        break;
                                    case "poison":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.POISON):(Effects.HEAL),time,effectLevel));
                                        break;
                                    case "regeneration":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.REGENERATION):(Effects.POISON),time,effectLevel));
                                        break;
                                    case "fire":
                                        if (!turn) {
                                            player.addEffect(new EffectInstance((Effects.FIRE_RESISTANCE), time, effectLevel));
                                        }else {
                                            player.setSecondsOnFire((tag.getBoolean("long"))?15:30);
                                            if (tag.getBoolean("plus")){
                                                player.hurt(DamageSource.IN_FIRE,5F);
                                            }
                                        }
                                        break;
                                    case "water":
                                        if (!turn) {
                                            player.addEffect(new EffectInstance((Effects.WATER_BREATHING), time, effectLevel));
                                        }else {
                                            player.removeEffect(Effects.WATER_BREATHING);
                                            player.hurt(DamageSource.DROWN,(tag.getBoolean("plus"))?7F:15F);
                                        }
                                        break;
                                    case "night":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.NIGHT_VISION):(Effects.BLINDNESS),time,effectLevel));
                                        break;
                                    case "turtle":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.MOVEMENT_SLOWDOWN):(Effects.MOVEMENT_SPEED),time,effectLevel));
                                        player.addEffect(new EffectInstance(!turn?Effects.DAMAGE_RESISTANCE:Effects.WEAKNESS,time,effectLevel));
                                        break;
                                    case "slow_falling":
                                        player.addEffect(new EffectInstance((!turn)?(Effects.SLOW_FALLING):(Effects.LEVITATION),time,effectLevel));
                                        break;
                                }
                            }
                        }
                    }else {
                        player.addEffect(new EffectInstance(Effects.WEAKNESS,time,effectLevel));
                    }
                }
                if (itemStack.getOrCreateTag().getBoolean("explosion")){
                    player.level.explode(player,player.xo,player.yo,player.zo,2.5F,Explosion.Mode.DESTROY);
                }
            }
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
