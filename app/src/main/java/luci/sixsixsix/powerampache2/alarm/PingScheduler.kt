/**
 * Copyright (C) 2024  Antonio Tari
 *
 * This file is a part of Power Ampache 2
 * Ampache Android client application
 * @author Antonio Tari
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package luci.sixsixsix.powerampache2.alarm

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_HOUR
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerEndTimestampFlow
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerResetUseCase
import luci.sixsixsix.powerampache2.domain.usecase.settings.SleepTimerSetEndTimestampUseCase
import luci.sixsixsix.powerampache2.domain.utils.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

const val REQUEST_CODE_PING = 665012
const val ACTION_PING = "luci.sixsixsix.powerampache2.alarm.action.PING"
const val REQUEST_CODE_SLEEP_TIMER = 6665419
const val ACTION_SLEEP_TIMER = "luci.sixsixsix.powerampache2.alarm.action.SLEEP_TIMER"

@Singleton
class PingScheduler @Inject constructor(
    @ApplicationContext context: Context,
    private val sleepTimerEndTimestampFlow: SleepTimerEndTimestampFlow,
    private val sleepTimerSetEndTimestampUseCase: SleepTimerSetEndTimestampUseCase,
    private val sleepTimerResetUseCase: SleepTimerResetUseCase,
    private val applicationCoroutineScope: CoroutineScope
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    // avoids to many timer set consecutively when sliding the slider
    private var sleepTimerSetJob: Job? = null

    init {
        sleepTimerResetUseCase()

        applicationCoroutineScope.launch {
            // if the value of the sleep timer is reset to zero, kill the alarm
            sleepTimerEndTimestampFlow()
                .filterNotNull()
                .filter { it == 0L }
                .collect {
                    try {
                        if (it == 0L && sleepTimerPendingIntent != null) {
                            println("aaaa cancelling the timer")
                            alarmManager.cancel(sleepTimerPendingIntent)
                        }
                    } catch (e: Exception) {
                        L.e(e)
                    }
                }
        }
    }

    private val pingPendingIntent = PendingIntent.getBroadcast(context,
        REQUEST_CODE_PING,
        Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_PING
            putExtra("MESSAGE", "PING")
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    private var isPingScheduled = false

    private val sleepTimerPendingIntent = PendingIntent.getBroadcast(context,
        REQUEST_CODE_SLEEP_TIMER,
        Intent(context, AlarmReceiver::class.java).apply { action = ACTION_SLEEP_TIMER },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    override fun schedule() {
        L("schedule alarm ping")
        if (isPingScheduled) return

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + INTERVAL_HOUR,
            INTERVAL_HOUR,
            pingPendingIntent
        )

        isPingScheduled = true
    }

    override fun cancel() = alarmManager.cancel(pingPendingIntent).also { isPingScheduled = false }

    override fun scheduleSleepTimer(minutesInterval: Int) {
        sleepTimerSetJob?.cancel()
        sleepTimerSetJob = applicationCoroutineScope.launch {
            delay(2000)
            alarmManager.cancel(sleepTimerPendingIntent)
            L("aaaa Sleep timer set")

            val millis = (minutesInterval * 60L) * 1000L
            val now = System.currentTimeMillis()
            val triggerAt = now + millis
            if (triggerAt <= now || minutesInterval <= 0) return@launch

            sleepTimerSetEndTimestampUseCase(triggerAt)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    triggerAt - 60000, // there are usually 2 mins delay
                    sleepTimerPendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    sleepTimerPendingIntent
                )
            }
        }
    }

    override fun cancelSleepTimer() {
        sleepTimerResetUseCase()
    }
}
