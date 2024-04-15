package com.lowae.agrreader.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {

    fun zip(outputZipFile: File, filesToCompress: List<File>) {
        val buffer = ByteArray(1024)

        FileOutputStream(outputZipFile).use { fos ->
            ZipOutputStream(fos).use { zos ->
                for (file in filesToCompress) {
                    FileInputStream(file).use { fis ->
                        val entry = ZipEntry(file.name)
                        zos.putNextEntry(entry)

                        var length: Int
                        while (fis.read(buffer).also { length = it } > 0) {
                            zos.write(buffer, 0, length)
                        }

                        zos.closeEntry()
                    }
                }
            }
        }
    }


    fun unzip(inputStream: ZipInputStream, destinationDirectory: File) {
        destinationDirectory.mkdirs()

        var entry: ZipEntry?
        val buffer = ByteArray(1024)

        while (inputStream.nextEntry.also { entry = it } != null) {
            val outputFile = File(destinationDirectory, entry?.name ?: continue)
            ensureZipPathSafety(outputFile, destinationDirectory)
            if (entry?.isDirectory == true) {
                outputFile.mkdirs()
            } else {
                FileOutputStream(outputFile).use { output ->
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
        }
    }


    @Throws(Exception::class)
    private fun ensureZipPathSafety(outputFile: File, destDirectory: File) {
        val destDirCanonicalPath = destDirectory.canonicalPath
        val outputFileCanonicalPath = outputFile.canonicalPath
        if (!outputFileCanonicalPath.startsWith(destDirCanonicalPath)) {
            throw Exception("Found Zip Path Traversal Vulnerability with $outputFileCanonicalPath")
        }
    }
}