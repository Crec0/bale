package dev.crec.bale

import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val SERVER_NAME = "bale"

val LOG: Logger = LoggerFactory.getLogger(SERVER_NAME)

@Suppress("unused")
fun init() {
    LOG.info("Initializing $SERVER_NAME")
}
