package com.rbi.service.impl

import com.rbi.repository.FileMetadataRepository
import com.rbi.exception.StorageException
import com.rbi.model.FileMetadataEntity
import com.rbi.service.VideoService
import com.rbi.util.Range
import io.ktor.http.content.*
import java.io.ByteArrayInputStream
import java.util.*

class DefaultVideoService(
    private val storageService: MinioStorageService,
    private val fileMetadataRepository: FileMetadataRepository
) : VideoService {

    override suspend fun save(video: PartData.FileItem): UUID {
        val fileUUID = UUID.randomUUID()
        val videoBytes = ByteArrayInputStream(video.streamProvider().readBytes())

        val metadata = FileMetadataEntity(
            fileUUID.toString(),
            videoBytes.available().toLong(),
            video.contentType.toString()
        )

        fileMetadataRepository.save(metadata)
        storageService.save(video, videoBytes, fileUUID)

        return fileUUID
    }

    override suspend fun fetchChunk(uuid: UUID, range: Range): ChunkWithMetadata {
        val fileMetadata = fileMetadataRepository.findById(uuid)
        return ChunkWithMetadata(fileMetadata!!, readChunk(uuid, range, fileMetadata.size))
    }

    override fun getFullVideo(uuid: UUID): ByteArray {
        return storageService.getObject(uuid.toString())
    }

    private fun readChunk(uuid: UUID, range: Range, fileSize: Long): ByteArray {
        val startPosition = range.getRangeStart()
        val endPosition = range.getRangeEnd(fileSize)
        val chunkSize = (endPosition - startPosition + 1)

        try {
            storageService.getInputStream(uuid, startPosition, chunkSize).use { inputStream ->
                return inputStream.readAllBytes()
            }
        } catch (exception: Exception) {
            throw StorageException(exception)
        }
    }

    data class ChunkWithMetadata(
        val metadata: FileMetadataEntity,
        val chunk: ByteArray
    )
}