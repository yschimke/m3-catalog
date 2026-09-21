@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.uibuilder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ee.schimke.composeai.uibuilder.CanvasAdapterRegistry
import ee.schimke.composeai.uibuilder.CanvasNodeScope
import ee.schimke.composeai.uibuilder.canvasAdapterRegistry
import ee.schimke.composeai.uibuilder.googleMaterialIconImageVector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop

val materialCanvasAdapters = canvasAdapterRegistry {
  register("material3/Button") {
    when (string("style")) {
      "filledTonal" ->
        FilledTonalButton(::click, modifier, enabled = boolean("enabled", true)) { Slot("content") }
      "text" ->
        TextButton(::click, modifier, enabled = boolean("enabled", true)) { Slot("content") }
      "fab" -> FloatingActionButton(::click, modifier) { Slot("content") }
      else -> Button(::click, modifier, enabled = boolean("enabled", true)) { Slot("content") }
    }
  }
  card("material3/Card", CardKind.Filled)
  card("material3/ElevatedCard", CardKind.Elevated)
  card("material3/OutlinedCard", CardKind.Outlined)
  register("material3/CenterAlignedTopAppBar") {
    CenterAlignedTopAppBar(
      title = { Slot("title") },
      modifier = modifier,
      navigationIcon = { Slot("navigationIcon") },
      actions = { Slot("actions") },
      colors =
        TopAppBarDefaults.topAppBarColors(
          containerColor = color("containerColor", Color.Transparent),
          scrolledContainerColor = color("scrolledContainerColor", Color.Transparent),
        ),
    )
  }
  register("material3/Checkbox") {
    Checkbox(
      checked = boolean("checked"),
      onCheckedChange = { dispatch("click") },
      modifier = modifier,
      enabled = boolean("enabled", true),
    )
  }
  register("material3/DatePicker") {
    val initial = string("selectedDate").toEpochMillisOrNull()
    val mode = if (string("mode") == "input") DisplayMode.Input else DisplayMode.Picker
    key(initial, mode) {
      val state =
        rememberDatePickerState(
          initialSelectedDateMillis = initial,
          initialDisplayedMonthMillis = initial,
          initialDisplayMode = mode,
        )
      DatePicker(state, modifier, showModeToggle = boolean("showModeToggle", true))
    }
  }
  register("material3/AlertDialog") {
    AlertDialog(
      onDismissRequest = {},
      confirmButton = { Slot("confirmButton") },
      modifier = modifier,
      dismissButton = optional("dismissButton"),
      icon = optional("icon"),
      title = optional("title"),
      text = optional("text"),
      containerColor = color("containerColor", MaterialTheme.colorScheme.surfaceContainerHigh),
      tonalElevation = float("tonalElevationDp", 6f).dp,
    )
  }
  register("material3/FilterChip") {
    FilterChip(
      selected = boolean("selected"),
      onClick = ::click,
      label = { Slot("label") },
      modifier = modifier,
      enabled = boolean("enabled", true),
      leadingIcon = optional("leadingIcon"),
      trailingIcon = optional("trailingIcon"),
    )
  }
  register("material3/HorizontalDivider") {
    HorizontalDivider(
      modifier,
      thickness = float("thicknessDp", 1f).dp,
      color = color("color", MaterialTheme.colorScheme.outlineVariant),
    )
  }
  register("material3/HorizontalFloatingToolbar") {
    HorizontalFloatingToolbar(
      expanded = boolean("expanded", true),
      modifier = modifier,
      colors =
        FloatingToolbarDefaults.standardFloatingToolbarColors(
          toolbarContainerColor =
            color("containerColor", MaterialTheme.colorScheme.primaryContainer)
        ),
    ) {
      Slot("content")
    }
  }
  icon("material3/Icon")
  iconButton("material3/IconButton", IconButtonKind.Standard)
  iconButton("material3/FilledIconButton", IconButtonKind.Filled)
  iconButton("material3/FilledTonalIconButton", IconButtonKind.Tonal)
  iconButton("material3/OutlinedIconButton", IconButtonKind.Outlined)
  register("material3/ListItem") {
    ListItem(
      headlineContent = { Slot("headline") },
      modifier = modifier,
      supportingContent = optional("supporting"),
      leadingContent = optional("leading"),
      trailingContent = optional("trailing"),
    )
  }
  register("material3/PrimaryTabRow") {
    PrimaryTabRow(integer("selectedIndex").coerceAtLeast(0), modifier) { Slot("tabs") }
  }
  register("material3/LinearProgressIndicator") {
    val progress = float("progress").coerceIn(0f, 1f)
    if (boolean("indeterminate")) LinearProgressIndicator(modifier = modifier)
    else LinearProgressIndicator(progress = { progress }, modifier = modifier)
  }
  register("material3/CircularProgressIndicator") {
    val progress = float("progress").coerceIn(0f, 1f)
    if (boolean("indeterminate")) CircularProgressIndicator(modifier = modifier)
    else CircularProgressIndicator(progress = { progress }, modifier = modifier)
  }
  register("material3/RadioButton") {
    RadioButton(
      selected = boolean("selected"),
      onClick = ::click,
      modifier = modifier,
      enabled = boolean("enabled", true),
    )
  }
  register("material3/SearchBar") {
    val expanded = boolean("expanded")
    SearchBar(
      inputField = { Slot("inputField", Modifier.fillMaxSize()) },
      expanded = expanded,
      onExpandedChange = {},
      modifier = modifier,
    ) {
      Slot("expandedContent")
    }
  }
  register("material3/InputField") {
    val value = string("value")
    key(value) {
      val state = remember { androidx.compose.foundation.text.input.TextFieldState(value) }
      val searchState = rememberSearchBarState(SearchBarValue.Collapsed)
      LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
          .drop(1)
          .collect { query ->
            updateBoundState("value", query)
            dispatch("queryChange")
          }
      }
      SearchBarDefaults.InputField(
        textFieldState = state,
        searchBarState = searchState,
        onSearch = { dispatch("search") },
        modifier = modifier,
        enabled = boolean("enabled", true),
        readOnly = boolean("readOnly"),
        placeholder = optional("placeholder"),
        leadingIcon = optional("leadingIcon"),
        trailingIcon = optional("trailingIcon"),
      )
    }
  }
  register("material3/Slider") {
    val from = float("valueFrom")
    val to = float("valueTo", 1f).coerceAtLeast(from)
    Slider(
      value = float("value").coerceIn(from, to),
      onValueChange = { updateBoundState("value", it.toString()) },
      modifier = modifier,
      enabled = boolean("enabled", true),
      valueRange = from..to,
      steps = integer("steps").coerceAtLeast(0),
    )
  }
  register("material3/Surface") {
    Surface(
      modifier,
      color = color("containerColor", Color.Transparent),
      contentColor = color("contentColor", MaterialTheme.colorScheme.onSurface),
      tonalElevation = float("tonalElevationDp").dp,
    ) {
      Slot("content")
    }
  }
  register("material3/Switch") {
    Switch(
      checked = boolean("checked"),
      onCheckedChange = { dispatch("click") },
      modifier = modifier,
      enabled = boolean("enabled", true),
      thumbContent = optional("thumbContent"),
    )
  }
  register("material3/Tab") {
    Tab(
      selected = boolean("selected"),
      onClick = ::click,
      modifier = modifier,
      enabled = boolean("enabled", true),
      text = optional("text"),
      icon = optional("icon"),
    )
  }
  text("material3/Text")
  textField("material3/TextField", outlined = false)
  textField("material3/OutlinedTextField", outlined = true)
  register("material3/TimePicker") {
    val hour = integer("hour").coerceIn(0, 23)
    val minute = integer("minute").coerceIn(0, 59)
    val is24Hour = boolean("is24Hour")
    key(hour, minute, is24Hour) {
      val state =
        rememberTimePickerState(
          initialHour = hour,
          initialMinute = minute,
          is24Hour = is24Hour,
        )
      if (string("mode") == "input") TimeInput(state = state, modifier = modifier)
      else
        TimePicker(state = state, modifier = modifier, layoutType = TimePickerLayoutType.Vertical)
    }
  }
}

