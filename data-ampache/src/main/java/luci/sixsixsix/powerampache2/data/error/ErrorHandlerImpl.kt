/**
 * Copyright (C) 2025  Antonio Tari
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
package luci.sixsixsix.powerampache2.data.error

import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import luci.sixsixsix.mrlog.L
import luci.sixsixsix.mrlog.MrLog
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.datasource.DbDataSource
import luci.sixsixsix.powerampache2.domain.errors.AmpachePreferenceException
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.ErrorType
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.errors.ScrobbleException
import luci.sixsixsix.powerampache2.domain.errors.ServerUrlNotInitializedException
import luci.sixsixsix.powerampache2.domain.errors.UserNotEnabledException
import luci.sixsixsix.powerampache2.domain.errors.models.ErrorLogMessageState
import luci.sixsixsix.powerampache2.domain.errors.models.ErrorStrings
import luci.sixsixsix.powerampache2.domain.errors.models.LogMessageState
import luci.sixsixsix.powerampache2.domain.models.ApplicationStrings
import luci.sixsixsix.powerampache2.domain.models.ServerInfo
import luci.sixsixsix.powerampache2.domain.models.nextcloudMusicVersion
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import luci.sixsixsix.powerampache2.domain.utils.DataStringsProvider
import luci.sixsixsix.powerampache2.errorlogger.models.ErrorLog
import luci.sixsixsix.powerampache2.errorlogger.remote.ErrorHandlerApi
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandlerImpl @Inject constructor(
    private val db: DbDataSource,
    private val errorHandlerApi: ErrorHandlerApi,
    configProvider: ConfigProvider,
    dataStringsProvider: DataStringsProvider,
    applicationCoroutineScope: CoroutineScope
): ErrorHandler {

    private val errorStrings: ErrorStrings = dataStringsProvider.errorStrings
    private val applicationStrings: ApplicationStrings = dataStringsProvider.applicationStrings

    private val _logMessageUserReadableState = MutableStateFlow(LogMessageState())
    override val logMessageUserReadableState: StateFlow<LogMessageState> = _logMessageUserReadableState
    override var notificationsListStateFlow: MutableStateFlow<List<LogMessageState>> =
        MutableStateFlow(listOf())

    private val _errorLogMessageState = MutableStateFlow(ErrorLogMessageState())
    override val errorLogMessageState: StateFlow<ErrorLogMessageState> = _errorLogMessageState

    override var serverInfo: ServerInfo = ServerInfo()

    val isErrorHandlingEnabled: StateFlow<Boolean> =
        db.settingsFlow
            .map { it.enableRemoteLogging }
            .distinctUntilChanged()
            .stateIn(
                scope = applicationCoroutineScope,
                started = SharingStarted.Companion.Eagerly,
                initialValue = configProvider.ENABLE_ERROR_LOG
            )

    override fun updateUserMessage(logMessage: String?) {
        MrLog("MusicPlaylistManager updateUserMessage", logMessage)
        _logMessageUserReadableState.value = LogMessageState(logMessage = logMessage)

        // add to the list of notifications
        logMessage?.let { lm ->
            if (lm.isNotBlank()) {
                // if already there remove it
                val messages = ArrayList<LogMessageState>(notificationsListStateFlow.value)
                // remove if already present
                var count = 0
                messages.map { it.logMessage }.indexOf(lm).apply {
                    if (this > -1) {
                        count = messages[this].count ?: 0
                        messages.removeAt(this)
                    }
                }

                notificationsListStateFlow.value = messages.apply {
                    add(0, LogMessageState(logMessage = lm, count = ++count))
                }
            }
        }

        // also log for debug reasons
        updateErrorLogMessage(logMessage)
    }

    /**
     * updates the error log in settings
     */
    override fun updateErrorLogMessage(logMessage: String?) {
        MrLog("MusicPlaylistManager updateErrorLogMessage", logMessage)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        _errorLogMessageState.value = ErrorLogMessageState(errorMessage = "$current\n$logMessage")
    }

    @OptIn(UnstableApi::class)
    override suspend fun <T> invoke(
        label: String,
        e: Throwable,
        fc: FlowCollector<Resource<T>>?,
        onError: (message: String, e: Throwable) -> Unit
    ) {
        // Blocking errors for server url not initialized
        // this is an exception generated by the interceptor for when a url has not yet been
        // initialized by the user
        if (e is MusicException && e.musicError.isServerUrlNotInitialized()) {
            MrLog("ServerUrlNotInitializedException")
            fc?.emit(Resource.Loading(false))
            return
        }

        val exceptionString = e.printStackTrace()

        var readableMessage: String? = null
        StringBuilder(label)
            .append(if (label.isBlank()) "" else " - ")
            .append(
                when (e) {
                    is UserNotEnabledException -> {
                        readableMessage = errorStrings.errorUserNotEnabled
                        "PlaybackException \n $exceptionString"
                    }
                    is HttpDataSource.InvalidResponseCodeException -> {
                        readableMessage = "${errorStrings.errorCannotConnect}\nResponse code: ${e.responseCode}"
                        "HttpDataSource.InvalidResponseCodeException \n$label\n $exceptionString"
                    }

                    is HttpDataSource.HttpDataSourceException -> {
                        readableMessage = errorStrings.errorCannotConnect
                        "HttpDataSource.HttpDataSourceException \n$readableMessage\n $exceptionString"
                    }

                    is PlaybackException -> {
                        readableMessage =
                            if (e.errorCode != PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED)
                                "Error Code: ${e.errorCode}. ${errorStrings.errorPlaybackException}\n$exceptionString"
                            else ""

                        "PlaybackException \n$readableMessage\n $exceptionString"
                    }

                    is IOException -> {
                        readableMessage = errorStrings.errorIoException
                        "cannot load data IOException $exceptionString"
                    }

                    is HttpException -> {
                        readableMessage = e.localizedMessage
                        "cannot load data HttpException $exceptionString"
                    }

                    is AmpachePreferenceException -> {
                        readableMessage = errorStrings.errorCannotEditPreference
                        "$readableMessage $exceptionString"
                    }

                    is ServerUrlNotInitializedException ->
                        "ServerUrlNotInitializedException $exceptionString"

                    is ScrobbleException -> {
                        readableMessage = ""
                        ""
                    }
                    is MusicException -> {
                        when (e.musicError.getErrorType()) {
                            ErrorType.ACCOUNT -> {
                                // clear session and try to autologin using the saved credentials
                                db.clearSession()
                                readableMessage = e.musicError.errorMessage
                            }

                            ErrorType.EMPTY ->
                                readableMessage = errorStrings.errorEmptyResult

                            ErrorType.DUPLICATE ->
                                readableMessage = errorStrings.errorDuplicate

                            ErrorType.Other ->
                                readableMessage = e.musicError.errorMessage

                            ErrorType.SYSTEM ->
                                readableMessage = e.musicError.errorMessage
                        }
                        e.musicError.toString()
                    }

                    else -> {
                        readableMessage = e.localizedMessage
                        "generic exception $exceptionString"
                    }
                }
            ).toString().apply {
                // check on error on the emitted data for detailed logging
                fc?.emit(Resource.Error(message = this, exception = e))
                // log and report error here
                logError(e, this)
                updateErrorLogMessage(this)
                // readable message here
                readableMessage?.let {
                    // TODO find a better way to not show verbose info
                    //  ie. session expired for timestamp
                    if (e is HttpException || e is IOException) {
                        updateUserMessage(errorStrings.errorOffline)
                    } else if (!readableMessage.lowercase().contains("timestamp") &&
                        !readableMessage.lowercase().contains("expired") &&
                        !readableMessage.lowercase().contains("session")) {
                        updateUserMessage(readableMessage)
                    } else if (e is UserNotEnabledException) {
                        updateUserMessage(readableMessage)
                    }
                }
                onError(this, e)
                L.e(readableMessage, e)
            }
    }

    override suspend fun logError(e: Throwable, message: String) =
        logError(message = "${e.stackTraceToString()}\n$message")


    /**
     * Updates the error log message, and if enabled send a error report over http or file.
     */
    override suspend fun logError(message: String) {
        try {
            updateErrorLogMessage(message)

            if (
                isErrorHandlingEnabled.value
                && message.isNotBlank()
                // do not log NullSessionException, too frequent
                && !message.lowercase().contains("NullSessionException".lowercase())
                ) {
                    val backendVersion = if (serverInfo.isNextcloud != true)
                        serverInfo.server else serverInfo.nextcloudMusicVersion()

                    errorHandlerApi.postErrorLog(
                        ErrorLog(
                            error_log = message,
                            android_version = applicationStrings.androidVersion,
                            app_version = applicationStrings.appVersionString,
                            backend_type = if (serverInfo.isNextcloud != true) "Ampache" else "Nextcloud",
                            backend_version = backendVersion ?: "null",
                            device_model = applicationStrings.deviceModel,
                            android_api_version = applicationStrings.androidApiLevel,
                            device_manufacturer = applicationStrings.deviceManufacturer,
                            ampache_api_version = serverInfo.version ?: "null"
                        )
                    )
            }
        } catch (e: Exception) {
            if (e is HttpException)
                L.e(e.stackTraceToString(), e.message(), e.localizedMessage, e.code(), e.response())
            else
                L.e(e.stackTraceToString(), e.localizedMessage)
        }
    }

    override fun resetMessages()  {
        updateUserMessage(logMessage = null)
        updateErrorLogMessage(logMessage = null)
    }

}