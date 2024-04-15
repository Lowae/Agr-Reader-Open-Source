package com.lowae.agrreader.data.repository.webdav

import com.lowae.agrreader.AgrReaderApp
import com.lowae.agrreader.data.repository.OpmlRepository
import com.lowae.agrreader.utils.RLog
import java.io.File
import java.io.FileInputStream

class OpmlBackupRestore(private val accountId: Int, private val opmlRepository: OpmlRepository) :
    WebDavBackupRestore {

    override val file: File = File("${AgrReaderApp.application.cacheDir}/backup/AgrReader.opml")

    override suspend fun backup() {
        file.writeText(opmlRepository.saveToString(accountId))
    }

    override suspend fun restore() {
        RLog.d("WebDavBackupRestore", "OpmlBackupRestore restore")
        opmlRepository.parserFeedFromFile2(FileInputStream(file))
            .join()
        RLog.d("WebDavBackupRestore", "OpmlBackupRestore restore end")
    }

}