private enum class CardKind {
  Filled,
  Elevated,
  Outlined,
}

private fun CanvasAdapterRegistry.Builder.card(id: String, kind: CardKind) {
  register(id) {
    when (kind) {
      CardKind.Filled ->
        Card(
          modifier = modifier,
          colors =
            CardDefaults.cardColors(
              containerColor = color("containerColor", MaterialTheme.colorScheme.surfaceContainer)
            ),
          elevation = CardDefaults.cardElevation(defaultElevation = float("elevationDp").dp),
        ) {
          Slot("content")
        }
      CardKind.Elevated -> ElevatedCard(modifier = modifier) { Slot("content") }
      CardKind.Outlined -> OutlinedCard(modifier = modifier) { Slot("content") }
    }
  }
}

private enum class IconButtonKind {
  Standard,
  Filled,
  Tonal,
  Outlined,
}

private fun CanvasAdapterRegistry.Builder.iconButton(id: String, kind: IconButtonKind) {
  register(id) {
    val size = modifier.size(float("sizeDp", 48f).dp)
    val content: @Composable () -> Unit = { Slot("content") }
    when (kind) {
      IconButtonKind.Standard ->
        IconButton(::click, size, boolean("enabled", true), content = content)
      IconButtonKind.Filled ->
        FilledIconButton(::click, size, boolean("enabled", true), content = content)
      IconButtonKind.Tonal ->
        FilledTonalIconButton(::click, size, boolean("enabled", true), content = content)
      IconButtonKind.Outlined ->
        OutlinedIconButton(::click, size, boolean("enabled", true), content = content)
    }
  }
}

