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
package luci.sixsixsix.powerampache2.errorhandling

import android.app.Application
import androidx.annotation.StringRes
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

/**
 * Use this module only for flavours that use ACRA email crash report.
 */
fun Application.initCrashReportAcra(
    reportEmail: String,
    @StringRes reportSubject: Int,
    @StringRes reportBody: Int
) {
    initAcra {
        //core configuration:
        //buildConfigClass = BuildConfig::class.java

        reportFormat = StringFormat.JSON
        //each plugin you chose above can be configured in a block like this:
        mailSender {
            //required
            mailTo = reportEmail
            //defaults to true
            reportAsFile = true
            //defaults to ACRA-report.stacktrace
            reportFileName = "Crash.txt"
            //defaults to "<applicationId> Crash Report"
            subject = getString(reportSubject)
            //defaults to empty
            body = getString(reportBody)
        }
    }
}
