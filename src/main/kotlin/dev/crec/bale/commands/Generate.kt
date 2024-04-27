package dev.crec.bale.commands

import com.mojang.brigadier.CommandDispatcher
import dev.crec.bale.crypto.Crypto
import dev.crec.bale.crypto.Crypto.base64
import dev.crec.bale.crypto.Crypto.writeToFile
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object Generate {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("bale").then(
                literal("generate").executes { ctx ->
                    val key = Crypto.genKey()

                    key.writeToFile()
                        .onSuccess {
                            ctx.source.sendSuccess({
                                Component.literal(
                                    """
                                    |Key Regenerated. Click this message to copy to clipboard.
                                    |In case you can't use click event, You can copy the key from file at .config/bale/key.txt
                                    """.trimMargin()
                                ).setStyle(
                                    Style.EMPTY.withClickEvent(
                                        ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, key.base64())
                                    )
                                )
                            }, true)
                        }
                        .onFailure { e ->
                            ctx.source.sendFailure(
                                Component.literal("Failed to write key to a file.").setStyle(
                                    Style.EMPTY.withColor(ChatFormatting.RED)
                                )
                            )
                        }

                    1
                }
            )
        )
    }
}