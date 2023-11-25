package com.rbi.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*
import java.util.*

fun Route.playerRouting() {
    route("/player") {
        get("/stream/{uuid}") {
            val uuid = UUID.fromString(call.parameters["uuid"])
            call.respond(ThymeleafContent(template = "stream", model = mapOf("id" to uuid)))
        }
    }
}