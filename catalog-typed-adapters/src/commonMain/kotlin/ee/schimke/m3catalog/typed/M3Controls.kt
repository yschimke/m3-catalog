package ee.schimke.m3catalog.typed

import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// These are ordinary public export wrappers, outside the kit's sticker inventory. Nullable
// Material callbacks become non-null here so discovery can validate their actual payloads.
object ControlDefaults {
  const val checked = false
  const val enabled = true
}

data class ToggleProperties(
  val checked: Boolean,
  val enabled: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

data class RadioProperties(val selected: Boolean, val enabled: Boolean, val onClick: () -> Unit)

@Composable
fun M3Checkbox(
  checked: Boolean = ControlDefaults.checked,
  enabled: Boolean = ControlDefaults.enabled,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) =
  Checkbox(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
  )

@Composable
fun M3Switch(
  checked: Boolean = ControlDefaults.checked,
  enabled: Boolean = ControlDefaults.enabled,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) =
  Switch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
  )

@Composable
fun M3RadioButton(
  selected: Boolean = ControlDefaults.checked,
  enabled: Boolean = ControlDefaults.enabled,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) = RadioButton(selected = selected, onClick = onClick, modifier = modifier, enabled = enabled)
