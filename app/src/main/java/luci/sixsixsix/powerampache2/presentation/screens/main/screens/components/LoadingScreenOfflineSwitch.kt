package luci.sixsixsix.powerampache2.presentation.screens.main.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.R

@Composable
fun LoadingScreenOfflineSwitch(
    modifier: Modifier,
    offlineModeEnabled: Boolean,
    onSwitchToggle: (newValue: Boolean) -> Unit
) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(id = R.string.offlineMode_switch_title),
            fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Switch(
            checked = offlineModeEnabled,
            onCheckedChange = onSwitchToggle,
            enabled = true
        )
    }
}