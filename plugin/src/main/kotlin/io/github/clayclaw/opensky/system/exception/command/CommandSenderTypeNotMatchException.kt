package io.github.clayclaw.opensky.system.exception.command

import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

class CommandSenderTypeNotMatchException(
    val requiredType: KClass<out CommandSender>,
): CommandException("Command sender can only be ${requiredType.simpleName}")