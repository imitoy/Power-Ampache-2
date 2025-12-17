package luci.sixsixsix.powerampache2

import android.app.Application
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class EmailErrorReportHelper: CrashReportHelper {
    override fun initialize(application: Application) {
        application.initAcra {
            //core configuration:
            //buildConfigClass = BuildConfig::class.java

            reportFormat = StringFormat.JSON
            //each plugin you chose above can be configured in a block like this:
            mailSender {
                //required
                mailTo = BuildConfig.ERROR_REPORT_EMAIL
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = application.getString(R.string.crash_mail_subject)
                //defaults to empty
                body = application.getString(R.string.crash_mail_body)
            }
        }
    }
}
