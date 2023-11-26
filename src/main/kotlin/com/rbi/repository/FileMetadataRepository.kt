package com.rbi.repository

import com.rbi.model.FileMetadataEntity
import java.util.UUID

interface FileMetadataRepository {
    suspend fun findById(id: UUID): FileMetadataEntity?
    suspend fun save(fileMetadataEntity: FileMetadataEntity): FileMetadataEntity?
    suspend fun findALl() :List<FileMetadataEntity?>
}