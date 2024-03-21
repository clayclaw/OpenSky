package io.github.clayclaw.opensky.extension

import java.util.logging.Logger

const val DEBUG = true

fun Logger.debug(msg: String) {
    if(DEBUG) {
        this.info("[DEBUG] $msg")
    }
}