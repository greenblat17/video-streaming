package com.rbi.plugins

import com.rbi.route.playerRouting
import com.rbi.route.videoRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        videoRouting()
        playerRouting()
    }
}
