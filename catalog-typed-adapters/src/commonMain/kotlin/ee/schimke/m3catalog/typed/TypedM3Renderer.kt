package ee.schimke.m3catalog.typed

import androidx.compose.runtime.Composable
import ee.schimke.composeai.discovery.AdapterSlot
import ee.schimke.composeai.uibuilder.renderer.sdk.TypedCanvasNodeScope
import ee.schimke.composeai.uibuilder.renderer.sdk.canvasAdapterRegistry
import ee.schimke.composeai.uibuilder.renderer.sdk.register

fun rawTypedM3CanvasAdapters(catalog: TypedM3Catalog) = canvasAdapterRegistry {
  register(catalog.button) {
    val adapter = catalog.button
    M3Button(
      style = value(adapter.style),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.card) {
    val adapter = catalog.card
    M3Card(
      style = value(adapter.style),
      containerColor = value(adapter.containerColor),
      elevationDp = value(adapter.elevationDp),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.centerAlignedTopAppBar) {
    val adapter = catalog.centerAlignedTopAppBar
    M3CenterAlignedTopAppBar(
      containerColor = value(adapter.containerColor),
      scrolledContainerColor = value(adapter.scrolledContainerColor),
      title = { Slot(adapter.title) },
      navigationIcon = { Slot(adapter.navigationIcon) },
      actions = { Slot(adapter.actions) },
      modifier = modifier,
    )
  }

  register(catalog.checkbox) {
    val adapter = catalog.checkbox
    M3Checkbox(
      checked = value(adapter.checked),
      enabled = value(adapter.enabled),
      onCheckedChange = callback(adapter.onCheckedChange),
      modifier = modifier,
    )
  }

  register(catalog.datePicker) {
    val adapter = catalog.datePicker
    M3DatePicker(
      selectedDate = value(adapter.selectedDate),
      mode = value(adapter.mode),
      showModeToggle = value(adapter.showModeToggle),
      modifier = modifier,
    )
  }

  register(catalog.dialog) {
    val adapter = catalog.dialog
    M3Dialog(
      containerColor = value(adapter.containerColor),
      tonalElevationDp = value(adapter.tonalElevationDp),
      confirmButton = { Slot(adapter.confirmButton) },
      dismissButton = optionalSlot(adapter.dismissButton),
      icon = optionalSlot(adapter.icon),
      title = optionalSlot(adapter.title),
      text = optionalSlot(adapter.text),
      modifier = modifier,
    )
  }

  register(catalog.filterChip) {
    val adapter = catalog.filterChip
    M3FilterChip(
      selected = value(adapter.selected),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      label = { Slot(adapter.label) },
      leadingIcon = optionalSlot(adapter.leadingIcon),
      trailingIcon = optionalSlot(adapter.trailingIcon),
      modifier = modifier,
    )
  }

  register(catalog.floatingActionButton) {
    val adapter = catalog.floatingActionButton
    M3FloatingActionButton(
      onClick = callback(adapter.onClick),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.horizontalDivider) {
    val adapter = catalog.horizontalDivider
    M3HorizontalDivider(
      thicknessDp = value(adapter.thicknessDp),
      color = value(adapter.color),
      modifier = modifier,
    )
  }

  register(catalog.horizontalFloatingToolbar) {
    val adapter = catalog.horizontalFloatingToolbar
    M3HorizontalFloatingToolbar(
      expanded = value(adapter.expanded),
      containerColor = value(adapter.containerColor),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.icon) {
    val adapter = catalog.icon
    M3Icon(
      iconKey = value(adapter.iconKey),
      sizeDp = value(adapter.sizeDp),
      contentDescription = value(adapter.contentDescription),
      color = value(adapter.color),
      modifier = modifier,
    )
  }

  register(catalog.iconButton) {
    val adapter = catalog.iconButton
    M3IconButton(
      style = value(adapter.style),
      sizeDp = value(adapter.sizeDp),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.listItem) {
    val adapter = catalog.listItem
    M3ListItem(
      headline = { Slot(adapter.headline) },
      supporting = optionalSlot(adapter.supporting),
      leading = optionalSlot(adapter.leading),
      trailing = optionalSlot(adapter.trailing),
      modifier = modifier,
    )
  }

  register(catalog.primaryTabRow) {
    val adapter = catalog.primaryTabRow
    M3PrimaryTabRow(
      selectedIndex = value(adapter.selectedIndex),
      tabs = { Slot(adapter.tabs) },
      modifier = modifier,
    )
  }

  register(catalog.linearProgressIndicator) {
    val adapter = catalog.linearProgressIndicator
    M3LinearProgressIndicator(
      progress = value(adapter.progress),
      indeterminate = value(adapter.indeterminate),
      modifier = modifier,
    )
  }

  register(catalog.circularProgressIndicator) {
    val adapter = catalog.circularProgressIndicator
    M3CircularProgressIndicator(
      progress = value(adapter.progress),
      indeterminate = value(adapter.indeterminate),
      modifier = modifier,
    )
  }

  register(catalog.provideTextStyle) {
    val adapter = catalog.provideTextStyle
    M3ProvideTextStyle(
      style = value(adapter.style),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.radioButton) {
    val adapter = catalog.radioButton
    M3RadioButton(
      selected = value(adapter.selected),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      modifier = modifier,
    )
  }

  register(catalog.searchBar) {
    val adapter = catalog.searchBar
    M3SearchBar(
      expanded = value(adapter.expanded),
      inputField = { Slot(adapter.inputField) },
      expandedContent = { Slot(adapter.expandedContent) },
      modifier = modifier,
    )
  }

  register(catalog.searchInputField) {
    val adapter = catalog.searchInputField
    M3SearchInputField(
      value = value(adapter.value),
      enabled = value(adapter.enabled),
      readOnly = value(adapter.readOnly),
      onValueChange = m3StateCallback(adapter.onValueChange, "queryChange"),
      onSearch = callback(adapter.onSearch),
      placeholder = optionalSlot(adapter.placeholder),
      leadingIcon = optionalSlot(adapter.leadingIcon),
      trailingIcon = optionalSlot(adapter.trailingIcon),
      modifier = modifier,
    )
  }

  register(catalog.slider) {
    val adapter = catalog.slider
    M3Slider(
      valueFrom = value(adapter.valueFrom),
      valueTo = value(adapter.valueTo),
      value = value(adapter.value),
      enabled = value(adapter.enabled),
      steps = value(adapter.steps),
      onValueChange = m3StateCallback(adapter.onValueChange, "valueChange"),
      modifier = modifier,
    )
  }

  register(catalog.surface) {
    val adapter = catalog.surface
    M3Surface(
      containerColor = value(adapter.containerColor),
      contentColor = value(adapter.contentColor),
      tonalElevationDp = value(adapter.tonalElevationDp),
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.switch) {
    val adapter = catalog.switch
    M3Switch(
      checked = value(adapter.checked),
      enabled = value(adapter.enabled),
      onCheckedChange = callback(adapter.onCheckedChange),
      thumbContent = optionalSlot(adapter.thumbContent),
      modifier = modifier,
    )
  }

  register(catalog.tab) {
    val adapter = catalog.tab
    M3Tab(
      selected = value(adapter.selected),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      text = optionalSlot(adapter.text),
      icon = optionalSlot(adapter.icon),
      modifier = modifier,
    )
  }

  register(catalog.text) {
    val adapter = catalog.text
    M3Text(
      text = value(adapter.text),
      color = value(adapter.color),
      fontSizeSp = value(adapter.fontSizeSp),
      lineHeightSp = value(adapter.lineHeightSp),
      letterSpacingSp = value(adapter.letterSpacingSp),
      minLines = value(adapter.minLines),
      maxLines = value(adapter.maxLines),
      softWrap = value(adapter.softWrap),
      style = value(adapter.style),
      fontWeight = value(adapter.fontWeight),
      fontStyle = value(adapter.fontStyle),
      overflow = value(adapter.overflow),
      textAlign = value(adapter.textAlign),
      textDecoration = value(adapter.textDecoration),
      modifier = modifier,
      onTextLayout = canvas::recordTextLayout,
    )
  }

  register(catalog.textField) {
    val adapter = catalog.textField
    M3TextField(
      value = value(adapter.value),
      enabled = value(adapter.enabled),
      readOnly = value(adapter.readOnly),
      isError = value(adapter.isError),
      singleLine = value(adapter.singleLine),
      onValueChange = m3StateCallback(adapter.onValueChange, "valueChange"),
      label = optionalSlot(adapter.label),
      placeholder = optionalSlot(adapter.placeholder),
      supportingText = optionalSlot(adapter.supportingText),
      leadingIcon = optionalSlot(adapter.leadingIcon),
      trailingIcon = optionalSlot(adapter.trailingIcon),
      modifier = modifier,
    )
  }

  register(catalog.outlinedTextField) {
    val adapter = catalog.outlinedTextField
    M3OutlinedTextField(
      value = value(adapter.value),
      enabled = value(adapter.enabled),
      readOnly = value(adapter.readOnly),
      isError = value(adapter.isError),
      singleLine = value(adapter.singleLine),
      onValueChange = m3StateCallback(adapter.onValueChange, "valueChange"),
      label = optionalSlot(adapter.label),
      placeholder = optionalSlot(adapter.placeholder),
      supportingText = optionalSlot(adapter.supportingText),
      leadingIcon = optionalSlot(adapter.leadingIcon),
      trailingIcon = optionalSlot(adapter.trailingIcon),
      modifier = modifier,
    )
  }

  register(catalog.timePicker) {
    val adapter = catalog.timePicker
    M3TimePicker(
      hour = value(adapter.hour),
      minute = value(adapter.minute),
      is24Hour = value(adapter.is24Hour),
      mode = value(adapter.mode),
      modifier = modifier,
    )
  }

  register(catalog.sticker) {
    val adapter = catalog.sticker
    M3Sticker(
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.materialExpressiveTheme) {
    val adapter = catalog.materialExpressiveTheme
    M3MaterialExpressiveTheme(
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.navigationSuiteItem) {
    val adapter = catalog.navigationSuiteItem
    M3NavigationSuiteItem(
      selected = value(adapter.selected),
      enabled = value(adapter.enabled),
      onClick = callback(adapter.onClick),
      icon = { Slot(adapter.icon) },
      label = optionalSlot(adapter.label),
      modifier = modifier,
    )
  }

  register(catalog.navigationSuiteScaffold) {
    val adapter = catalog.navigationSuiteScaffold
    M3NavigationSuiteScaffold(
      containerColor = value(adapter.containerColor),
      contentColor = value(adapter.contentColor),
      navigationItems = { Slot(adapter.navigationItems) },
      primaryAction = { Slot(adapter.primaryAction) },
      content = { Slot(adapter.content) },
      modifier = modifier,
    )
  }

  register(catalog.primaryScrollableTabRow) {
    val adapter = catalog.primaryScrollableTabRow
    M3PrimaryScrollableTabRow(
      selectedIndex = value(adapter.selectedIndex),
      containerColor = value(adapter.containerColor),
      contentColor = value(adapter.contentColor),
      edgePaddingDp = value(adapter.edgePaddingDp),
      minTabWidthDp = value(adapter.minTabWidthDp),
      tabs = { Slot(adapter.tabs) },
      modifier = modifier,
    )
  }

  register(catalog.variableFontText) {
    val adapter = catalog.variableFontText
    M3VariableFontText(
      font = value(adapter.font),
      wght = value(adapter.wght),
      wdth = value(adapter.wdth),
      slnt = value(adapter.slnt),
      rond = value(adapter.rond),
      text = value(adapter.text),
      color = value(adapter.color),
      fontSizeSp = value(adapter.fontSizeSp),
      modifier = modifier,
    )
  }

  register(catalog.snackbarHost) {
    val adapter = catalog.snackbarHost
    M3SnackbarHost(
      visible = value(adapter.visible),
      actionLabel = value(adapter.actionLabel),
      message = value(adapter.message),
      onClick = callback(adapter.onClick),
      snackbar = optionalSlot(adapter.snackbar),
      modifier = modifier,
    )
  }
}

@Composable
private fun <P> TypedCanvasNodeScope<P>.optionalSlot(
  slot: AdapterSlot<P>
): (@Composable () -> Unit)? =
  if (canvas.itemCount(slot.name) == 0) null
  else {
    { Slot(slot) }
  }
