package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.common.getCustomDirPermission
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun ChooseCustomDirDownloads(
    currentFolderUriStr: String?,
    onFolderSelected: (Uri) -> Unit,
    onClearCustomDirDownloads: () -> Unit
) {
    val folderLauncher = LocalContext.current.getCustomDirPermission(onFolderSelected)
    ChooseCustomDirDownloadsContent(
        currentFolderUriStr = currentFolderUriStr,
        onFolderSelectButtonClick = { folderLauncher.launch(null) },
        onClearCustomDirDownloads = onClearCustomDirDownloads
    )
}

@Composable
private fun ChooseCustomDirDownloadsContent(
    currentFolderUriStr: String?,
    onFolderSelectButtonClick: () -> Unit,
    onClearCustomDirDownloads: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentFolderUriStr?.isNotBlank() == true) {
            OutlinedButton(
                //modifier = Modifier.fillMaxWidth(0.5f),
                onClick = onClearCustomDirDownloads
            ) {
                Text("Reset")
            }
            Spacer(modifier = Modifier.height(4.dp).width(8.dp))
        }

        TextWithSubtitle(
            modifier = Modifier.weight(1f),
            title = "Choose custom folder for offline music",
            subtitle = "Current selected folder: ${currentFolderUriStr(currentFolderUriStr)}",
            trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew,
            onClick = onFolderSelectButtonClick
        )
    }
}

private fun currentFolderUriStr(currentFolderUriStr: String?): String {
    val noSelectionText = "No folder selected, using default storage location"
    return currentFolderUriStr
        ?.ifBlank { noSelectionText }
        ?.replace("content://", "")
        ?.replace("com.android.externalstorage.documents", "")
        ?.replace("/tree/", "")
        //?.replace("primary", "")
        ?.replace("%3A", "/")
        ?.replace("%2F", "/")
        ?: noSelectionText
}

@Preview
@Composable
fun ChooseCustomDirDownloadsPreview() {
    ChooseCustomDirDownloadsContent(
        currentFolderUriStr = "content://com.android.externalstorage.documents/tree/Music",
        onFolderSelectButtonClick = {},
        onClearCustomDirDownloads = {}
    )
}
