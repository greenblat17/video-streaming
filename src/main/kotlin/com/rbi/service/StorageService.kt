package com.rbi.service

import io.ktor.http.content.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

interface StorageService {

    fun save(fileName: String, videoBytes: ByteArrayInputStream, uuid: UUID)

    fun getInputStream(fileName: String, offset: Long, length: Long): InputStream

    fun getObject(fileName: String): ByteArray

}