package com.rbi.service.impl

import com.rbi.config.MinioProperties
import com.rbi.service.StorageService
import io.ktor.http.content.*
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

class MinioStorageService(private val minioClient: MinioClient) : StorageService {

    private val putObjectPartSize = 5242880L

    override fun save(video: PartData.FileItem, videoBytes: ByteArrayInputStream, uuid: UUID) {
        val fileFullName = generateFileName(uuid, video.originalFileName!!)
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .`object`(fileFullName)
                .stream(videoBytes, videoBytes.available().toLong(), putObjectPartSize)
                .build()
        )
    }


    override fun getInputStream(uuid: UUID, offset: Long, length: Long): InputStream {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .offset(offset)
                .length(length)
                .`object`("$uuid.mp4")
                .build()
        )
    }

    override fun getObject(uuid: String): ByteArray {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .`object`("$uuid.mp4")
                .build()
        ).readAllBytes()
    }

    private fun generateFileName(uuid: UUID, originalFileName: String): String {
        return uuid.toString() + "." + getExtension(originalFileName)
    }

    private fun getExtension(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1)
    }
}