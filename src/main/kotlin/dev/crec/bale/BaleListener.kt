package dev.crec.bale

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.nio.charset.Charset

class BaleListener : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, buffer: Any) {
        val byteBuf = buffer as ByteBuf
        byteBuf.markReaderIndex()

        val wasUnwantedConnection = processPacket(ctx, byteBuf)

        if (wasUnwantedConnection) {
            /*
            * If the connection is not a bale client connection,
            * We don't want to keep firing this channel for this connection.
            * Therefore, we simply remove it from the pipeline.
            */
            byteBuf.resetReaderIndex()
            ctx.channel().pipeline().remove(this)
            ctx.fireChannelRead(buffer)
        }
    }

    private fun processPacket(ctx: ChannelHandlerContext, buffer: ByteBuf): Boolean {
         println(buffer.readBytes(buffer.readableBytes()).toString(Charset.defaultCharset()))
        this.reply(ctx) {
            "bale  test"
        }

        return true
    }

    private fun reply(ctx: ChannelHandlerContext, bodyProvider: () -> String) {
        val body = bodyProvider()

        val byteBuf = Unpooled.buffer().apply {
            this.writeBytes(body.toByteArray())
        }

        ctx.writeAndFlush(byteBuf)
    }
}
