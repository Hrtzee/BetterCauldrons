package io.github.hrtzee.BetterCauldrons.Networking;

import io.github.hrtzee.BetterCauldrons.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetWorking {
    public static SimpleChannel INSTANCE;
    private static final String VERSION = "0.1.0";
    private static int ID = 0;
    public static int nextID(){return ID++;}
    public static void registerMessage(){
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Utils.MOD_ID, "mage_networking"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION));
        INSTANCE.registerMessage(nextID(),ProductPacket.class,ProductPacket::encode,ProductPacket::new,ProductPacket::handle);
    }
    /*
    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }
    public static void sendTo(ServerPlayerEntity player, Object msg) {
        if (!(player instanceof FakePlayer)) {
            INSTANCE.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }

    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
    public static void sendTracking(Entity e, Object msg) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> e), msg);
    }

    public static void sendTrackingBlock(BlockPos p, World w, Object msg) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(p.getX(), p.getY(), p.getZ(), 128.0D, w.dimension())), msg);
    }

    static {
        NetworkRegistry.ChannelBuilder channelBuilder = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("mahoutsukai", "main_channel"));
        String var10001 = VERSION;
        var10001.getClass();
        channelBuilder = channelBuilder.clientAcceptedVersions(var10001::equals);
        var10001 = VERSION;
        var10001.getClass();
        INSTANCE = channelBuilder.serverAcceptedVersions(var10001::equals).networkProtocolVersion(() -> VERSION).simpleChannel();
    }
     */
}