private fun CanvasAdapterRegistry.Builder.icon(id: String) {
  register(id) {
    val vector = googleMaterialIconImageVector(string("iconKey"))
    if (vector == null) Text("?", modifier.size(float("sizeDp", 24f).dp))
    else
      Icon(
        vector,
        string("contentDescription").ifEmpty { null },
        modifier.size(float("sizeDp", 24f).dp),
        tint = color("color", MaterialTheme.colorScheme.onSurface),
      )
  }
}

private fun CanvasAdapterRegistry.Builder.text(id: String) {
  register(id) {
    Text(
      text = string("text"),
      modifier = modifier,
      color = color("color", Color.Unspecified),
      style = textStyle(),
      fontWeight = fontWeight(),
      fontStyle = fontStyle(),
      fontSize = float("fontSizeSp").takeIf { it > 0f }?.sp ?: TextUnit.Unspecified,
      lineHeight = float("lineHeightSp").takeIf { it > 0f }?.sp ?: TextUnit.Unspecified,
      letterSpacing =
        float("letterSpacingSp").takeIf { "letterSpacingSp" in node.properties }?.sp
          ?: TextUnit.Unspecified,
      textDecoration = textDecoration(),
      textAlign = textAlign(),
      minLines = integer("minLines", 1).coerceAtLeast(1),
      maxLines = integer("maxLines", Int.MAX_VALUE).coerceAtLeast(1),
      softWrap = boolean("softWrap", true),
      overflow = textOverflow(),
      onTextLayout = ::recordTextLayout,
    )
  }
}

private fun CanvasAdapterRegistry.Builder.textField(id: String, outlined: Boolean) {
  register(id) {
    val content: @Composable (String, (String) -> Unit) -> Unit = { value, change ->
      if (outlined) {
        OutlinedTextField(
          value,
          change,
          modifier,
          enabled = boolean("enabled", true),
          readOnly = boolean("readOnly"),
          label = optional("label"),
          placeholder = optional("placeholder"),
          supportingText = optional("supportingText"),
          leadingIcon = optional("leadingIcon"),
          trailingIcon = optional("trailingIcon"),
          isError = boolean("isError"),
          singleLine = boolean("singleLine", true),
        )
      } else {
        TextField(
          value,
          change,
          modifier,
          enabled = boolean("enabled", true),
          readOnly = boolean("readOnly"),
          label = optional("label"),
          placeholder = optional("placeholder"),
          supportingText = optional("supportingText"),
          leadingIcon = optional("leadingIcon"),
          trailingIcon = optional("trailingIcon"),
          isError = boolean("isError"),
          singleLine = boolean("singleLine", true),
        )
      }
    }
    content(string("value")) { updateBoundState("value", it) }
  }
}

