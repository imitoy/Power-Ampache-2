package luci.sixsixsix.powerampache2.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util.startForegroundService
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.powerampache2.common.Constants.SERVICE_STOP_TIMEOUT
import luci.sixsixsix.powerampache2.domain.SleepTimerEventBus
import luci.sixsixsix.powerampache2.domain.common.WeakContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicController @Inject constructor(
    @ApplicationContext private val context: Context,
    val weakContext: WeakContext,
    private val sleepTimerEventBus: SleepTimerEventBus,
    private val applicationCoroutineScope: CoroutineScope,
    val playlistManager: MusicPlaylistManager
) {
    private val serviceIntent = Intent(context, SimpleMediaService::class.java)
    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var startMusicServiceCalled = false // ensure called only once
    private var stopJob: Job? = null

    init {
        if (SimpleMediaService.isRunning) {
            // initialize the controllers if returning from killed state and service running
            initController(context)
        }

        // Listen to sleep timer events
        applicationCoroutineScope.launch {
            sleepTimerEventBus.sleepTimerEvents.collect {
                withContext(Dispatchers.Main) {
                    resetStopMusic()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun startMusicServiceIfNecessary() {
        if(!SimpleMediaService.isRunning && !startMusicServiceCalled) {
            println("SERVICE- startMusicServiceIfNecessary")
            weakContext.get()?.applicationContext?.let { applicationContext ->
                startForegroundService(applicationContext, serviceIntent)
                initController(applicationContext)
                startMusicServiceCalled = true
            }
        }
    }

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, SimpleMediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                try {
                    controller = controllerFuture?.get()
                } catch (e: Exception) {
                    L.e(e, "Failed to get MediaController")
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    /**
     * IMPORTANT: run this on Main Thread
     */
    fun releaseController() {
        controller?.release()
        controller = null
        controllerFuture?.cancel(true)
        controllerFuture = null
    }

    @OptIn(UnstableApi::class)
    fun stopService() {
        L("SERVICE- stopMusicService isRunning: ${SimpleMediaService.isRunning}")

        if (!SimpleMediaService.isRunning) return
        releaseController()

        weakContext.get()?.applicationContext?.let { applicationContext ->
            try {
                L("SERVICE- stopMusicService")
                applicationContext.stopService(serviceIntent)
                startMusicServiceCalled = false
            } catch (e: Exception) {
                startMusicServiceCalled = false
                L.e(e, "SERVICE-")
            }
        }
    }
    fun stopMusicService(addDelay: Boolean = true) {
        stopJob?.cancel()
        if (addDelay) {
            stopJob = applicationCoroutineScope.launch {
                delay(SERVICE_STOP_TIMEOUT) // safety net, delay stopping the service in case the application just got restored from background
                withContext(Dispatchers.Main) {
                    stopService()
                }
            }
        } else {
            runOnMain { stopService() }
        }
    }

    fun resetStopMusic() {
        try {
            playlistManager.reset()
            stopMusicService()
        } catch (e: Exception) {
            L.e(e)
        }
    }

    private fun runOnMain(block: () -> Unit) {
        Handler(Looper.getMainLooper()).post { block() }
    }
}
