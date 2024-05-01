package dev.crec.bale.crypto

import dev.crec.bale.configFile
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.io.path.readText
import kotlin.io.path.writeText


private const val ALGORITHM = "AES/GCM/NoPadding"
private const val AES_KEY_BYTES = 256
private const val TAG_LENGTH_BITS = 128
private const val IV_LENGTH_BYTES = 128 / 8

object Crypto {

    fun genKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(AES_KEY_BYTES, SecureRandom.getInstanceStrong())

        return keyGen.generateKey()
    }

    private fun genNonce(): ByteArray {
        val nonce = ByteArray(IV_LENGTH_BYTES)
        SecureRandom().nextBytes(nonce)

        return nonce
    }

    fun encrypt(text: String, key: SecretKey): ByteArray {
        val iv = genNonce()

        val encryptedBytes = Cipher.getInstance(ALGORITHM).run {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_LENGTH_BITS, iv))
            doFinal(text.encodeToByteArray())
        }

        return ByteBuffer.allocate(iv.size + encryptedBytes.size).run {
            put(iv)
            put(encryptedBytes)
            array()
        }
    }

    fun decrypt(encrypted: ByteArray, key: SecretKey): ByteArray {
        val buffer = ByteBuffer.wrap(encrypted)
        val iv = ByteArray(IV_LENGTH_BYTES).apply { buffer.get(this) }
        val textBytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }

        val decryptedBytes = Cipher.getInstance(ALGORITHM).run {
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
            init(Cipher.DECRYPT_MODE, key, gcmSpec)

            doFinal(textBytes)
        }

        return decryptedBytes
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun SecretKey.base64() = Base64.encode(this.encoded)

    fun SecretKey.writeToFile() = runCatching {
        configFile.writeText(this.base64())
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun readFromFile() = runCatching {
        val key = configFile.readText()

        SecretKeySpec(Base64.decode(key), "AES")
    }
}
