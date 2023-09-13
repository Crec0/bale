package dev.crec.bale.packets

interface Packet {
    val buffer: ByteArray
    val type: PacketType

    fun pack(): ByteArray
}
