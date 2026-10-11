package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterValueCodecs
import ee.schimke.composeai.discovery.ComponentRecord
import ee.schimke.composeai.discovery.TypedComponentAdapter

data class ButtonProperties(val style: String, val enabled: Boolean, val onClick: () -> Unit)

class ButtonAdapter(record: ComponentRecord) :
  TypedComponentAdapter<ButtonProperties>("m3/button", record) {
  val style =
    property(
      ButtonProperties::style,
      AdapterValueCodecs.String,
      TypedButtonDefaults.style,
      bindable = false,
    )
  val enabled =
    property(
      ButtonProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedButtonDefaults.enabled,
      bindable = false,
    )
  val onClick = event(ButtonProperties::onClick)
  val content = slot("content")
}

data class CardProperties(val style: String, val containerColor: String, val elevationDp: Float)

class CardAdapter(record: ComponentRecord) :
  TypedComponentAdapter<CardProperties>("m3/card", record) {
  val style =
    property(
      CardProperties::style,
      AdapterValueCodecs.String,
      TypedCardDefaults.style,
      bindable = false,
    )
  val containerColor =
    property(
      CardProperties::containerColor,
      AdapterValueCodecs.String,
      TypedCardDefaults.containerColor,
      bindable = false,
    )
  val elevationDp =
    property(
      CardProperties::elevationDp,
      M3ValueCodecs.Float,
      TypedCardDefaults.elevationDp,
      bindable = false,
    )
  val content = slot("content")
}

data class CenterAlignedTopAppBarProperties(
  val containerColor: String,
  val scrolledContainerColor: String,
)

class CenterAlignedTopAppBarAdapter(record: ComponentRecord) :
  TypedComponentAdapter<CenterAlignedTopAppBarProperties>("m3/center-aligned-top-app-bar", record) {
  val containerColor =
    property(
      CenterAlignedTopAppBarProperties::containerColor,
      AdapterValueCodecs.String,
      TypedCenterAlignedTopAppBarDefaults.containerColor,
      bindable = false,
    )
  val scrolledContainerColor =
    property(
      CenterAlignedTopAppBarProperties::scrolledContainerColor,
      AdapterValueCodecs.String,
      TypedCenterAlignedTopAppBarDefaults.scrolledContainerColor,
      bindable = false,
    )
  val title = slot("title")
  val navigationIcon = slot("navigationIcon")
  val actions = slot("actions")
}

