package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import luci.sixsixsix.powerampache2.domain.SleepTimerEventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimerEventBusImpl @Inject constructor() : SleepTimerEventBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val sleepTimerEvents: Flow<Unit> = _events
    override fun emitSleepTimerExpired() { _events.tryEmit(Unit) }
}
