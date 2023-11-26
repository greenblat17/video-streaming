package com.rbi.service

import io.ktor.http.content.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

interface StorageService {

    fun save(video: PartData.FileItem, videoBytes: ByteArrayInputStream, uuid: UUID)

    fun getInputStream(uuid: UUID, offset: Long, length: Long): InputStream

    fun getObject(uuid: String): ByteArray

}