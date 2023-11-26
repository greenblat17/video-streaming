package com.rbi.service.impl

import com.rbi.repository.FileMetadataRepository
import com.rbi.exception.StorageException
import com.rbi.model.FileMetadataEntity
import com.rbi.service.VideoService
import com.rbi.util.FileUtil.Companion.generateFileNameWithExtension
import com.rbi.util.FileUtil.Companion.getExtension
import com.rbi.util.Range
import io.ktor.http.content.*
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.collections.ArrayList

class DefaultVideoService(
    private val storageService: MinioStorageService,
    private val fileMetadataRepository: FileMetadataRepository
) : VideoService {

    suspend fun getAllFiles(): List<String> {
        val files = ArrayList<String>()

        val metadatas = fileMetadataRepository.findALl()
        metadatas.forEach {
            files.add(it!!.id)
        }

        return files
    }

    override suspend fun save(video: PartData.FileItem): UUID {
        val fileUUID = UUID.randomUUID()

        val videoBytes = ByteArrayInputStream(video.streamProvider().readBytes())
        val fileName = video.originalFileName.toString()

        val metadata = FileMetadataEntity(
            fileUUID.toString(),
            videoBytes.available().toLong(),
            getExtension(fileName),
            video.contentType.toString()
        )

        fileMetadataRepository.save(metadata)
        storageService.save(fileName, videoBytes, fileUUID)

        return fileUUID
    }

    override suspend fun fetchChunk(uuid: UUID, range: Range): ChunkWithMetadata {
        val fileMetadata = fileMetadataRepository.findById(uuid)
        val fileName = generateFileNameWithExtension(uuid, fileMetadata!!.extension)
        return ChunkWithMetadata(fileMetadata, readChunk(fileName, range, fileMetadata.size))
    }

    override fun getFullVideo(uuid: UUID): ByteArray {
        return storageService.getObject(uuid.toString())
    }

    private fun readChunk(fileName: String, range: Range, fileSize: Long): ByteArray {
        val startPosition = range.getRangeStart()
        val endPosition = range.getRangeEnd(fileSize)
        val chunkSize = (endPosition - startPosition + 1)

        try {
            storageService.getInputStream(fileName, startPosition, chunkSize).use { inputStream ->
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