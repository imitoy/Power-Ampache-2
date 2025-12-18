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
package luci.sixsixsix.powerampache2.common

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.domain.errors.models.ErrorStrings
import luci.sixsixsix.powerampache2.domain.models.ApplicationStrings
import luci.sixsixsix.powerampache2.domain.utils.DataStringsProvider
import javax.inject.Inject

class DataStringsProviderImpl @Inject constructor(@ApplicationContext private val context: Context): DataStringsProvider {
    override val errorStrings: ErrorStrings
        get() = context.run {
            ErrorStrings(
                errorUserNotEnabled = getString(R.string.error_user_notEnabled),
                errorCannotConnect = getString(R.string.error_cannotConnect),
                errorOffline = getString(R.string.error_offline),
                errorPlaybackException = getString(R.string.error_playback_exception),
                errorIoException = getString(R.string.error_io_exception),
                errorCannotEditPreference = getString(R.string.error_cannotEditPreference),
                errorDuplicate = getString(R.string.error_duplicate),
                errorEmptyResult = getString(R.string.error_empty_result)
            )

        }

    override val applicationStrings: ApplicationStrings
        // some non-android devices might crash getting device info strings.
        get() = ApplicationStrings(
            androidVersion = try { Build.VERSION.RELEASE } catch (e: Exception) { "${e.localizedMessage}" },
            androidApiLevel = try { Build.VERSION.SDK_INT } catch (_: Exception) { 0 },
            deviceModel = try { Build.MODEL } catch (e: Exception) { "${e.localizedMessage}" },
            deviceManufacturer = try { Build.MANUFACTURER } catch (e: Exception) { "${e.localizedMessage}" },
            appVersionString = getVersionInfoString(context),
            backendType = "todo",
            backendVersion = "todo"
        )
}