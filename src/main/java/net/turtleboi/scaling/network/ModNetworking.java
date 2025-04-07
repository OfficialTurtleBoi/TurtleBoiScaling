package net.turtleboi.scaling.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.turtleboi.scaling.Scaling;
import net.turtleboi.turtlecore.network.packet.util.experience.ExperienceHandler;

import java.util.function.Supplier;

public class ModNetworking {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register () {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Scaling.MOD_ID, "networking"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        //net.messageBuilder(RemoveExperienceC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
        //        .decoder(RemoveExperienceC2SPacket::new)
        //        .encoder(RemoveExperienceC2SPacket::toBytes)
        //        .consumerMainThread(RemoveExperienceC2SPacket::handle)
        //        .add();


    }

    public static <MSG> void sendToPlayer (MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers (MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToNear (MSG message, LivingEntity livingEntity) {
        double x = livingEntity.getX();
        double y = livingEntity.getY();
        double z = livingEntity.getZ();
        double r2 = Minecraft.getInstance().options.renderDistance().get() * 16;
        INSTANCE.send(PacketDistributor.NEAR.with(() ->
                        new PacketDistributor.TargetPoint(x, y, z, r2, livingEntity.level().dimension())),
                message);
    }

    public static <MSG> void sendToServer (MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static Supplier<ExperienceHandler> experienceHandlerSupplier;

    public static ExperienceHandler getExperienceHandler() {
        if (experienceHandlerSupplier == null) {
            throw new IllegalStateException("ExperienceHandler not provided");
        }
        return experienceHandlerSupplier.get();
    }
}
