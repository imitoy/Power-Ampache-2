package luci.sixsixsix.powerampache2

import android.app.Application
import luci.sixsixsix.powerampache2.errorhandling.initCrashReportAcra

class EmailErrorReportHelper: CrashReportHelper {
    override fun initialize(application: Application) {
        application.initCrashReportAcra(
            BuildConfig.ERROR_REPORT_EMAIL,
            R.string.crash_mail_subject,
            R.string.crash_mail_body
        )
    }
}
