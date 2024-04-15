package com.lowae.agrreader.data.model.account.security

import com.google.gson.Gson
import com.lowae.agrreader.utils.GsonUtils

abstract class SecurityKey {

    open val isValid: Boolean = true

    fun <T> decode(value: String?, classOfT: Class<T>): T =
        Gson().fromJson(DESUtils.decrypt(value?.ifEmpty { DESUtils.empty } ?: DESUtils.empty),
            classOfT)

    override fun toString(): String {
        return DESUtils.encrypt(GsonUtils.toJson(this))
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
