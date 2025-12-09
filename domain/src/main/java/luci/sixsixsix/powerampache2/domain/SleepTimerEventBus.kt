package luci.sixsixsix.powerampache2.domain

import kotlinx.coroutines.flow.Flow

interface SleepTimerEventBus {
    val sleepTimerEvents: Flow<Unit>
    fun emitSleepTimerExpired()
}
