package luci.sixsixsix.powerampache2.domain.errors.models

import java.time.LocalDateTime

data class LogMessageState(
    var logMessage: String? = null,
    val date: LocalDateTime = LocalDateTime.now(),
    val count: Int? = null
)