data class CheckboxProperties(
  val checked: Boolean,
  val enabled: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

class CheckboxAdapter(record: ComponentRecord) :
  TypedComponentAdapter<CheckboxProperties>("m3/checkbox", record) {
  val checked =
    property(
      CheckboxProperties::checked,
      AdapterValueCodecs.Boolean,
      TypedCheckboxDefaults.checked,
      bindable = true,
    )
  val enabled =
    property(
      CheckboxProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedCheckboxDefaults.enabled,
      bindable = false,
    )
  val onCheckedChange = stateChange(CheckboxProperties::onCheckedChange, checked)
}

data class DatePickerProperties(
  val selectedDate: String,
  val mode: String,
  val showModeToggle: Boolean,
)

class DatePickerAdapter(record: ComponentRecord) :
  TypedComponentAdapter<DatePickerProperties>("m3/date-picker", record) {
  val selectedDate =
    property(
      DatePickerProperties::selectedDate,
      AdapterValueCodecs.String,
      TypedDatePickerDefaults.selectedDate,
      bindable = false,
    )
  val mode =
    property(
      DatePickerProperties::mode,
      AdapterValueCodecs.String,
      TypedDatePickerDefaults.mode,
      bindable = false,
    )
  val showModeToggle =
    property(
      DatePickerProperties::showModeToggle,
      AdapterValueCodecs.Boolean,
      TypedDatePickerDefaults.showModeToggle,
      bindable = false,
    )
}

data class DialogProperties(val containerColor: String, val tonalElevationDp: Float)

class DialogAdapter(record: ComponentRecord) :
  TypedComponentAdapter<DialogProperties>("m3/dialog", record) {
  val containerColor =
    property(
      DialogProperties::containerColor,
      AdapterValueCodecs.String,
      TypedDialogDefaults.containerColor,
      bindable = false,
    )
  val tonalElevationDp =
    property(
      DialogProperties::tonalElevationDp,
      M3ValueCodecs.Float,
      TypedDialogDefaults.tonalElevationDp,
      bindable = false,
    )
  val confirmButton = slot("confirmButton")
  val dismissButton = slot("dismissButton")
  val icon = slot("icon")
  val title = slot("title")
  val text = slot("text")
}

data class FilterChipProperties(
  val selected: Boolean,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

class FilterChipAdapter(record: ComponentRecord) :
  TypedComponentAdapter<FilterChipProperties>("m3/filter-chip", record) {
  val selected =
    property(
      FilterChipProperties::selected,
      AdapterValueCodecs.Boolean,
      TypedFilterChipDefaults.selected,
      bindable = true,
    )
  val enabled =
    property(
      FilterChipProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedFilterChipDefaults.enabled,
      bindable = false,
    )
  val onClick = event(FilterChipProperties::onClick)
  val label = slot("label")
  val leadingIcon = slot("leadingIcon")
  val trailingIcon = slot("trailingIcon")
}

data class FloatingActionButtonProperties(val onClick: () -> Unit)

class FloatingActionButtonAdapter(record: ComponentRecord) :
  TypedComponentAdapter<FloatingActionButtonProperties>("m3/floating-action-button", record) {
  val onClick = event(FloatingActionButtonProperties::onClick)
  val content = slot("content")
}

data class HorizontalDividerProperties(val thicknessDp: Float, val color: String)

class HorizontalDividerAdapter(record: ComponentRecord) :
  TypedComponentAdapter<HorizontalDividerProperties>("m3/horizontal-divider", record) {
  val thicknessDp =
    property(
      HorizontalDividerProperties::thicknessDp,
      M3ValueCodecs.Float,
      TypedHorizontalDividerDefaults.thicknessDp,
      bindable = false,
    )
  val color =
    property(
      HorizontalDividerProperties::color,
      AdapterValueCodecs.String,
      TypedHorizontalDividerDefaults.color,
      bindable = false,
    )
}

data class HorizontalFloatingToolbarProperties(val expanded: Boolean, val containerColor: String)

class HorizontalFloatingToolbarAdapter(record: ComponentRecord) :
  TypedComponentAdapter<HorizontalFloatingToolbarProperties>(
    "m3/horizontal-floating-toolbar",
    record,
  ) {
  val expanded =
    property(
      HorizontalFloatingToolbarProperties::expanded,
      AdapterValueCodecs.Boolean,
      TypedHorizontalFloatingToolbarDefaults.expanded,
      bindable = false,
    )
  val containerColor =
    property(
      HorizontalFloatingToolbarProperties::containerColor,
      AdapterValueCodecs.String,
      TypedHorizontalFloatingToolbarDefaults.containerColor,
      bindable = false,
    )
  val content = slot("content")
}

data class IconProperties(
  val iconKey: String,
  val sizeDp: Float,
  val contentDescription: String,
  val color: String,
)

class IconAdapter(record: ComponentRecord) :
  TypedComponentAdapter<IconProperties>("m3/icon", record) {
  val iconKey =
    property(
      IconProperties::iconKey,
      AdapterValueCodecs.String,
      TypedIconDefaults.iconKey,
      bindable = false,
    )
  val sizeDp =
    property(
      IconProperties::sizeDp,
      M3ValueCodecs.Float,
      TypedIconDefaults.sizeDp,
      bindable = false,
    )
  val contentDescription =
    property(
      IconProperties::contentDescription,
      AdapterValueCodecs.String,
      TypedIconDefaults.contentDescription,
      bindable = false,
    )
  val color =
    property(
      IconProperties::color,
      AdapterValueCodecs.String,
      TypedIconDefaults.color,
      bindable = false,
    )
}

data class IconButtonProperties(
  val style: String,
  val sizeDp: Float,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

class IconButtonAdapter(record: ComponentRecord) :
  TypedComponentAdapter<IconButtonProperties>("m3/icon-button", record) {
  val style =
    property(
      IconButtonProperties::style,
      AdapterValueCodecs.String,
      TypedIconButtonDefaults.style,
      bindable = false,
    )
  val sizeDp =
    property(
      IconButtonProperties::sizeDp,
      M3ValueCodecs.Float,
      TypedIconButtonDefaults.sizeDp,
      bindable = false,
    )
  val enabled =
    property(
      IconButtonProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedIconButtonDefaults.enabled,
      bindable = false,
    )
  val onClick = event(IconButtonProperties::onClick)
  val content = slot("content")
}

data class PrimaryTabRowProperties(val selectedIndex: Int)

class PrimaryTabRowAdapter(record: ComponentRecord) :
  TypedComponentAdapter<PrimaryTabRowProperties>("m3/primary-tab-row", record) {
  val selectedIndex =
    property(
      PrimaryTabRowProperties::selectedIndex,
      M3ValueCodecs.Int,
      TypedPrimaryTabRowDefaults.selectedIndex,
      bindable = true,
    )
  val tabs = slot("tabs")
}

data class LinearProgressIndicatorProperties(val progress: Float, val indeterminate: Boolean)

class LinearProgressIndicatorAdapter(record: ComponentRecord) :
  TypedComponentAdapter<LinearProgressIndicatorProperties>("m3/linear-progress-indicator", record) {
  val progress =
    property(
      LinearProgressIndicatorProperties::progress,
      M3ValueCodecs.Float,
      TypedLinearProgressIndicatorDefaults.progress,
      bindable = true,
    )
  val indeterminate =
    property(
      LinearProgressIndicatorProperties::indeterminate,
      AdapterValueCodecs.Boolean,
      TypedLinearProgressIndicatorDefaults.indeterminate,
      bindable = false,
    )
}

data class CircularProgressIndicatorProperties(val progress: Float, val indeterminate: Boolean)

class CircularProgressIndicatorAdapter(record: ComponentRecord) :
  TypedComponentAdapter<CircularProgressIndicatorProperties>(
    "m3/circular-progress-indicator",
    record,
  ) {
  val progress =
    property(
      CircularProgressIndicatorProperties::progress,
      M3ValueCodecs.Float,
      TypedCircularProgressIndicatorDefaults.progress,
      bindable = true,
    )
  val indeterminate =
    property(
      CircularProgressIndicatorProperties::indeterminate,
      AdapterValueCodecs.Boolean,
      TypedCircularProgressIndicatorDefaults.indeterminate,
      bindable = false,
    )
}

data class ProvideTextStyleProperties(val style: String)

class ProvideTextStyleAdapter(record: ComponentRecord) :
  TypedComponentAdapter<ProvideTextStyleProperties>("m3/provide-text-style", record) {
  val style =
    property(
      ProvideTextStyleProperties::style,
      AdapterValueCodecs.String,
      TypedProvideTextStyleDefaults.style,
      bindable = false,
    )
  val content = slot("content")
}

data class RadioButtonProperties(
  val selected: Boolean,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

class RadioButtonAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RadioButtonProperties>("m3/radio-button", record) {
  val selected =
    property(
      RadioButtonProperties::selected,
      AdapterValueCodecs.Boolean,
      TypedRadioButtonDefaults.selected,
      bindable = true,
    )
  val enabled =
    property(
      RadioButtonProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedRadioButtonDefaults.enabled,
      bindable = false,
    )
  val onClick = event(RadioButtonProperties::onClick)
}

data class SearchBarProperties(val expanded: Boolean)

class SearchBarAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SearchBarProperties>("m3/search-bar", record) {
  val expanded =
    property(
      SearchBarProperties::expanded,
      AdapterValueCodecs.Boolean,
      TypedSearchBarDefaults.expanded,
      bindable = false,
    )
  val inputField = slot("inputField")
  val expandedContent = slot("expandedContent")
}

data class SearchInputFieldProperties(
  val value: String,
  val enabled: Boolean,
  val readOnly: Boolean,
  val onValueChange: (String) -> Unit,
  val onSearch: () -> Unit,
)

class SearchInputFieldAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SearchInputFieldProperties>("m3/search-input-field", record) {
  val value =
    property(
      SearchInputFieldProperties::value,
      AdapterValueCodecs.String,
      TypedSearchInputFieldDefaults.value,
      bindable = true,
    )
  val enabled =
    property(
      SearchInputFieldProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedSearchInputFieldDefaults.enabled,
      bindable = false,
    )
  val readOnly =
    property(
      SearchInputFieldProperties::readOnly,
      AdapterValueCodecs.Boolean,
      TypedSearchInputFieldDefaults.readOnly,
      bindable = false,
    )
  val onValueChange = stateChange(SearchInputFieldProperties::onValueChange, value)
  val onSearch = event(SearchInputFieldProperties::onSearch)
  val placeholder = slot("placeholder")
  val leadingIcon = slot("leadingIcon")
  val trailingIcon = slot("trailingIcon")
}

data class SliderProperties(
  val valueFrom: Float,
  val valueTo: Float,
  val value: Float,
  val enabled: Boolean,
  val steps: Int,
  val onValueChange: (Float) -> Unit,
)

class SliderAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SliderProperties>("m3/slider", record) {
  val valueFrom =
    property(
      SliderProperties::valueFrom,
      M3ValueCodecs.Float,
      TypedSliderDefaults.valueFrom,
      bindable = false,
    )
  val valueTo =
    property(
      SliderProperties::valueTo,
      M3ValueCodecs.Float,
      TypedSliderDefaults.valueTo,
      bindable = false,
    )
  val value =
    property(
      SliderProperties::value,
      M3ValueCodecs.Float,
      TypedSliderDefaults.value,
      bindable = true,
    )
  val enabled =
    property(
      SliderProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedSliderDefaults.enabled,
      bindable = false,
    )
  val steps =
    property(
      SliderProperties::steps,
      M3ValueCodecs.Int,
      TypedSliderDefaults.steps,
      bindable = false,
    )
  val onValueChange = stateChange(SliderProperties::onValueChange, value)
}

data class SurfaceProperties(
  val containerColor: String,
  val contentColor: String,
  val tonalElevationDp: Float,
)

class SurfaceAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SurfaceProperties>("m3/surface", record) {
  val containerColor =
    property(
      SurfaceProperties::containerColor,
      AdapterValueCodecs.String,
      TypedSurfaceDefaults.containerColor,
      bindable = false,
    )
  val contentColor =
    property(
      SurfaceProperties::contentColor,
      AdapterValueCodecs.String,
      TypedSurfaceDefaults.contentColor,
      bindable = false,
    )
  val tonalElevationDp =
    property(
      SurfaceProperties::tonalElevationDp,
      M3ValueCodecs.Float,
      TypedSurfaceDefaults.tonalElevationDp,
      bindable = false,
    )
  val content = slot("content")
}

data class SwitchProperties(
  val checked: Boolean,
  val enabled: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

class SwitchAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SwitchProperties>("m3/switch", record) {
  val checked =
    property(
      SwitchProperties::checked,
      AdapterValueCodecs.Boolean,
      TypedSwitchDefaults.checked,
      bindable = true,
    )
  val enabled =
    property(
      SwitchProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedSwitchDefaults.enabled,
      bindable = false,
    )
  val onCheckedChange = stateChange(SwitchProperties::onCheckedChange, checked)
  val thumbContent = slot("thumbContent")
}

data class TabProperties(val selected: Boolean, val enabled: Boolean, val onClick: () -> Unit)

class TabAdapter(record: ComponentRecord) : TypedComponentAdapter<TabProperties>("m3/tab", record) {
  val selected =
    property(
      TabProperties::selected,
      AdapterValueCodecs.Boolean,
      TypedTabDefaults.selected,
      bindable = true,
    )
  val enabled =
    property(
      TabProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedTabDefaults.enabled,
      bindable = false,
    )
  val onClick = event(TabProperties::onClick)
  val text = slot("text")
  val icon = slot("icon")
}

data class TextProperties(
  val text: String,
  val color: String,
  val fontSizeSp: Float,
  val lineHeightSp: Float,
  val letterSpacingSp: Float,
  val minLines: Int,
  val maxLines: Int,
  val softWrap: Boolean,
  val style: String,
  val fontWeight: String,
  val fontStyle: String,
  val overflow: String,
  val textAlign: String,
  val textDecoration: String,
)

class TextAdapter(record: ComponentRecord) :
  TypedComponentAdapter<TextProperties>("m3/text", record) {
  val text =
    property(
      TextProperties::text,
      AdapterValueCodecs.String,
      TypedTextDefaults.text,
      bindable = false,
    )
  val color =
    property(
      TextProperties::color,
      AdapterValueCodecs.String,
      TypedTextDefaults.color,
      bindable = false,
    )
  val fontSizeSp =
    property(
      TextProperties::fontSizeSp,
      M3ValueCodecs.Float,
      TypedTextDefaults.fontSizeSp,
      bindable = false,
    )
  val lineHeightSp =
    property(
      TextProperties::lineHeightSp,
      M3ValueCodecs.Float,
      TypedTextDefaults.lineHeightSp,
      bindable = false,
    )
  val letterSpacingSp =
    property(
      TextProperties::letterSpacingSp,
      M3ValueCodecs.Float,
      TypedTextDefaults.letterSpacingSp,
      bindable = false,
    )
  val minLines =
    property(
      TextProperties::minLines,
      M3ValueCodecs.Int,
      TypedTextDefaults.minLines,
      bindable = false,
    )
  val maxLines =
    property(
      TextProperties::maxLines,
      M3ValueCodecs.Int,
      TypedTextDefaults.maxLines,
      bindable = false,
    )
  val softWrap =
    property(
      TextProperties::softWrap,
      AdapterValueCodecs.Boolean,
      TypedTextDefaults.softWrap,
      bindable = false,
    )
  val style =
    property(
      TextProperties::style,
      AdapterValueCodecs.String,
      TypedTextDefaults.style,
      bindable = false,
    )
  val fontWeight =
    property(
      TextProperties::fontWeight,
      AdapterValueCodecs.String,
      TypedTextDefaults.fontWeight,
      bindable = false,
    )
  val fontStyle =
    property(
      TextProperties::fontStyle,
      AdapterValueCodecs.String,
      TypedTextDefaults.fontStyle,
      bindable = false,
    )
  val overflow =
    property(
      TextProperties::overflow,
      AdapterValueCodecs.String,
      TypedTextDefaults.overflow,
      bindable = false,
    )
  val textAlign =
    property(
      TextProperties::textAlign,
      AdapterValueCodecs.String,
      TypedTextDefaults.textAlign,
      bindable = false,
    )
  val textDecoration =
    property(
      TextProperties::textDecoration,
      AdapterValueCodecs.String,
      TypedTextDefaults.textDecoration,
      bindable = false,
    )
}

data class TextFieldProperties(
  val value: String,
  val enabled: Boolean,
  val readOnly: Boolean,
  val isError: Boolean,
  val singleLine: Boolean,
  val onValueChange: (String) -> Unit,
)

class TextFieldAdapter(record: ComponentRecord) :
  TypedComponentAdapter<TextFieldProperties>("m3/text-field", record) {
  val value =
    property(
      TextFieldProperties::value,
      AdapterValueCodecs.String,
      TypedTextFieldDefaults.value,
      bindable = true,
    )
  val enabled =
    property(
      TextFieldProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedTextFieldDefaults.enabled,
      bindable = false,
    )
  val readOnly =
    property(
      TextFieldProperties::readOnly,
      AdapterValueCodecs.Boolean,
      TypedTextFieldDefaults.readOnly,
      bindable = false,
    )
  val isError =
    property(
      TextFieldProperties::isError,
      AdapterValueCodecs.Boolean,
      TypedTextFieldDefaults.isError,
      bindable = false,
    )
  val singleLine =
    property(
      TextFieldProperties::singleLine,
      AdapterValueCodecs.Boolean,
      TypedTextFieldDefaults.singleLine,
      bindable = false,
    )
  val onValueChange = stateChange(TextFieldProperties::onValueChange, value)
  val label = slot("label")
  val placeholder = slot("placeholder")
  val supportingText = slot("supportingText")
  val leadingIcon = slot("leadingIcon")
  val trailingIcon = slot("trailingIcon")
}

data class OutlinedTextFieldProperties(
  val value: String,
  val enabled: Boolean,
  val readOnly: Boolean,
  val isError: Boolean,
  val singleLine: Boolean,
  val onValueChange: (String) -> Unit,
)

class OutlinedTextFieldAdapter(record: ComponentRecord) :
  TypedComponentAdapter<OutlinedTextFieldProperties>("m3/outlined-text-field", record) {
  val value =
    property(
      OutlinedTextFieldProperties::value,
      AdapterValueCodecs.String,
      TypedOutlinedTextFieldDefaults.value,
      bindable = true,
    )
  val enabled =
    property(
      OutlinedTextFieldProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedOutlinedTextFieldDefaults.enabled,
      bindable = false,
    )
  val readOnly =
    property(
      OutlinedTextFieldProperties::readOnly,
      AdapterValueCodecs.Boolean,
      TypedOutlinedTextFieldDefaults.readOnly,
      bindable = false,
    )
  val isError =
    property(
      OutlinedTextFieldProperties::isError,
      AdapterValueCodecs.Boolean,
      TypedOutlinedTextFieldDefaults.isError,
      bindable = false,
    )
  val singleLine =
    property(
      OutlinedTextFieldProperties::singleLine,
      AdapterValueCodecs.Boolean,
      TypedOutlinedTextFieldDefaults.singleLine,
      bindable = false,
    )
  val onValueChange = stateChange(OutlinedTextFieldProperties::onValueChange, value)
  val label = slot("label")
  val placeholder = slot("placeholder")
  val supportingText = slot("supportingText")
  val leadingIcon = slot("leadingIcon")
  val trailingIcon = slot("trailingIcon")
}

data class TimePickerProperties(
  val hour: Int,
  val minute: Int,
  val is24Hour: Boolean,
  val mode: String,
)

class TimePickerAdapter(record: ComponentRecord) :
  TypedComponentAdapter<TimePickerProperties>("m3/time-picker", record) {
  val hour =
    property(
      TimePickerProperties::hour,
      M3ValueCodecs.Int,
      TypedTimePickerDefaults.hour,
      bindable = false,
    )
  val minute =
    property(
      TimePickerProperties::minute,
      M3ValueCodecs.Int,
      TypedTimePickerDefaults.minute,
      bindable = false,
    )
  val is24Hour =
    property(
      TimePickerProperties::is24Hour,
      AdapterValueCodecs.Boolean,
      TypedTimePickerDefaults.is24Hour,
      bindable = false,
    )
  val mode =
    property(
      TimePickerProperties::mode,
      AdapterValueCodecs.String,
      TypedTimePickerDefaults.mode,
      bindable = false,
    )
}

class StickerProperties

class StickerAdapter(record: ComponentRecord) :
  TypedComponentAdapter<StickerProperties>("m3/sticker", record) {
  val content = slot("content")
}

class MaterialExpressiveThemeProperties

class MaterialExpressiveThemeAdapter(record: ComponentRecord) :
  TypedComponentAdapter<MaterialExpressiveThemeProperties>("m3/material-expressive-theme", record) {
  val content = slot("content")
}

data class NavigationSuiteItemProperties(
  val selected: Boolean,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

class NavigationSuiteItemAdapter(record: ComponentRecord) :
  TypedComponentAdapter<NavigationSuiteItemProperties>("m3/navigation-suite-item", record) {
  val selected =
    property(
      NavigationSuiteItemProperties::selected,
      AdapterValueCodecs.Boolean,
      TypedNavigationSuiteItemDefaults.selected,
      bindable = true,
    )
  val enabled =
    property(
      NavigationSuiteItemProperties::enabled,
      AdapterValueCodecs.Boolean,
      TypedNavigationSuiteItemDefaults.enabled,
      bindable = false,
    )
  val onClick = event(NavigationSuiteItemProperties::onClick)
  val icon = slot("icon")
  val label = slot("label")
}

data class NavigationSuiteScaffoldProperties(val containerColor: String, val contentColor: String)

class NavigationSuiteScaffoldAdapter(record: ComponentRecord) :
  TypedComponentAdapter<NavigationSuiteScaffoldProperties>("m3/navigation-suite-scaffold", record) {
  val containerColor =
    property(
      NavigationSuiteScaffoldProperties::containerColor,
      AdapterValueCodecs.String,
      TypedNavigationSuiteScaffoldDefaults.containerColor,
      bindable = false,
    )
  val contentColor =
    property(
      NavigationSuiteScaffoldProperties::contentColor,
      AdapterValueCodecs.String,
      TypedNavigationSuiteScaffoldDefaults.contentColor,
      bindable = false,
    )
  val navigationItems = slot("navigationItems")
  val primaryAction = slot("primaryAction")
  val content = slot("content")
}

data class PrimaryScrollableTabRowProperties(
  val selectedIndex: Int,
  val containerColor: String,
  val contentColor: String,
  val edgePaddingDp: Float,
  val minTabWidthDp: Float,
)

class PrimaryScrollableTabRowAdapter(record: ComponentRecord) :
  TypedComponentAdapter<PrimaryScrollableTabRowProperties>(
    "m3/primary-scrollable-tab-row",
    record,
  ) {
  val selectedIndex =
    property(
      PrimaryScrollableTabRowProperties::selectedIndex,
      M3ValueCodecs.Int,
      TypedPrimaryScrollableTabRowDefaults.selectedIndex,
      bindable = true,
    )
  val containerColor =
    property(
      PrimaryScrollableTabRowProperties::containerColor,
      AdapterValueCodecs.String,
      TypedPrimaryScrollableTabRowDefaults.containerColor,
      bindable = false,
    )
  val contentColor =
    property(
      PrimaryScrollableTabRowProperties::contentColor,
      AdapterValueCodecs.String,
      TypedPrimaryScrollableTabRowDefaults.contentColor,
      bindable = false,
    )
  val edgePaddingDp =
    property(
      PrimaryScrollableTabRowProperties::edgePaddingDp,
      M3ValueCodecs.Float,
      TypedPrimaryScrollableTabRowDefaults.edgePaddingDp,
      bindable = false,
    )
  val minTabWidthDp =
    property(
      PrimaryScrollableTabRowProperties::minTabWidthDp,
      M3ValueCodecs.Float,
      TypedPrimaryScrollableTabRowDefaults.minTabWidthDp,
      bindable = false,
    )
  val tabs = slot("tabs")
}

data class VariableFontTextProperties(
  val font: String,
  val wght: Float,
  val wdth: Float,
  val slnt: Float,
  val rond: Float,
  val text: String,
  val color: String,
  val fontSizeSp: Float,
)

class VariableFontTextAdapter(record: ComponentRecord) :
  TypedComponentAdapter<VariableFontTextProperties>("m3/variable-font-text", record) {
  val font =
    property(
      VariableFontTextProperties::font,
      AdapterValueCodecs.String,
      TypedVariableFontTextDefaults.font,
      bindable = false,
    )
  val wght =
    property(
      VariableFontTextProperties::wght,
      M3ValueCodecs.Float,
      TypedVariableFontTextDefaults.wght,
      bindable = true,
    )
  val wdth =
    property(
      VariableFontTextProperties::wdth,
      M3ValueCodecs.Float,
      TypedVariableFontTextDefaults.wdth,
      bindable = true,
    )
  val slnt =
    property(
      VariableFontTextProperties::slnt,
      M3ValueCodecs.Float,
      TypedVariableFontTextDefaults.slnt,
      bindable = true,
    )
  val rond =
    property(
      VariableFontTextProperties::rond,
      M3ValueCodecs.Float,
      TypedVariableFontTextDefaults.rond,
      bindable = true,
    )
  val text =
    property(
      VariableFontTextProperties::text,
      AdapterValueCodecs.String,
      TypedVariableFontTextDefaults.text,
      bindable = false,
    )
  val color =
    property(
      VariableFontTextProperties::color,
      AdapterValueCodecs.String,
      TypedVariableFontTextDefaults.color,
      bindable = false,
    )
  val fontSizeSp =
    property(
      VariableFontTextProperties::fontSizeSp,
      M3ValueCodecs.Float,
      TypedVariableFontTextDefaults.fontSizeSp,
      bindable = false,
    )
}

data class SnackbarHostProperties(
  val visible: Boolean,
  val actionLabel: String,
  val message: String,
  val onClick: () -> Unit,
)

class SnackbarHostAdapter(record: ComponentRecord) :
  TypedComponentAdapter<SnackbarHostProperties>("m3/snackbar-host", record) {
  val visible =
    property(
      SnackbarHostProperties::visible,
      AdapterValueCodecs.Boolean,
      TypedSnackbarHostDefaults.visible,
      bindable = false,
    )
  val actionLabel =
    property(
      SnackbarHostProperties::actionLabel,
      AdapterValueCodecs.String,
      TypedSnackbarHostDefaults.actionLabel,
      bindable = false,
    )
  val message =
    property(
      SnackbarHostProperties::message,
      AdapterValueCodecs.String,
      TypedSnackbarHostDefaults.message,
      bindable = false,
    )
  val onClick = event(SnackbarHostProperties::onClick)
  val snackbar = slot("snackbar")
}
