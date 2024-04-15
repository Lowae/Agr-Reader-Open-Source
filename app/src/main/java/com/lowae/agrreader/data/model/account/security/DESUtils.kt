package com.lowae.agrreader.data.model.account.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object DESUtils {

    const val empty = "CvJ1PKM8EW8="
    private const val secret = "mJn':4Nbk};AMVFGEWiY!(8&gp1xOv@/"

    fun encrypt(cleartext: String): String {
        return Base64.encodeToString(cleartext.toByteArray(), Base64.DEFAULT)
    }

    fun decrypt(ciphertext: String): String {
        val base64Decode = Base64.decode(ciphertext, Base64.DEFAULT)
        return try {
            val key = SecretKeyFactory
                .getInstance("DES")
                .generateSecret(DESKeySpec(secret.toByteArray()))

            Cipher.getInstance("DES").run {
                init(Cipher.DECRYPT_MODE, key)
                String(doFinal(base64Decode))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            String(base64Decode)
        }
    }
}
