package com.rbi

import com.rbi.repository.DatabaseFactory
import com.rbi.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.partialcontent.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment.config)

    configureTemplating()
    configureRouting()

    install(PartialContent)
}