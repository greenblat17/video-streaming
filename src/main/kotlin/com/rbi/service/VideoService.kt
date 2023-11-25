package com.rbi.service

import com.rbi.service.impl.DefaultVideoService
import com.rbi.util.Range
import io.ktor.http.content.*
import java.util.UUID

interface VideoService {

    suspend fun save(video: PartData.FileItem): UUID

    suspend fun fetchChunk(uuid: UUID, range: Range): DefaultVideoService.ChunkWithMetadata

    fun getFullVideo(uuid: UUID): ByteArray

}