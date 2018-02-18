package dyson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec


object DysonCredentials {

    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val key: ByteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20)
    private val initVector: ByteArray = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
    private val skeySpec = SecretKeySpec(key, "AES")
    private val decryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private val encryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    private val decoder = Base64.getDecoder()
    private val encoder = Base64.getEncoder()

    init {
        decryptCipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(skeySpec.encoded, "AES"), IvParameterSpec(initVector))
        encryptCipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(skeySpec.encoded, "AES"), IvParameterSpec(initVector))
    }

    fun decrypt(encryptedValue: String): String {
        val decryptedBytes = decryptCipher.doFinal(decoder.decode(encryptedValue))
        return mapper.readTree(String(decryptedBytes))["apPasswordHash"].asText()
    }

    fun encrypt(value: String) : String {
        val json = """ { "apPasswordHash" : "$value" }"""
        val encryptedBytes = encoder.encode(encryptCipher.doFinal(json.toByteArray()))
        return String(encryptedBytes)
    }

}