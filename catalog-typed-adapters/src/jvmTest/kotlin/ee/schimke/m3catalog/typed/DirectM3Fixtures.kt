package ee.schimke.m3catalog.typed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DirectM3Fixture(id: String, modifier: Modifier) {
  when (id) {
    "m3/button" -> M3Button(content = { Text("Slot content") }, modifier = modifier)
    "m3/card" -> M3Card(content = { Text("Slot content") }, modifier = modifier)
    "m3/center-aligned-top-app-bar" ->
      M3CenterAlignedTopAppBar(
        title = { Text("Slot content") },
        navigationIcon = { Text("Slot content") },
        actions = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/checkbox" -> M3Checkbox(modifier = modifier)
    "m3/date-picker" -> M3DatePicker(modifier = modifier)
    "m3/dialog" ->
      M3Dialog(
        confirmButton = { Text("Slot content") },
        dismissButton = { Text("Slot content") },
        icon = { Text("Slot content") },
        title = { Text("Slot content") },
        text = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/filter-chip" ->
      M3FilterChip(
        label = { Text("Slot content") },
        leadingIcon = { Text("Slot content") },
        trailingIcon = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/floating-action-button" ->
      M3FloatingActionButton(content = { Text("Slot content") }, modifier = modifier)
    "m3/horizontal-divider" -> M3HorizontalDivider(modifier = modifier)
    "m3/horizontal-floating-toolbar" ->
      M3HorizontalFloatingToolbar(content = { Text("Slot content") }, modifier = modifier)
    "m3/icon" -> M3Icon(modifier = modifier)
    "m3/icon-button" -> M3IconButton(content = { Text("Slot content") }, modifier = modifier)
    "m3/list-item" ->
      M3ListItem(
        headline = { Text("Slot content") },
        supporting = { Text("Slot content") },
        leading = { Text("Slot content") },
        trailing = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/primary-tab-row" -> M3PrimaryTabRow(tabs = { Text("Slot content") }, modifier = modifier)
    "m3/linear-progress-indicator" -> M3LinearProgressIndicator(modifier = modifier)
    "m3/circular-progress-indicator" -> M3CircularProgressIndicator(modifier = modifier)
    "m3/provide-text-style" ->
      M3ProvideTextStyle(content = { Text("Slot content") }, modifier = modifier)
    "m3/radio-button" -> M3RadioButton(modifier = modifier)
    "m3/search-bar" ->
      M3SearchBar(
        inputField = { Text("Slot content") },
        expandedContent = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/search-input-field" ->
      M3SearchInputField(
        value = "Typed catalog",
        placeholder = { Text("Slot content") },
        leadingIcon = { Text("Slot content") },
        trailingIcon = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/slider" -> M3Slider(modifier = modifier)
    "m3/surface" -> M3Surface(content = { Text("Slot content") }, modifier = modifier)
    "m3/switch" -> M3Switch(thumbContent = { Text("Slot content") }, modifier = modifier)
    "m3/tab" ->
      M3Tab(text = { Text("Slot content") }, icon = { Text("Slot content") }, modifier = modifier)
    "m3/text" -> M3Text(text = "Typed catalog", modifier = modifier)
    "m3/text-field" ->
      M3TextField(
        value = "Typed catalog",
        label = { Text("Slot content") },
        placeholder = { Text("Slot content") },
        supportingText = { Text("Slot content") },
        leadingIcon = { Text("Slot content") },
        trailingIcon = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/outlined-text-field" ->
      M3OutlinedTextField(
        value = "Typed catalog",
        label = { Text("Slot content") },
        placeholder = { Text("Slot content") },
        supportingText = { Text("Slot content") },
        leadingIcon = { Text("Slot content") },
        trailingIcon = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/time-picker" -> M3TimePicker(modifier = modifier)
    "m3/sticker" -> M3Sticker(content = { Text("Slot content") }, modifier = modifier)
    "m3/material-expressive-theme" ->
      M3MaterialExpressiveTheme(content = { Text("Slot content") }, modifier = modifier)
    "m3/navigation-suite-item" ->
      M3NavigationSuiteItem(
        icon = { Text("Slot content") },
        label = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/navigation-suite-scaffold" ->
      M3NavigationSuiteScaffold(
        navigationItems = { Text("Slot content") },
        primaryAction = { Text("Slot content") },
        content = { Text("Slot content") },
        modifier = modifier,
      )
    "m3/primary-scrollable-tab-row" ->
      M3PrimaryScrollableTabRow(tabs = { Text("Slot content") }, modifier = modifier)
    "m3/variable-font-text" -> M3VariableFontText(text = "Typed catalog", modifier = modifier)
    "m3/snackbar-host" ->
      M3SnackbarHost(visible = true, snackbar = { Text("Slot content") }, modifier = modifier)
    else -> error("No fixture for $id")
  }
}
