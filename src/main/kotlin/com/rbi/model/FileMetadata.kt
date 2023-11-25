package com.rbi.model

import org.jetbrains.exposed.sql.Table
import java.io.Serializable

data class FileMetadataEntity(val id: String, val size: Long, val httpContentType: String) : Serializable

object FileMetadata : Table() {
    val id = varchar("id", 255)
    val size = integer("size")
    val httpContentType = varchar("http_content_type", 255)

    override val primaryKey = PrimaryKey(id)
}