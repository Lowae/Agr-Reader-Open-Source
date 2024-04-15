package com.lowae.agrreader.data.model.account.security

class FreshRSSSecurityKey private constructor() : GoogleReaderSecurityKey() {

    constructor(serverUrl: String?, username: String?, password: String?) : this() {
        this.serverUrl = serverUrl
        this.username = username
        this.password = password
    }

    constructor(value: String? = DESUtils.empty) : this() {
        decode(value, FreshRSSSecurityKey::class.java).let {
            serverUrl = it.serverUrl
            username = it.username
            password = it.password
        }
    }
}
