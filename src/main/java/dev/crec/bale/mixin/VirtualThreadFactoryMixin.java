package dev.crec.bale.mixin;

import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.concurrent.ThreadFactory;

@Mixin(value = ServerConnectionListener.class, remap = false)
public class VirtualThreadFactoryMixin {
    @ModifyArg(
            method = "method_14348",
            at = @At(value = "INVOKE", target = "Lio/netty/channel/nio/NioEventLoopGroup;<init>(ILjava/util/concurrent/ThreadFactory;)V"),
            index = 1
    )
    private static ThreadFactory factory(ThreadFactory factory) {
        return Thread.ofVirtual().factory();
    }

    @ModifyArg(
            method = "method_14349",
            at = @At(value = "INVOKE", target = "Lio/netty/channel/epoll/EpollEventLoopGroup;<init>(ILjava/util/concurrent/ThreadFactory;)V"),
            index = 1
    )
    private static ThreadFactory factory2(ThreadFactory factory) {
        return Thread.ofVirtual().factory();
    }
}
