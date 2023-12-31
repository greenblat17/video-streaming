package com.rbi.repository

import com.rbi.model.FileMetadata
import com.rbi.repository.DatabaseFactory.dbQuery
import com.rbi.model.FileMetadataEntity
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.*

class FileMetadataRepositoryImpl : FileMetadataRepository {

    private fun resultRowToEntity(row: ResultRow) = FileMetadataEntity(
        id = row[FileMetadata.id],
        size = row[FileMetadata.size].toLong(),
        extension = row[FileMetadata.extension],
        httpContentType = row[FileMetadata.httpContentType],
    )

    override suspend fun findById(id: UUID): FileMetadataEntity? = dbQuery {
        FileMetadata.select { FileMetadata.id eq id.toString() }
            .map(::resultRowToEntity)
            .singleOrNull()
    }

    override suspend fun save(fileMetadataEntity: FileMetadataEntity): FileMetadataEntity = dbQuery {
        val insertStatement = FileMetadata.insert {
            it[id] = fileMetadataEntity.id
            it[size] = fileMetadataEntity.size.toInt()
            it[extension] = fileMetadataEntity.extension
            it[httpContentType] = fileMetadataEntity.httpContentType
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEntity)!!
    }

    override suspend fun findALl(): List<FileMetadataEntity?> = dbQuery {
        FileMetadata.selectAll()
            .map(::resultRowToEntity)
    }


}