private fun CanvasNodeScope.click() = dispatch("click")

@Composable
private fun CanvasNodeScope.optional(name: String): (@Composable () -> Unit)? {
  if (itemCount(name) == 0) return null
  return { Slot(name) }
}

@Composable
private fun CanvasNodeScope.color(name: String, fallback: Color): Color =
  materialColor(string(name), fallback)

@Composable
private fun CanvasNodeScope.textStyle(): TextStyle =
  when (string("style")) {
    "displayLarge" -> MaterialTheme.typography.displayLarge
    "displayMedium" -> MaterialTheme.typography.displayMedium
    "displaySmall" -> MaterialTheme.typography.displaySmall
    "headlineLarge" -> MaterialTheme.typography.headlineLarge
    "headlineMedium" -> MaterialTheme.typography.headlineMedium
    "headlineSmall" -> MaterialTheme.typography.headlineSmall
    "titleLarge" -> MaterialTheme.typography.titleLarge
    "titleMedium" -> MaterialTheme.typography.titleMedium
    "titleSmall" -> MaterialTheme.typography.titleSmall
    "bodyLarge" -> MaterialTheme.typography.bodyLarge
    "bodyMedium" -> MaterialTheme.typography.bodyMedium
    "bodySmall" -> MaterialTheme.typography.bodySmall
    "labelLarge" -> MaterialTheme.typography.labelLarge
    "labelMedium" -> MaterialTheme.typography.labelMedium
    "labelSmall" -> MaterialTheme.typography.labelSmall
    else -> TextStyle.Default
  }

private fun CanvasNodeScope.fontWeight(): FontWeight? =
  when (string("fontWeight")) {
    "thin" -> FontWeight.Thin
    "extraLight" -> FontWeight.ExtraLight
    "light" -> FontWeight.Light
    "medium" -> FontWeight.Medium
    "semiBold" -> FontWeight.SemiBold
    "bold" -> FontWeight.Bold
    "extraBold" -> FontWeight.ExtraBold
    "black" -> FontWeight.Black
    "normal" -> FontWeight.Normal
    else -> null
  }

private fun CanvasNodeScope.fontStyle(): FontStyle? =
  when (string("fontStyle")) {
    "italic" -> FontStyle.Italic
    "normal" -> FontStyle.Normal
    else -> null
  }

private fun CanvasNodeScope.textOverflow(): TextOverflow =
  when (string("overflow")) {
    "ellipsis" -> TextOverflow.Ellipsis
    "visible" -> TextOverflow.Visible
    else -> TextOverflow.Clip
  }

private fun CanvasNodeScope.textAlign(): TextAlign? =
  when (string("textAlign")) {
    "left" -> TextAlign.Left
    "right" -> TextAlign.Right
    "center" -> TextAlign.Center
    "justify" -> TextAlign.Justify
    "start" -> TextAlign.Start
    "end" -> TextAlign.End
    else -> null
  }

private fun CanvasNodeScope.textDecoration(): TextDecoration? =
  when (string("textDecoration")) {
    "underline" -> TextDecoration.Underline
    "lineThrough" -> TextDecoration.LineThrough
    else -> null
  }

/** Gregorian civil date to Unix epoch milliseconds, without a JVM-only date dependency. */
private fun String.toEpochMillisOrNull(): Long? {
  val parts = split('-')
  if (parts.size != 3) return null
  var year = parts[0].toIntOrNull() ?: return null
  val month = parts[1].toIntOrNull()?.takeIf { it in 1..12 } ?: return null
  val day = parts[2].toIntOrNull()?.takeIf { it in 1..31 } ?: return null
  year -= if (month <= 2) 1 else 0
  val era = if (year >= 0) year / 400 else (year - 399) / 400
  val yearOfEra = year - era * 400
  val shiftedMonth = month + if (month > 2) -3 else 9
  val dayOfYear = (153 * shiftedMonth + 2) / 5 + day - 1
  val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
  return (era * 146097L + dayOfEra - 719468L) * 86_400_000L
}
