package luci.sixsixsix.powerampache2.presentation.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import luci.sixsixsix.powerampache2.presentation.common.TextWithSubtitle

@Composable
fun SettingsSlider(
    modifier: Modifier = Modifier,
    title: String,
    sliderTitle: String,
    subtitle: String? = null,
    min: Int = 0,
    max: Int = 666,
    sliderValue: Int,
    onValueChange: (newValue: Int) -> Unit
) {

    Column(modifier = modifier) {
        TextWithSubtitle(
            title = title,
            subtitle = subtitle,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = sliderTitle,
            //fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
        )
        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { newValue ->
                onValueChange(newValue.toInt())
            },
            valueRange = min.toFloat()..max.toFloat(),
            //steps = 1, // Number of steps for discrete values (optional)
        )
    }
}
