package com.rbi.route

import com.rbi.config.MinioConfig
import com.rbi.repository.FileMetadataRepositoryImpl
import com.rbi.service.impl.DefaultVideoService
import com.rbi.service.impl.MinioStorageService
import com.rbi.util.HeaderUtil.Companion.calculateContentLengthHeader
import com.rbi.util.HeaderUtil.Companion.constructContentRangeHeader
import com.rbi.util.Range
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import java.util.UUID


fun Route.videoRouting() {
    val minioClient = MinioConfig.init()

    val storageService = MinioStorageService(minioClient)
    val fileMetadataDao = FileMetadataRepositoryImpl()
    val videoService = DefaultVideoService(storageService, fileMetadataDao)

    val defaultChunkSize = environment!!.config
        .property("app.streaming.defaultChunkSize")
        .getString()
        .toInt()

    route("/video") {

        get("/upload") {
            call.respond(ThymeleafContent("upload", model = emptyMap()))
        }

        get("/full/{uuid}") {
            val uuid = UUID.fromString(call.parameters["uuid"])

            val videoBytes = videoService.getFullVideo(uuid)

            call.respondBytes(
                bytes = videoBytes,
                contentType = call.request.contentType(),
                status = HttpStatusCode.OK
            )
        }

        get("/chunk/{uuid}") {
            val uuid = UUID.fromString(call.parameters["uuid"])
            val range = call.request.headers["Range"]

            val parsedRange = Range.parseHttpRangeString(range, defaultChunkSize)
            val chunkWithMetadata = videoService.fetchChunk(uuid, parsedRange)

            call.response.header("Content-Type", chunkWithMetadata.metadata.httpContentType)
            call.response.header("Accept-Ranges", "bytes")
            call.response.header(
                "Content-Length",
                calculateContentLengthHeader(parsedRange, chunkWithMetadata.metadata.size)
            )
            call.response.header(
                "Content-Range",
                constructContentRangeHeader(parsedRange, chunkWithMetadata.metadata.size)
            )

            call.respondBytes(
                status = HttpStatusCode.PartialContent,
                bytes = chunkWithMetadata.chunk,
            )

        }

        post {
            val form = call.receiveMultipart()
            val part = form.readPart()

            if (part is PartData.FileItem) {
                call.respondRedirect("/video/upload")
            } else {
                call.respond(ThymeleafContent("error", model = emptyMap()))
            }

        }

    }

}


