package io.github.clayclaw.opensky.extension

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

fun currentUTC(): DateTime = DateTime.now(DateTimeZone.UTC)