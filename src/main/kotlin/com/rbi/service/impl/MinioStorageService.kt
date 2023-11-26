package com.rbi.service.impl

import com.rbi.config.MinioProperties
import com.rbi.service.StorageService
import com.rbi.util.FileUtil.Companion.generateFileName
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

class MinioStorageService(private val minioClient: MinioClient) : StorageService {

    private val putObjectPartSize = 5242880L

    override fun save(fileName: String, videoBytes: ByteArrayInputStream, uuid: UUID) {
        val fileFullName = generateFileName(uuid, fileName)
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .`object`(fileFullName)
                .stream(videoBytes, videoBytes.available().toLong(), putObjectPartSize)
                .build()
        )
    }


    override fun getInputStream(fileName: String, offset: Long, length: Long): InputStream {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .offset(offset)
                .length(length)
                .`object`(fileName)
                .build()
        )
    }

    override fun getObject(fileName: String): ByteArray {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(MinioProperties.BUCKET_NAME)
                .`object`(fileName)
                .build()
        ).readAllBytes()
    }

}