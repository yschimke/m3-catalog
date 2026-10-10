@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.typed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
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
import androidx.window.core.layout.WindowSizeClass
import ee.schimke.composeai.uibuilder.LocalUiBuilderFontFamilies
import ee.schimke.composeai.uibuilder.LocalUiBuilderFontRegistry
import ee.schimke.composeai.uibuilder.LocalUiBuilderFontVariants
import ee.schimke.composeai.uibuilder.export.FontSettings
import ee.schimke.composeai.uibuilder.export.VariableFontText
import ee.schimke.composeai.uibuilder.renderer.sdk.googleMaterialIconImageVector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop

object TypedButtonDefaults {
  const val style: String = ""
  const val enabled: Boolean = true
}

@Composable
fun M3Button(
  style: String = TypedButtonDefaults.style,
  enabled: Boolean = TypedButtonDefaults.enabled,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  when (style) {
    "filledTonal" -> FilledTonalButton(onClick, modifier, enabled = enabled) { content() }
    "text" -> TextButton(onClick, modifier, enabled = enabled) { content() }
    "fab" -> FloatingActionButton(onClick, modifier) { content() }
    else -> Button(onClick, modifier, enabled = enabled) { content() }
  }
}

object TypedCardDefaults {
  const val containerColor: String = ""
  const val elevationDp: Float = 0f
}

@Composable
fun M3Card(
  containerColor: String = TypedCardDefaults.containerColor,
  elevationDp: Float = TypedCardDefaults.elevationDp,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    colors =
      CardDefaults.cardColors(
        containerColor = materialColor(containerColor, MaterialTheme.colorScheme.surfaceContainer)
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = elevationDp.dp),
  ) {
    content()
  }
}

object TypedElevatedCardDefaults {
  const val containerColor: String = ""
  const val elevationDp: Float = 1f
}

@Composable
fun M3ElevatedCard(
  containerColor: String = TypedElevatedCardDefaults.containerColor,
  elevationDp: Float = TypedElevatedCardDefaults.elevationDp,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  ElevatedCard(
    modifier = modifier,
    colors =
      CardDefaults.elevatedCardColors(
        containerColor =
          materialColor(containerColor, MaterialTheme.colorScheme.surfaceContainerLow)
      ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevationDp.dp),
  ) {
    content()
  }
}

object TypedOutlinedCardDefaults {
  const val containerColor: String = ""
  const val elevationDp: Float = 0f
}

@Composable
fun M3OutlinedCard(
  containerColor: String = TypedOutlinedCardDefaults.containerColor,
  elevationDp: Float = TypedOutlinedCardDefaults.elevationDp,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  OutlinedCard(
    modifier = modifier,
    colors =
      CardDefaults.outlinedCardColors(
        containerColor = materialColor(containerColor, MaterialTheme.colorScheme.surfaceContainer)
      ),
    elevation = CardDefaults.outlinedCardElevation(defaultElevation = elevationDp.dp),
  ) {
    content()
  }
}

object TypedCenterAlignedTopAppBarDefaults {
  const val containerColor: String = ""
  const val scrolledContainerColor: String = ""
}

@Composable
fun M3CenterAlignedTopAppBar(
  containerColor: String = TypedCenterAlignedTopAppBarDefaults.containerColor,
  scrolledContainerColor: String = TypedCenterAlignedTopAppBarDefaults.scrolledContainerColor,
  title: @Composable () -> Unit = {},
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  CenterAlignedTopAppBar(
    title = { title() },
    modifier = modifier,
    navigationIcon = { navigationIcon() },
    actions = { actions() },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = materialColor(containerColor, Color.Transparent),
        scrolledContainerColor = materialColor(scrolledContainerColor, Color.Transparent),
      ),
  )
}

