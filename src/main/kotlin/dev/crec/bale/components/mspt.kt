package dev.crec.bale.components

import kotlinx.html.MAIN
import kotlinx.html.div
import kotlinx.html.span
import net.minecraft.server.MinecraftServer
import net.minecraft.util.TimeUtil
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max

fun MAIN.msptCard() {
    div(classes = "p-4 w-32 bg-background/10 border-2 border-solid backdrop-blur rounded-lg flex flex-col") {
        span (classes = "font-bold") {
            +"MSPT"
        }
        span (classes = "text-xl") {
            attributes["hx-get"] = "/stats/mspt"
            attributes["hx-trigger"] = "every 1s"
            attributes["hx-swap"] = "innerHTML"
            +"-1.00"
        }
        span (classes = "font-bold mt-4") {
            +"TPS"
        }
        span (classes = "text-xl") {
            attributes["hx-get"] = "/stats/tps"
            attributes["hx-trigger"] = "every 1s"
            attributes["hx-swap"] = "innerHTML"
            +"-1.00"
        }
    }
}

fun calculateMSPT(serverRef: AtomicReference<MinecraftServer>): Float {
    if (serverRef.get() == null) return -1F
    return serverRef.get().averageTickTimeNanos.toFloat() / TimeUtil.NANOSECONDS_PER_MILLISECOND
}

fun calculateTPS(serverRef: AtomicReference<MinecraftServer>): Float {
    if (serverRef.get() == null) return -1F
    val tickRateManager = serverRef.get().tickRateManager()
    val maxIdealMSPT = if (tickRateManager.isSprinting) 0F else tickRateManager.millisecondsPerTick()
    return 1000F / max(maxIdealMSPT, calculateMSPT(serverRef))
}