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
package luci.sixsixsix.powerampache2.domain.errors

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.domain.errors.models.ErrorLogMessageState
import luci.sixsixsix.powerampache2.domain.errors.models.LogMessageState
import luci.sixsixsix.powerampache2.domain.models.ServerInfo

interface ErrorHandler {
    val logMessageUserReadableState: StateFlow<LogMessageState>

    /**
     * notifications that will appear in the notification view/screen
     */
    var notificationsListStateFlow: MutableStateFlow<List<LogMessageState>>

    val errorLogMessageState: StateFlow<ErrorLogMessageState>

    /**
     * server info for error reporting
     */
    var serverInfo: ServerInfo

    /**
     * User facing message, this will appear visibly to the user (ie. toast or snackbar)
     */
    fun updateUserMessage(logMessage: String?)

    /**
     * Those errors will be collected in the error log, like a debug view.
     */
    fun updateErrorLogMessage(logMessage: String?)

    suspend operator fun <T> invoke(
        label:String = "",
        e: Throwable,
        fc: FlowCollector<Resource<T>>? = null,
        onError: (message: String, e: Throwable) -> Unit = { _, _ -> { } }
    )

    suspend fun logError(e: Throwable, message: String = "")
    suspend fun logError(message: String)
    fun resetMessages()
}
