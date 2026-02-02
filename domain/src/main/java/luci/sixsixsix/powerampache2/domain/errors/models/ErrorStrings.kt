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
package luci.sixsixsix.powerampache2.domain.errors.models

data class ErrorStrings(
    val errorUserNotEnabled: String = "User not enabled, please check your email for the account confirmation link or enable the user from the server",
    val errorCannotConnect: String = "Problem connecting to the server or data source. \nPlay a different track or check your connection",
    val errorPlaybackException: String = "Issues playing this track.",
    val errorIoException: String = "Cannot load data, are you connected to the internet?\nIf the problem persist try to kill and reopen the app",
    val errorCannotEditPreference: String = "Cannot Edit Preference",
    val errorOffline: String = "Cannot connect to your server",
    val errorDuplicate: String = "Duplicate Entry",
    val errorEmptyResult: String = "No results to display, or you are trying to add or remove something not found on the server"
)
