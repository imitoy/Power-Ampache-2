package luci.sixsixsix.powerampache2.domain.utils

import luci.sixsixsix.powerampache2.domain.errors.models.ErrorStrings
import luci.sixsixsix.powerampache2.domain.models.ApplicationStrings

interface DataStringsProvider {
    val errorStrings: ErrorStrings
    val applicationStrings: ApplicationStrings

}
