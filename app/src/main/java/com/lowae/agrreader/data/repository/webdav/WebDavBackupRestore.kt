package com.lowae.agrreader.data.repository.webdav

import java.io.File

sealed interface WebDavBackupRestore {

    val file: File

    suspend fun backup()

    suspend fun restore()

}