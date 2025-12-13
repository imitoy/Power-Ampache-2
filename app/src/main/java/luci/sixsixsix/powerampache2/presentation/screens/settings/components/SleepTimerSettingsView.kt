package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import luci.sixsixsix.powerampache2.R

@Composable
fun SleepTimerSettingsView(
    sliderValue: Int,
    endTimeStr: String?,
    onReset: () -> Unit,
    onValueChange: (newValue: Int) -> Unit
) {
    val sliderTitle = endTimeStr?.let {
        stringResource(R.string.sleepTimer_time_set,it)
    } ?: stringResource(R.string.sleepTimer_time_unset)

    Row(
        modifier = Modifier.padding(start = 16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        SettingsSlider(
            modifier = Modifier.weight(0.7f),
            title = stringResource(R.string.sleepTimer_title),
            sliderTitle = sliderTitle,
            max = 120,
            sliderValue = sliderValue,
            onValueChange = onValueChange
        )
        IconButton(
            onClick = onReset) {
            Icon(
                Icons.Default.Stop,
                contentDescription = stringResource(R.string.settings_player_button_resetDefaults)
            )
        }
    }
}

@Composable
@Preview
fun SleepTimerSettingsViewPreview() {
    SleepTimerSettingsView(
        sliderValue = 11,
        endTimeStr = "Will be triggered at 11:00",
        onValueChange = { },
        onReset = { }
    )
}
