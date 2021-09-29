package io.github.hrtzee.BetterCauldrons.Networking;

import io.github.hrtzee.BetterCauldrons.Recipes.Recipes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ProductPacket {
    public String recipe;
    public double x;
    public double y;
    public double z;
    public ProductPacket(PacketBuffer buffer){
        this.recipe = buffer.readUtf();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }
    public ProductPacket(String recipe,double x,double y,double z){
        this.recipe = recipe;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void encode(PacketBuffer buffer){
        buffer.writeUtf(this.recipe);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }
    public static void handle(ProductPacket message, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> {
            final ServerPlayerEntity player = ctx.get().getSender();
            assert player != null;
            World world = player.level;
            Recipes recipe = Recipes.EMPTY;
            for (Recipes recipes:Recipes.values()){
                if (recipes.toString().equals(message.recipe)){
                    recipe = recipes;
                    break;
                }
            }
            player.sendMessage(new StringTextComponent(recipe.getProduct().toString()),player.getUUID());
            InventoryHelper.dropItemStack(world, message.x, message.y, message.z, new ItemStack(recipe.getProduct().getItem()));
        });
        ctx.get().setPacketHandled(true);
    }
}