object TypedCheckboxDefaults {
  const val checked: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3Checkbox(
  checked: Boolean = TypedCheckboxDefaults.checked,
  enabled: Boolean = TypedCheckboxDefaults.enabled,
  onCheckedChange: (Boolean) -> Unit = {},
  modifier: Modifier = Modifier,
) {

  Checkbox(
    checked = checked,
    onCheckedChange = { onCheckedChange(it) },
    modifier = modifier,
    enabled = enabled,
  )
}

/** 2025-08-17 UTC: the same day `DatePickers.kt` pins its stickers to. */
private const val PINNED_DATE_PICKER_MILLIS = 1755388800000L

object TypedDatePickerDefaults {
  const val selectedDate: String = ""
  const val mode: String = ""
  const val showModeToggle: Boolean = true
}

@Composable
fun M3DatePicker(
  selectedDate: String = TypedDatePickerDefaults.selectedDate,
  mode: String = TypedDatePickerDefaults.mode,
  showModeToggle: Boolean = TypedDatePickerDefaults.showModeToggle,
  modifier: Modifier = Modifier,
) {

  // A missing or unparseable date falls back to the stickers' pinned day (2025-08-17) rather than
  // `null`, which would open on the current month and make every render depend on the clock.
  val initial = selectedDate.toEpochMillisOrNull() ?: PINNED_DATE_PICKER_MILLIS
  val mode = if (mode == "input") DisplayMode.Input else DisplayMode.Picker
  key(initial, mode) {
    val state =
      rememberDatePickerState(
        initialSelectedDateMillis = initial,
        initialDisplayedMonthMillis = initial,
        initialDisplayMode = mode,
      )
    DatePicker(state, modifier, showModeToggle = showModeToggle)
  }
}

object TypedDialogDefaults {
  const val containerColor: String = ""
  const val tonalElevationDp: Float = 6f
}

@Composable
fun M3Dialog(
  containerColor: String = TypedDialogDefaults.containerColor,
  tonalElevationDp: Float = TypedDialogDefaults.tonalElevationDp,
  confirmButton: @Composable () -> Unit = {},
  dismissButton: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  text: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  AlertDialog(
    onDismissRequest = {},
    confirmButton = { confirmButton() },
    modifier = modifier,
    dismissButton = dismissButton,
    icon = icon,
    title = title,
    text = text,
    containerColor = materialColor(containerColor, MaterialTheme.colorScheme.surfaceContainerHigh),
    tonalElevation = tonalElevationDp.dp,
  )
}

object TypedFilterChipDefaults {
  const val selected: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3FilterChip(
  selected: Boolean = TypedFilterChipDefaults.selected,
  enabled: Boolean = TypedFilterChipDefaults.enabled,
  onClick: () -> Unit = {},
  label: @Composable () -> Unit = {},
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  FilterChip(
    selected = selected,
    onClick = onClick,
    label = { label() },
    modifier = modifier,
    enabled = enabled,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
  )
}

object TypedFloatingActionButtonDefaults {}

@Composable
fun M3FloatingActionButton(
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  FloatingActionButton(onClick, modifier) { content() }
}

object TypedHorizontalDividerDefaults {
  const val thicknessDp: Float = 1f
  const val color: String = ""
}

@Composable
fun M3HorizontalDivider(
  thicknessDp: Float = TypedHorizontalDividerDefaults.thicknessDp,
  color: String = TypedHorizontalDividerDefaults.color,
  modifier: Modifier = Modifier,
) {

  HorizontalDivider(
    modifier,
    thickness = thicknessDp.dp,
    color = materialColor(color, MaterialTheme.colorScheme.outlineVariant),
  )
}

object TypedHorizontalFloatingToolbarDefaults {
  const val expanded: Boolean = true
  const val containerColor: String = ""
}

@Composable
fun M3HorizontalFloatingToolbar(
  expanded: Boolean = TypedHorizontalFloatingToolbarDefaults.expanded,
  containerColor: String = TypedHorizontalFloatingToolbarDefaults.containerColor,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  HorizontalFloatingToolbar(
    expanded = expanded,
    modifier = modifier,
    colors =
      FloatingToolbarDefaults.standardFloatingToolbarColors(
        toolbarContainerColor =
          materialColor(containerColor, MaterialTheme.colorScheme.primaryContainer)
      ),
  ) {
    content()
  }
}

object TypedIconDefaults {
  const val iconKey: String = ""
  const val sizeDp: Float = 24f
  const val contentDescription: String = ""
  const val color: String = ""
}

@Composable
fun M3Icon(
  iconKey: String = TypedIconDefaults.iconKey,
  sizeDp: Float = TypedIconDefaults.sizeDp,
  contentDescription: String = TypedIconDefaults.contentDescription,
  color: String = TypedIconDefaults.color,
  modifier: Modifier = Modifier,
) {

  val vector = googleMaterialIconImageVector(iconKey)
  if (vector == null) Text("?", modifier.size(sizeDp.dp))
  else
    Icon(
      vector,
      contentDescription.ifEmpty { null },
      modifier.size(sizeDp.dp),
      tint = materialColor(color, MaterialTheme.colorScheme.onSurface),
    )
}

object TypedIconButtonDefaults {
  const val sizeDp: Float = 48f
  const val enabled: Boolean = true
}

@Composable
fun M3IconButton(
  sizeDp: Float = TypedIconButtonDefaults.sizeDp,
  enabled: Boolean = TypedIconButtonDefaults.enabled,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  IconButton(onClick = onClick, modifier = modifier.size(sizeDp.dp), enabled = enabled) {
    content()
  }
}

object TypedFilledIconButtonDefaults {
  const val sizeDp: Float = 48f
  const val enabled: Boolean = true
}

@Composable
fun M3FilledIconButton(
  sizeDp: Float = TypedFilledIconButtonDefaults.sizeDp,
  enabled: Boolean = TypedFilledIconButtonDefaults.enabled,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  FilledIconButton(onClick = onClick, modifier = modifier.size(sizeDp.dp), enabled = enabled) {
    content()
  }
}

object TypedFilledTonalIconButtonDefaults {
  const val sizeDp: Float = 48f
  const val enabled: Boolean = true
}

@Composable
fun M3FilledTonalIconButton(
  sizeDp: Float = TypedFilledTonalIconButtonDefaults.sizeDp,
  enabled: Boolean = TypedFilledTonalIconButtonDefaults.enabled,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  FilledTonalIconButton(onClick = onClick, modifier = modifier.size(sizeDp.dp), enabled = enabled) {
    content()
  }
}

object TypedOutlinedIconButtonDefaults {
  const val sizeDp: Float = 48f
  const val enabled: Boolean = true
}

@Composable
fun M3OutlinedIconButton(
  sizeDp: Float = TypedOutlinedIconButtonDefaults.sizeDp,
  enabled: Boolean = TypedOutlinedIconButtonDefaults.enabled,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  OutlinedIconButton(onClick = onClick, modifier = modifier.size(sizeDp.dp), enabled = enabled) {
    content()
  }
}

object TypedListItemDefaults {}

@Composable
fun M3ListItem(
  headline: @Composable () -> Unit = {},
  supporting: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  ListItem(
    headlineContent = { headline() },
    modifier = modifier,
    supportingContent = supporting,
    leadingContent = leading,
    trailingContent = trailing,
  )
}

object TypedPrimaryTabRowDefaults {
  const val selectedIndex: Int = 0
}

@Composable
fun M3PrimaryTabRow(
  selectedIndex: Int = TypedPrimaryTabRowDefaults.selectedIndex,
  tabs: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  PrimaryTabRow(selectedIndex.coerceAtLeast(0), modifier) { tabs() }
}

object TypedLinearProgressIndicatorDefaults {
  const val progress: Float = 0f
  const val indeterminate: Boolean = false
}

@Composable
fun M3LinearProgressIndicator(
  progress: Float = TypedLinearProgressIndicatorDefaults.progress,
  indeterminate: Boolean = TypedLinearProgressIndicatorDefaults.indeterminate,
  modifier: Modifier = Modifier,
) {

  val normalizedProgress = progress.coerceIn(0f, 1f)
  if (indeterminate) LinearProgressIndicator(modifier = modifier)
  else LinearProgressIndicator(progress = { normalizedProgress }, modifier = modifier)
}

object TypedCircularProgressIndicatorDefaults {
  const val progress: Float = 0f
  const val indeterminate: Boolean = false
}

@Composable
fun M3CircularProgressIndicator(
  progress: Float = TypedCircularProgressIndicatorDefaults.progress,
  indeterminate: Boolean = TypedCircularProgressIndicatorDefaults.indeterminate,
  modifier: Modifier = Modifier,
) {

  val normalizedProgress = progress.coerceIn(0f, 1f)
  if (indeterminate) CircularProgressIndicator(modifier = modifier)
  else CircularProgressIndicator(progress = { normalizedProgress }, modifier = modifier)
}

object TypedProvideTextStyleDefaults {
  const val style: String = ""
}

@Composable
fun M3ProvideTextStyle(
  style: String = TypedProvideTextStyleDefaults.style,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Box(modifier) { ProvideTextStyle(typedTextStyle(style)) { content() } }
}

object TypedRadioButtonDefaults {
  const val selected: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3RadioButton(
  selected: Boolean = TypedRadioButtonDefaults.selected,
  enabled: Boolean = TypedRadioButtonDefaults.enabled,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  RadioButton(
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
  )
}

object TypedSearchBarDefaults {
  const val expanded: Boolean = false
}

@Composable
fun M3SearchBar(
  expanded: Boolean = TypedSearchBarDefaults.expanded,
  inputField: @Composable () -> Unit = {},
  expandedContent: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  SearchBar(
    inputField = { inputField() },
    expanded = expanded,
    onExpandedChange = {},
    modifier = modifier,
  ) {
    expandedContent()
  }
}

object TypedSearchInputFieldDefaults {
  const val value: String = ""
  const val enabled: Boolean = true
  const val readOnly: Boolean = false
}

@Composable
fun M3SearchInputField(
  value: String = TypedSearchInputFieldDefaults.value,
  enabled: Boolean = TypedSearchInputFieldDefaults.enabled,
  readOnly: Boolean = TypedSearchInputFieldDefaults.readOnly,
  onValueChange: (String) -> Unit = {},
  onSearch: () -> Unit = {},
  placeholder: (@Composable () -> Unit)? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  key(value) {
    val state = remember { androidx.compose.foundation.text.input.TextFieldState(value) }
    val searchState = rememberSearchBarState(SearchBarValue.Collapsed)
    LaunchedEffect(state) {
      snapshotFlow { state.text.toString() }.drop(1).collect { query -> onValueChange(query) }
    }
    SearchBarDefaults.InputField(
      textFieldState = state,
      searchBarState = searchState,
      onSearch = { onSearch() },
      modifier = modifier,
      enabled = enabled,
      readOnly = readOnly,
      placeholder = placeholder,
      leadingIcon = leadingIcon,
      trailingIcon = trailingIcon,
    )
  }
}

object TypedSliderDefaults {
  const val valueFrom: Float = 0f
  const val valueTo: Float = 1f
  const val value: Float = 0f
  const val enabled: Boolean = true
  const val steps: Int = 0
}

@Composable
fun M3Slider(
  valueFrom: Float = TypedSliderDefaults.valueFrom,
  valueTo: Float = TypedSliderDefaults.valueTo,
  value: Float = TypedSliderDefaults.value,
  enabled: Boolean = TypedSliderDefaults.enabled,
  steps: Int = TypedSliderDefaults.steps,
  onValueChange: (Float) -> Unit = {},
  modifier: Modifier = Modifier,
) {

  val from = valueFrom
  val to = valueTo.coerceAtLeast(from)
  Slider(
    value = value.coerceIn(from, to),
    onValueChange = { onValueChange(it) },
    modifier = modifier,
    enabled = enabled,
    valueRange = from..to,
    steps = steps.coerceAtLeast(0),
  )
}

object TypedSurfaceDefaults {
  const val containerColor: String = ""
  const val contentColor: String = ""
  const val tonalElevationDp: Float = 0f
}

@Composable
fun M3Surface(
  containerColor: String = TypedSurfaceDefaults.containerColor,
  contentColor: String = TypedSurfaceDefaults.contentColor,
  tonalElevationDp: Float = TypedSurfaceDefaults.tonalElevationDp,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  Surface(
    modifier,
    color = materialColor(containerColor, Color.Transparent),
    contentColor = materialColor(contentColor, MaterialTheme.colorScheme.onSurface),
    tonalElevation = tonalElevationDp.dp,
  ) {
    content()
  }
}

object TypedSwitchDefaults {
  const val checked: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3Switch(
  checked: Boolean = TypedSwitchDefaults.checked,
  enabled: Boolean = TypedSwitchDefaults.enabled,
  onCheckedChange: (Boolean) -> Unit = {},
  thumbContent: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  Switch(
    checked = checked,
    onCheckedChange = { onCheckedChange(it) },
    modifier = modifier,
    enabled = enabled,
    thumbContent = thumbContent,
  )
}

object TypedTabDefaults {
  const val selected: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3Tab(
  selected: Boolean = TypedTabDefaults.selected,
  enabled: Boolean = TypedTabDefaults.enabled,
  onClick: () -> Unit = {},
  text: (@Composable () -> Unit)? = null,
  icon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  Tab(
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    text = text,
    icon = icon,
  )
}

object TypedTextDefaults {
  const val text: String = ""
  const val color: String = ""
  const val fontSizeSp: Float = 0f
  const val lineHeightSp: Float = 0f
  const val letterSpacingSp: Float = Float.MIN_VALUE
  const val minLines: Int = 1
  const val maxLines: Int = Int.MAX_VALUE
  const val softWrap: Boolean = true
  const val style: String = ""
  const val fontWeight: String = ""
  const val fontStyle: String = ""
  const val overflow: String = ""
  const val textAlign: String = ""
  const val textDecoration: String = ""
}

@Composable
fun M3Text(
  text: String = TypedTextDefaults.text,
  color: String = TypedTextDefaults.color,
  fontSizeSp: Float = TypedTextDefaults.fontSizeSp,
  lineHeightSp: Float = TypedTextDefaults.lineHeightSp,
  letterSpacingSp: Float = TypedTextDefaults.letterSpacingSp,
  minLines: Int = TypedTextDefaults.minLines,
  maxLines: Int = TypedTextDefaults.maxLines,
  softWrap: Boolean = TypedTextDefaults.softWrap,
  style: String = TypedTextDefaults.style,
  fontWeight: String = TypedTextDefaults.fontWeight,
  fontStyle: String = TypedTextDefaults.fontStyle,
  overflow: String = TypedTextDefaults.overflow,
  textAlign: String = TypedTextDefaults.textAlign,
  textDecoration: String = TypedTextDefaults.textDecoration,
  modifier: Modifier = Modifier,
  onTextLayout: (androidx.compose.ui.text.TextLayoutResult) -> Unit = {},
) {

  Text(
    text = text,
    modifier = modifier,
    color = materialColor(color, Color.Unspecified),
    style = typedTextStyle(style),
    fontWeight = typedFontWeight(fontWeight),
    fontStyle = typedFontStyle(fontStyle),
    fontSize = fontSizeSp.takeIf { it > 0f }?.sp ?: TextUnit.Unspecified,
    lineHeight = lineHeightSp.takeIf { it > 0f }?.sp ?: TextUnit.Unspecified,
    letterSpacing = letterSpacingSp.takeIf { it != Float.MIN_VALUE }?.sp ?: TextUnit.Unspecified,
    textDecoration = typedTextDecoration(textDecoration),
    textAlign = typedTextAlign(textAlign),
    minLines = minLines.coerceAtLeast(1),
    maxLines = maxLines.coerceAtLeast(1),
    softWrap = softWrap,
    overflow = typedTextOverflow(overflow),
    onTextLayout = onTextLayout,
  )
}

object TypedTextFieldDefaults {
  const val value: String = ""
  const val enabled: Boolean = true
  const val readOnly: Boolean = false
  const val isError: Boolean = false
  const val singleLine: Boolean = true
}

@Composable
fun M3TextField(
  value: String = TypedTextFieldDefaults.value,
  enabled: Boolean = TypedTextFieldDefaults.enabled,
  readOnly: Boolean = TypedTextFieldDefaults.readOnly,
  isError: Boolean = TypedTextFieldDefaults.isError,
  singleLine: Boolean = TypedTextFieldDefaults.singleLine,
  onValueChange: (String) -> Unit = {},
  label: (@Composable () -> Unit)? = null,
  placeholder: (@Composable () -> Unit)? = null,
  supportingText: (@Composable () -> Unit)? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  TextField(
    value = value,
    onValueChange = { onValueChange(it) },
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    label = label,
    placeholder = placeholder,
    supportingText = supportingText,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    isError = isError,
    singleLine = singleLine,
  )
}

object TypedOutlinedTextFieldDefaults {
  const val value: String = ""
  const val enabled: Boolean = true
  const val readOnly: Boolean = false
  const val isError: Boolean = false
  const val singleLine: Boolean = true
}

@Composable
fun M3OutlinedTextField(
  value: String = TypedOutlinedTextFieldDefaults.value,
  enabled: Boolean = TypedOutlinedTextFieldDefaults.enabled,
  readOnly: Boolean = TypedOutlinedTextFieldDefaults.readOnly,
  isError: Boolean = TypedOutlinedTextFieldDefaults.isError,
  singleLine: Boolean = TypedOutlinedTextFieldDefaults.singleLine,
  onValueChange: (String) -> Unit = {},
  label: (@Composable () -> Unit)? = null,
  placeholder: (@Composable () -> Unit)? = null,
  supportingText: (@Composable () -> Unit)? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  OutlinedTextField(
    value = value,
    onValueChange = { onValueChange(it) },
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    label = label,
    placeholder = placeholder,
    supportingText = supportingText,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    isError = isError,
    singleLine = singleLine,
  )
}

object TypedTimePickerDefaults {
  const val hour: Int = 0
  const val minute: Int = 0
  const val is24Hour: Boolean = false
  const val mode: String = ""
}

@Composable
fun M3TimePicker(
  hour: Int = TypedTimePickerDefaults.hour,
  minute: Int = TypedTimePickerDefaults.minute,
  is24Hour: Boolean = TypedTimePickerDefaults.is24Hour,
  mode: String = TypedTimePickerDefaults.mode,
  modifier: Modifier = Modifier,
) {

  val normalizedHour = hour.coerceIn(0, 23)
  val normalizedMinute = minute.coerceIn(0, 59)
  key(hour, minute, is24Hour) {
    val state =
      rememberTimePickerState(
        initialHour = normalizedHour,
        initialMinute = normalizedMinute,
        is24Hour = is24Hour,
      )
    if (mode == "input") TimeInput(state = state, modifier = modifier)
    else TimePicker(state = state, modifier = modifier, layoutType = TimePickerLayoutType.Vertical)
  }
}

object TypedStickerDefaults {}

@Composable
fun M3Sticker(
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Box(modifier) { content() }
}

object TypedMaterialExpressiveThemeDefaults {}

@Composable
fun M3MaterialExpressiveTheme(
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  MaterialExpressiveTheme { Box(modifier) { content() } }
}

object TypedNavigationSuiteItemDefaults {
  const val selected: Boolean = false
  const val enabled: Boolean = true
}

@Composable
fun M3NavigationSuiteItem(
  selected: Boolean = TypedNavigationSuiteItemDefaults.selected,
  enabled: Boolean = TypedNavigationSuiteItemDefaults.enabled,
  onClick: () -> Unit = {},
  icon: @Composable () -> Unit = {},
  label: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {

  // Styled for the type its scaffold resolved, not the default, which reads the host window.
  NavigationSuiteItem(
    selected = selected,
    onClick = onClick,
    icon = { icon() },
    label = label,
    modifier = modifier,
    navigationSuiteType = LocalTypedNavigationSuiteType.current,
    enabled = enabled,
  )
}

object TypedNavigationSuiteScaffoldDefaults {
  const val containerColor: String = ""
  const val contentColor: String = ""
}

@Composable
fun M3NavigationSuiteScaffold(
  containerColor: String = TypedNavigationSuiteScaffoldDefaults.containerColor,
  contentColor: String = TypedNavigationSuiteScaffoldDefaults.contentColor,
  navigationItems: @Composable () -> Unit = {},
  primaryAction: @Composable () -> Unit = {},
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  // The suite type is measured from the frame the design is drawn in, as the catalog's own
  // `NavigationSuiteScaffoldSticker` does: `currentWindowAdaptiveInfo()` would read the host
  // window, so a phone frame beside a tablet frame would draw the same navigation component.
  BoxWithConstraints(modifier) {
    val width = maxWidth
    val height = maxHeight
    val type =
      remember(width, height) {
        NavigationSuiteScaffoldDefaults.navigationSuiteType(
          WindowAdaptiveInfo(WindowSizeClass.compute(width.value, height.value), Posture())
        )
      }
    CompositionLocalProvider(LocalTypedNavigationSuiteType provides type) {
      NavigationSuiteScaffold(
        navigationItems = { navigationItems() },
        navigationSuiteType = type,
        containerColor = materialColor(containerColor, MaterialTheme.colorScheme.surface),
        contentColor = materialColor(contentColor, MaterialTheme.colorScheme.onSurface),
        primaryActionContent = { primaryAction() },
      ) {
        content()
      }
    }
  }
}

object TypedPrimaryScrollableTabRowDefaults {
  const val selectedIndex: Int = 0
  const val containerColor: String = ""
  const val contentColor: String = ""
  const val edgePaddingDp: Float = 52f
  const val minTabWidthDp: Float = 90f
}

@Composable
fun M3PrimaryScrollableTabRow(
  selectedIndex: Int = TypedPrimaryScrollableTabRowDefaults.selectedIndex,
  containerColor: String = TypedPrimaryScrollableTabRowDefaults.containerColor,
  contentColor: String = TypedPrimaryScrollableTabRowDefaults.contentColor,
  edgePaddingDp: Float = TypedPrimaryScrollableTabRowDefaults.edgePaddingDp,
  minTabWidthDp: Float = TypedPrimaryScrollableTabRowDefaults.minTabWidthDp,
  tabs: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {

  PrimaryScrollableTabRow(
    selectedIndex.coerceAtLeast(0),
    modifier,
    containerColor = materialColor(containerColor, TabRowDefaults.primaryContainerColor),
    contentColor = materialColor(contentColor, TabRowDefaults.primaryContentColor),
    edgePadding = edgePaddingDp.dp,
    minTabWidth = minTabWidthDp.dp,
  ) {
    tabs()
  }
}

object TypedVariableFontTextDefaults {
  const val font: String = ""
  const val wght: Float = 400f
  const val wdth: Float = 100f
  const val slnt: Float = 0f
  const val rond: Float = 0f
  const val text: String = ""
  const val color: String = ""
  const val fontSizeSp: Float = 32f
}

@Composable
fun M3VariableFontText(
  font: String = TypedVariableFontTextDefaults.font,
  wght: Float = TypedVariableFontTextDefaults.wght,
  wdth: Float = TypedVariableFontTextDefaults.wdth,
  slnt: Float = TypedVariableFontTextDefaults.slnt,
  rond: Float = TypedVariableFontTextDefaults.rond,
  text: String = TypedVariableFontTextDefaults.text,
  color: String = TypedVariableFontTextDefaults.color,
  fontSizeSp: Float = TypedVariableFontTextDefaults.fontSizeSp,
  modifier: Modifier = Modifier,
) {
  val selectedFont = VariableFontText.Font.fromWire(font) ?: VariableFontText.Font.RobotoFlex
  val axes =
    selectedFont.axes.map { axis ->
      FontSettings.Axis(
        axis.tag,
        when (axis.property) {
          "wght" -> wght
          "wdth" -> wdth
          "slnt" -> slnt
          "rond" -> rond
          else -> axis.default
        },
      )
    }
  val registry = LocalUiBuilderFontRegistry.current
  LaunchedEffect(registry, selectedFont) { registry?.request(selectedFont.family) }
  val loaded = LocalUiBuilderFontFamilies.current[selectedFont.family]
  val family =
    if (loaded == null) null
    else LocalUiBuilderFontVariants.current?.variant(selectedFont.family, axes) ?: loaded
  Text(
    text = text,
    modifier = modifier,
    color = materialColor(color, Color.Unspecified),
    style =
      TextStyle(
        fontFamily = family,
        fontWeight = if (family == null) FontWeight(wght.toInt().coerceIn(1, 1000)) else null,
        fontSize = fontSizeSp.sp,
      ),
  )
}

object TypedSnackbarHostDefaults {
  const val visible: Boolean = false
  const val actionLabel: String = ""
  const val message: String = ""
}

@Composable
fun M3SnackbarHost(
  visible: Boolean = TypedSnackbarHostDefaults.visible,
  actionLabel: String = TypedSnackbarHostDefaults.actionLabel,
  message: String = TypedSnackbarHostDefaults.message,
  onClick: () -> Unit = {},
  snackbar: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  if (visible) {
    if (snackbar != null) Box(modifier) { snackbar() }
    else
      Snackbar(
        modifier = modifier,
        action =
          if (actionLabel.isNotEmpty()) ({ TextButton(onClick) { Text(actionLabel) } }) else null,
      ) {
        Text(message)
      }
  } else Box(modifier)
}

/** The type the enclosing `material3/NavigationSuiteScaffold` resolved for its frame. */
private val LocalTypedNavigationSuiteType = compositionLocalOf {
  NavigationSuiteType.ShortNavigationBarCompact
}

@Composable
private fun typedTextStyle(style: String): TextStyle =
  when (style) {
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

private fun typedFontWeight(fontWeight: String): FontWeight? =
  when (fontWeight) {
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

private fun typedFontStyle(fontStyle: String): FontStyle? =
  when (fontStyle) {
    "italic" -> FontStyle.Italic
    "normal" -> FontStyle.Normal
    else -> null
  }

private fun typedTextOverflow(overflow: String): TextOverflow =
  when (overflow) {
    "ellipsis" -> TextOverflow.Ellipsis
    "visible" -> TextOverflow.Visible
    else -> TextOverflow.Clip
  }

private fun typedTextAlign(textAlign: String): TextAlign? =
  when (textAlign) {
    "left" -> TextAlign.Left
    "right" -> TextAlign.Right
    "center" -> TextAlign.Center
    "justify" -> TextAlign.Justify
    "start" -> TextAlign.Start
    "end" -> TextAlign.End
    else -> null
  }

private fun typedTextDecoration(textDecoration: String): TextDecoration? =
  when (textDecoration) {
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
