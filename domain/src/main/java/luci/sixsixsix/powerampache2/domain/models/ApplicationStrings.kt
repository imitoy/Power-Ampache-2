package luci.sixsixsix.powerampache2.domain.models

data class ApplicationStrings(
    val androidVersion: String,
    val androidApiLevel: Int,
    val deviceModel: String,
    val deviceManufacturer: String,
    val appVersionString: String,
    val backendType: String,
    val backendVersion: String
)
