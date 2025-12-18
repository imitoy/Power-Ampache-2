package luci.sixsixsix.powerampache2.errorlogger.models

data class ErrorLog(
    val error_log: String,
    val android_version: String,
    val app_version: String,
    val backend_type: String,
    val backend_version: String,
    val device_manufacturer: String,
    val device_model: String,
    val android_api_version: Int,
)
