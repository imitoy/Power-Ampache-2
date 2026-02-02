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
package luci.sixsixsix.powerampache2

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import luci.sixsixsix.powerampache2.di.ErrorHandlerModule
import luci.sixsixsix.powerampache2.domain.common.Constants
import luci.sixsixsix.powerampache2.domain.utils.ConfigProvider
import javax.inject.Inject

@HiltAndroidApp
class PowerAmpache2Application : Application(), ImageLoaderFactory, Configuration.Provider {

    @Inject
    lateinit var workerFactoryConfiguration: Configuration

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var configProvider: ConfigProvider

    private val crashReportHelper: CrashReportHelper = ErrorHandlerModule.provideCrashReportHelper()

    override fun onCreate() {
        super.onCreate()
        // initialize the default values for the config, new values will be fetched by a network call
        Constants.config = configProvider.defaultPa2Config()

//        GlobalScope.launch {
//            delay(9000)
//            throw NullPointerException("Test Crash") // Force a crash
//        }
    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)
        crashReportHelper.initialize(this)
    }

    override fun newImageLoader(): ImageLoader = imageLoader

    override val workManagerConfiguration: Configuration
        get() = workerFactoryConfiguration
}
