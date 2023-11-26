package com.rbi.util

import java.util.*

class FileUtil {

    companion object {
        fun generateFileNameWithExtension(uuid: UUID, extension: String): String {
            return "$uuid.$extension"
        }

        fun generateFileName(uuid: UUID, originalFileName: String): String {
            return generateFileNameWithExtension(uuid, getExtension(originalFileName))
        }

        fun getExtension(fileName: String): String {
            return fileName.substring(fileName.lastIndexOf(".") + 1)
        }
    }
}