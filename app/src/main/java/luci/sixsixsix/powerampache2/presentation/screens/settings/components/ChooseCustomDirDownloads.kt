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
package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.getCustomDirPermission
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun ChooseCustomDirDownloads(
    currentFolderUriStr: String?,
    onFolderSelected: (Uri) -> Unit,
    onClearCustomDirDownloads: () -> Unit
) {
    val folderLauncher = LocalContext.current.getCustomDirPermission(onFolderSelected)
    Column(modifier = Modifier.fillMaxWidth()) {
        ChooseCustomDirDownloadsContent(
            currentFolderUriStr = currentFolderUriStr,
            onFolderSelectButtonClick = { folderLauncher.launch(null) },
            onClearCustomDirDownloads = onClearCustomDirDownloads
        )
        if (currentFolderUriStr?.isNotBlank() == true) {
            ChooseCustomDirWarningMessage()
        }
    }
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
            ) { Text(stringResource(R.string.settings_customFolder_reset)) }
            Spacer(modifier = Modifier.height(4.dp).width(8.dp))
        }

        TextWithSubtitle(
            modifier = Modifier.weight(1f),
            // "Choose custom folder for offline music"
            title = stringResource(R.string.settings_customFolder_title),
            //"Current selected folder: ${currentFolderUriStr(currentFolderUriStr)}",
            subtitle = stringResource(R.string.settings_customFolder_subtitle, currentFolderUriStr(currentFolderUriStr)),
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

@Composable
private fun ChooseCustomDirWarningMessage() {
    var isExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        OutlinedButton(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                text = stringResource(R.string.settings_customFolder_warning_title),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        if (isExpanded) {
            ChooseCustomDirWarningDialog {
                isExpanded = false
            }
        }
    }
}

@Composable
private fun ChooseCustomDirWarningDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_customFolder_warning_descr),
                //fontSize = 12.sp,
                //lineHeight = 12.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

@Preview
@Composable
fun ChooseCustomDirDownloadsPreview() {
    ChooseCustomDirDownloads(
        currentFolderUriStr = "content://com.android.externalstorage.documents/tree/Music",
        onFolderSelected = {},
        onClearCustomDirDownloads = {}
    )
}
