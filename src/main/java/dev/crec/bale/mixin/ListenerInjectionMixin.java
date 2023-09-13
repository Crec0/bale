package dev.crec.bale.mixin;

import dev.crec.bale.BaleListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1")
public class ListenerInjectionMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            method = "initChannel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;configureSerialization(Lio/netty/channel/ChannelPipeline;Lnet/minecraft/network/protocol/PacketFlow;)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectChannel(Channel channel, CallbackInfo ci, ChannelPipeline pipeline) {
        pipeline.addAfter("legacy_query", "bale", new BaleListener());
    }
}
