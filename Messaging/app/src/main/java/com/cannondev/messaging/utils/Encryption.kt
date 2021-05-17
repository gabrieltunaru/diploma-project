package com.cannondev.messaging.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.security.crypto.MasterKey
import java.security.*
import javax.crypto.Cipher

object Encryption {
    val TAG = javaClass.simpleName

    fun init(context: Context) {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        Utils.saveToPrefs(context, "mainKey", mainKey.toString())
    }

    fun generate(): KeyPair? {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            "AndroidKeyStore"
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            "rsa",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            build()
        }

        kpg.initialize(parameterSpec)

        return kpg.generateKeyPair()
    }

    fun getPrivateKey(): PrivateKey? {
        val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        val entry: KeyStore.Entry = ks.getEntry("rsa", null)
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry")
            return null
        }
        return entry.privateKey
    }

    fun encryptMessage(text: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val enc = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(enc, Base64.DEFAULT)
    }


    fun decryptMessage(text: String, pvKey: PrivateKey): String {
        val decoded = Base64.decode(text, Base64.DEFAULT)
        val cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, pvKey)
        val enc = cipher.doFinal(decoded)
        return String(enc)
    }

}