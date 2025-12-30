package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.common.PowerAmpSwitch

@Composable
fun SleepTimerSettingsView(
    sliderValue: Int,
    endTimeStr: String?,
    sleepTimerWaitSongEnd: Boolean,
    onReset: () -> Unit,
    onValueChange: (newValue: Int) -> Unit,
    onSleepTimerWaitForSongEndChange: (newValue: Boolean) -> Unit
) {
    val sliderTitle = endTimeStr?.let {
        remainingTimeMessage(sliderValue) + stringResource(R.string.sleepTimer_time_set,it)
    } ?: stringResource(R.string.sleepTimer_time_unset)

    Column(modifier = Modifier.fillMaxWidth()) {
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
                onClick = onReset
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = stringResource(R.string.settings_player_button_resetDefaults)
                )
            }
        }

        PowerAmpSwitch(
            enabled = true,
            title = R.string.sleepTimer_waitEnd,
            checked = sleepTimerWaitSongEnd,
            onCheckedChange = onSleepTimerWaitForSongEndChange,
            modifier = Modifier.padding(start = 18.dp, end = 8.dp)
        )
    }
}

@Composable
fun remainingTimeMessage(remainingTime: Int): String {
    var message by remember { mutableStateOf("") }
    var hasInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(remainingTime) {
        if (!hasInitialized) {
            hasInitialized = true
            return@LaunchedEffect
        }

        if (remainingTime > 0) {
            message = "(${remainingTime}m) "
            delay(30_000)
            message = ""
        }
    }

    return message
}

@Composable
@Preview
fun SleepTimerSettingsViewPreview() {
    SleepTimerSettingsView(
        sliderValue = 11,
        endTimeStr = "Will be triggered at 11:00",
        sleepTimerWaitSongEnd = true,
        onValueChange = { },
        onReset = { },
        onSleepTimerWaitForSongEndChange = { }
    )
}
