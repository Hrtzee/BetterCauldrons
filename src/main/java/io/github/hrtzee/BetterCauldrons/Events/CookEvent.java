package io.github.hrtzee.BetterCauldrons.Events;

import io.github.hrtzee.BetterCauldrons.Networking.NetWorking;
import io.github.hrtzee.BetterCauldrons.Networking.ProductPacket;
import io.github.hrtzee.BetterCauldrons.Recipes.IRecipe;
import io.github.hrtzee.BetterCauldrons.Recipes.Recipes;
import io.github.hrtzee.BetterCauldrons.Recipes.TestEnum;
import io.github.hrtzee.BetterCauldrons.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Utils.MOD_ID)
public class CookEvent {
    @SubscribeEvent
    public static void turnCrystalEvent(PlayerInteractEvent.RightClickBlock event){
        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
        World world = event.getWorld();
        boolean flag = false;
        boolean flag_ = true;
        boolean[] progress = {false,false,false,false,false,false,false,false,false};
        BlockPos pos1 = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());
        BlockPos pos2 = new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());
        Recipes recipe = Recipes.EMPTY;
        ItemStack product = new ItemStack(Items.STONE_SWORD);
        if (!(world.getBlockState(pos).is(Blocks.CAULDRON)))return;
        if (!(world.getBlockState(pos1).is(Blocks.FIRE)))return;
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
                                    if (itemEntity.getItem().getItem().equals(recipes.getItem(i).getItem()))
                                        progress[i] = true;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 9; i++) {
                    if (!progress[i]) {
                        flag_ = false;
                        for (int j = 0; j < 9; j++) progress[j] = false;
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
            if (flag){
                player.swing(Hand.MAIN_HAND,true);
                itemStack.setDamageValue(itemStack.getDamageValue()+3);
                if (recipe.getConsume()>world.getBlockState(pos).getValue(BlockStateProperties.LEVEL_CAULDRON))return;
                for (Entity entity:entities) {if (entity instanceof ItemEntity) entity.addTag("dyn");}
                final ItemStack finalProduct = product;
                Recipes finalRecipe = recipe;
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
                                MinecraftForge.EVENT_BUS.unregister(this);
                                return;
                            }
                            if (this.ticks >= this.waitTicks)
                                run();
                        }
                    }
                    private void run() {
                        boolean flag__ = true;
                        world.addParticle(ParticleTypes.EXPLOSION,pos.getX(),pos.getY(),pos.getZ(),5,5,5);
                        world.playSound(null,pos, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS,1,1);
                        world.playSound(null,pos, SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 1,1);
                        BlockPos blockPos1 = new BlockPos(pos.getX()+1, pos.getY(), pos.getZ());
                        BlockPos blockPos2 = new BlockPos(pos.getX()-1, pos.getY(), pos.getZ());
                        BlockPos blockPos3 = new BlockPos(pos.getX(), pos.getY(), pos.getZ()+1);
                        BlockPos blockPos4 = new BlockPos(pos.getX(), pos.getY(), pos.getZ()-1);
                        /*if (world.getBlockState(new BlockPos(blockPos1)).is(Blocks.CHEST)&&world.getBlockEntity(blockPos1) instanceof IInventory){
                            IInventory inventory = (IInventory) world.getBlockEntity(blockPos1);
                            if (inventory!=null)
                                if (!inventory.canPlaceItem(1, finalRecipe.getProduct()))
                                    flag__ = true;
                            else {
                                flag__ = false;
                                inventory.setItem(1, );
                                inventory.setChanged();
                            }
                        }
                        else if ()
                         */
                        //NetWorking.INSTANCE.sendToServer(new ProductPacket(finalRecipe.toString(),pos.getX(),pos.getY()+1,pos.getZ()));
                        InventoryHelper.dropItemStack(world,pos.getX(),pos.getY()+1,pos.getZ(), new ItemStack(finalProduct.getItem()));
                        world.setBlock(pos,world.getBlockState(pos).setValue(BlockStateProperties.LEVEL_CAULDRON,0),3);
                        world.setBlock(pos2,world.getBlockState(pos2).setValue(BlockStateProperties.OPEN,true),3);
                        for (Entity entity:entities) {if (entity instanceof ItemEntity && entity.getTags().contains("dyn")) entity.remove();}
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }.start((int) 100);
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
}
