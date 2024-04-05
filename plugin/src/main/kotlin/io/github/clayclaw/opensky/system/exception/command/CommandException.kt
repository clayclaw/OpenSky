package io.github.clayclaw.opensky.system.exception.command

abstract class CommandException(
    override val message: String,
    override val cause: Throwable? = null,
): Exception(message, cause)