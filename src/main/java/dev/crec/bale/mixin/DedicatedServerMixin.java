package dev.crec.bale.mixin;

import dev.crec.bale.BaleKt;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method="initServer", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerConnectionListener;startTcpServerListener(Ljava/net/InetAddress;I)V"))
    private void startServer(CallbackInfoReturnable<Boolean> cir) {
        BaleKt.serve();
    }

    @Inject(method="stopServer", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;stopServer()V"))
    private void stopServer(CallbackInfo ci) {
        BaleKt.stop();
    }
}
