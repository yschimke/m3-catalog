package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.UiBuilderAuthoredComponent
import ee.schimke.composeai.discovery.UiBuilderBuiltin
import ee.schimke.composeai.discovery.UiBuilderBuiltinCode
import ee.schimke.composeai.discovery.UiBuilderBuiltinWasm
import ee.schimke.composeai.discovery.UiBuilderComposeSourceExport
import ee.schimke.composeai.discovery.UiBuilderMenu
import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import ee.schimke.composeai.discovery.UiBuilderTemplateEntry
import ee.schimke.composeai.uibuilder.protocol.JsonValueTypeV1
import ee.schimke.composeai.uibuilder.protocol.PropertyCapabilityV1
import ee.schimke.composeai.uibuilder.protocol.SlotCapabilityV1
import ee.schimke.composeai.uibuilder.protocol.SlotCardinalityV1
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Authored Kotlin: the committed JSON remains the production input during the parallel trial.
// Inventory still comes from @CatalogComponent; these declarations own only builder policy.

fun m3BuilderPolicy(): UiBuilderPolicyFile =
  UiBuilderPolicyFile.Builder("compose-ui-builder-policy/v1", "mobile")
    .apply {
      jsonSchema =
        "https://raw.githubusercontent.com/yschimke/compose-ai-tools/main/scripts/design-artifacts/ui-builder.policy.schema.json"
      componentIdPrefix = "m3/"
      platformLabel = "Mobile"
      composeSourceExport = UiBuilderComposeSourceExport.Builder("compose-material3", 1).build()

      previewSurfaces = policyPreviewSurfaces()
      frame = policyFrame()
      menu =
        UiBuilderMenu.Builder()
          .apply {
            groupOrder =
              listOf(
                "Bottom app bar",
                "Navigation bar",
                "Navigation rail",
                "Search",
                "Tabs",
                "Top app bar",
                "Buttons",
                "FAB",
                "Icon buttons",
                "Icons",
                "Segmented buttons",
                "Split button",
                "Toggle buttons",
                "Toolbars",
                "Checkbox",
                "Chips",
                "Date pickers",
                "Menus",
                "Radio button",
                "Sliders",
                "Switch",
                "Time pickers",
                "Text fields",
                "Bottom sheets",
                "Surfaces",
                "Cards",
                "Carousel",
                "Dialogs",
                "Divider",
                "Lists",
                "Side sheets",
                "Badges",
                "Loading indicator",
                "Progress indicators",
                "Snackbar",
                "Tooltips",
                "Color",
                "Shapes",
                "Typography",
              )
          }
          .build()
      colorTokens = policyColorTokens()
      templates =
        listOf(
          UiBuilderTemplateEntry.Builder("ui-builder/designs/blank.json").build(),
          UiBuilderTemplateEntry.Builder("ui-builder/designs/hello.json").build(),
          UiBuilderTemplateEntry.Builder("ui-builder/designs/adaptive-navigation.json").build(),
          UiBuilderTemplateEntry.Builder("ui-builder/designs/supporting-pane.json").build(),
          UiBuilderTemplateEntry.Builder("ui-builder/designs/list-detail.json").build(),
          UiBuilderTemplateEntry.Builder("ui-builder/designs/adaptive-feed.json").build(),
        )
      components =
        linkedMapOf(
          "m3/button" to componentButton(),
          "m3/card" to componentCard(),
          "m3/center-aligned-top-app-bar" to componentCenterAlignedTopAppBar(),
          "m3/checkbox" to componentCheckbox(),
          "m3/date-picker" to componentDatePicker(),
          "m3/dialog" to componentDialog(),
          "m3/filter-chip" to componentFilterChip(),
          "m3/floating-action-button" to componentFloatingActionButton(),
          "m3/horizontal-divider" to componentHorizontalDivider(),
          "m3/horizontal-floating-toolbar" to componentHorizontalFloatingToolbar(),
          "m3/icon" to componentIcon(),
          "m3/icon-button" to componentIconButton(),
          "m3/list-item" to componentListItem(),
          "m3/primary-tab-row" to componentPrimaryTabRow(),
          "m3/linear-progress-indicator" to componentLinearProgressIndicator(),
          "m3/circular-progress-indicator" to componentCircularProgressIndicator(),
          "m3/provide-text-style" to componentProvideTextStyle(),
          "m3/radio-button" to componentRadioButton(),
          "m3/search-bar" to componentSearchBar(),
          "m3/search-input-field" to componentSearchInputField(),
          "m3/slider" to componentSlider(),
          "m3/surface" to componentSurface(),
          "m3/switch" to componentSwitch(),
          "m3/tab" to componentTab(),
          "m3/text" to componentText(),
          "m3/text-field" to componentTextField(),
          "m3/outlined-text-field" to componentOutlinedTextField(),
          "m3/time-picker" to componentTimePicker(),
          "m3/sticker" to componentSticker(),
          "m3/material-expressive-theme" to componentMaterialExpressiveTheme(),
          "m3/navigation-suite-item" to componentNavigationSuiteItem(),
          "m3/navigation-suite-scaffold" to componentNavigationSuiteScaffold(),
          "m3/primary-scrollable-tab-row" to componentPrimaryScrollableTabRow(),
        )
      builtins =
        linkedMapOf(
          "m3/variable-font-text" to builtinVariableFontText(),
          "m3/snackbar-host" to builtinSnackbarHost(),
        )
      supersedes = policySupersedes()
    }
    .build()

private fun policyPreviewSurfaces(): JsonElement = buildJsonObject {
  put(
    "wasm",
    buildJsonObject {
      put("fidelity", JsonPrimitive("approximate"))
      put(
        "reason",
        JsonPrimitive(
          "The builder's canvas is Compose Multiplatform for Wasm and links its own Material 3, not this catalog's Android artifacts. For the components it has an adapter for the picture is close; where it has none it draws a placeholder. Layout, spacing and typography are near enough to design with and are not what ships."
        ),
      )
    },
  )
  put(
    "native",
    buildJsonObject {
      put("fidelity", JsonPrimitive("authoritative"))
      put("backend", JsonPrimitive("android"))
      put(
        "reason",
        JsonPrimitive(
          "The generated Kotlin compiled against this module's own classpath and rendered under Robolectric — the same lane that produces this catalog's stickers."
        ),
      )
    },
  )
}

private fun policyFrame(): JsonElement = buildJsonObject {
  put("adapter", JsonPrimitive("frame/rect"))
  put("seedDevice", JsonPrimitive("id:pixel_6"))
}

private fun policyColorTokens(): JsonElement = buildJsonObject {
  put(
    "roles",
    JsonArray(
      listOf(
        JsonPrimitive("background"),
        JsonPrimitive("surface"),
        JsonPrimitive("surfaceContainer"),
        JsonPrimitive("surfaceContainerLow"),
        JsonPrimitive("surfaceContainerHigh"),
        JsonPrimitive("surfaceContainerHighest"),
        JsonPrimitive("primary"),
        JsonPrimitive("onPrimary"),
        JsonPrimitive("tertiary"),
        JsonPrimitive("onTertiary"),
        JsonPrimitive("onSurface"),
        JsonPrimitive("onSurfaceVariant"),
        JsonPrimitive("outlineVariant"),
        JsonPrimitive("transparent"),
      )
    ),
  )
}

private fun componentButton(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.ButtonKt.Button"
      displayName = "Button"
      canvas = "material3/Button"
      traits = listOf("Action", "ToolbarItem")
      propertyCapabilities =
        listOf(
          property(
            "style",
            JsonValueTypeV1.STRING,
            required = true,
            allowedValues =
              listOf(
                JsonPrimitive("filled"),
                JsonPrimitive("filledTonal"),
                JsonPrimitive("text"),
                JsonPrimitive("fab"),
              ),
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property(
            "selected",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — which is an object, and why `object` is one of the accepted types.",
          ),
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("onClickAction", JsonValueTypeV1.OBJECT, JsonValueTypeV1.NULL, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "content",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Leaf", "Container"),
            traits = listOf("AnyContent"),
          )
        )
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentCard(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.CardKt.Card"
      displayName = "Card"
      canvas = "material3/Card"
      traits = listOf("GridItem", "CarouselItem", "ListItem", "OverlayContent")
      propertyCapabilities =
        listOf(
          property(
            "style",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("filled"),
                JsonPrimitive("elevated"),
                JsonPrimitive("outlined"),
              ),
            notes =
              "Which Material 3 card: `filled` (Card, the default), `elevated` (ElevatedCard) or `outlined` (OutlinedCard). One builder component with the style as a property, as `m3/button` is; the catalog keeps a sticker per style.",
          ),
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("shape", JsonValueTypeV1.STRING, required = false),
          property("stableKey", JsonValueTypeV1.STRING, required = false),
          property("elevationDp", JsonValueTypeV1.NUMBER, required = false),
          property("onClickAction", JsonValueTypeV1.OBJECT, JsonValueTypeV1.NULL, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "content",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container", "Leaf"),
            traits = listOf("AnyContent"),
          )
        )
      modifierCapabilities = cardModifiers
    }
    .build()

private fun componentCenterAlignedTopAppBar(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.AppBarKt.CenterAlignedTopAppBar"
      displayName = "Top app bar"
      canvas = "material3/CenterAlignedTopAppBar"
      traits = listOf("TopBar", "ScreenContent")
      propertyCapabilities =
        listOf(
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property(
            "scrolledContainerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property(
            "scrollBehavior",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("pinned"),
                JsonPrimitive("enterAlways"),
                JsonPrimitive("exitUntilCollapsed"),
              ),
            notes =
              "Reaches the generated Kotlin as the matching TopAppBarDefaults behavior. The canvas draws the bar pinned whatever this says: a scroll behavior is driven by a nested-scroll connection the document has no scaffold to carry.",
          ),
        )
      slotCapabilities =
        listOf(
          slot(
            "title",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          )
        )
      modifierCapabilities = wideModifiers
    }
    .build()

private fun componentCheckbox(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.CheckboxKt.Checkbox"
      displayName = "Checkbox"
      canvas = "material3/Checkbox"
      traits = listOf("SelectionControl", "Action")
      propertyCapabilities =
        listOf(
          property(
            "checked",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — the same shape a filter chip's `selected` takes.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = selectionModifiers
    }
    .build()

private fun componentDatePicker(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.DatePickerKt.DatePicker"
      displayName = "Date picker"
      canvas = "material3/DatePicker"
      traits = listOf("PickerContent", "DateContent")
      propertyCapabilities =
        listOf(
          property(
            "mode",
            JsonValueTypeV1.STRING,
            required = true,
            allowedValues = listOf(JsonPrimitive("picker"), JsonPrimitive("input")),
            notes = "Material's own DisplayMode: the calendar, or the typed date field.",
          ),
          property(
            "selectedDate",
            JsonValueTypeV1.STRING,
            required = true,
            notes =
              "ISO-8601 `YYYY-MM-DD`. Pinned rather than read from the clock so a render is the same picture on every day.",
          ),
          property("showModeToggle", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentDialog(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.AlertDialog_skikoKt.AlertDialog"
      displayName = "Dialog"
      canvas = "material3/AlertDialog"
      traits = listOf("DialogContent", "OverlayContent")
      propertyCapabilities =
        listOf(
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("tonalElevationDp", JsonValueTypeV1.NUMBER, required = false),
          property("shapeDp", JsonValueTypeV1.NUMBER, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "icon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("IconContent"),
          ),
          slot(
            "title",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "text",
            min = 0,
            max = null,
            ordered = true,
            roles = listOf("Container", "Leaf"),
            traits = listOf("AnyContent"),
          ),
          slot(
            "dismissButton",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container"),
            traits = listOf("Action"),
          ),
          slot(
            "confirmButton",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Container"),
            traits = listOf("Action"),
          ),
        )
      modifierCapabilities =
        listOf(
          "size",
          "fillMaxWidth",
          "padding",
          "alpha",
          "offset",
          "rotate",
          "scale",
          "zIndex",
          "testTag",
          "width",
          "height",
          "widthIn",
          "heightIn",
          "aspectRatio",
          "background",
          "border",
          "shadow",
          "wrapContentSize",
          "align",
          "alignHorizontal",
          "alignVertical",
          "weight",
        )
    }
    .build()

private fun componentFilterChip(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.ChipKt.FilterChip"
      displayName = "Filter chip"
      canvas = "material3/FilterChip"
      traits = listOf("SelectionControl", "ListItem")
      propertyCapabilities =
        listOf(
          property(
            "selected",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — which is an object, and why `object` is one of the accepted types.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property("shape", JsonValueTypeV1.STRING, required = false),
          property("onClickAction", JsonValueTypeV1.OBJECT, JsonValueTypeV1.NULL, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "label",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "leadingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("AnyContent"),
          ),
        )
      modifierCapabilities = compactModifiers
    }
    .build()

private fun componentFloatingActionButton(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.FloatingActionButtonKt.FloatingActionButton"
      group = "FAB"
    }
    .build()

private fun componentHorizontalDivider(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.DividerKt.HorizontalDivider"
      group = "Divider"
      displayName = "Horizontal divider"
      canvas = "material3/HorizontalDivider"
      traits = listOf("DividerContent")
      propertyCapabilities =
        listOf(
          property("thicknessDp", JsonValueTypeV1.NUMBER, required = false),
          property(
            "color",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
        )
      slotCapabilities = listOf()
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentHorizontalFloatingToolbar(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.FloatingToolbarKt.HorizontalFloatingToolbar"
      displayName = "Horizontal floating toolbar"
      canvas = "material3/HorizontalFloatingToolbar"
      traits = listOf("ToolbarContent", "OverlayContent")
      propertyCapabilities =
        listOf(
          property("expanded", JsonValueTypeV1.BOOLEAN, required = false),
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("contentPadding", JsonValueTypeV1.OBJECT, required = false),
          property("alignment", JsonValueTypeV1.STRING, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "content",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container", "Leaf"),
            traits = listOf("ToolbarItem"),
          )
        )
      modifierCapabilities = compactModifiers
    }
    .build()

private fun componentIcon(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.IconKt.Icon"
      displayName = "Icon"
      canvas = "material3/Icon"
      traits = listOf("IconContent")
      propertyCapabilities =
        listOf(
          property(
            "iconKey",
            JsonValueTypeV1.STRING,
            required = true,
            notes =
              "The key that resolves to a vector, and still required: it is what the canvas and the Compose export draw from. iconName and the axes beside it are recorded for the outline registry and become what is drawn once every lane reads it.",
          ),
          property(
            "iconName",
            JsonValueTypeV1.STRING,
            notes =
              "A Material Symbols name, such as search or chevron_right. One name is one icon: the face and the axes below are properties of this node rather than different icons. The host resolves the name against the pinned font and records the outline in the design's assets, so what the canvas draws is what an export and an offline render draw.",
          ),
          property(
            "iconStyle",
            JsonValueTypeV1.STRING,
            allowedValues =
              listOf(JsonPrimitive("outlined"), JsonPrimitive("rounded"), JsonPrimitive("sharp")),
            notes =
              "Which Material Symbols face draws the name. The three are corner treatments of one drawing, not three icons.",
          ),
          property(
            "iconFill",
            JsonValueTypeV1.NUMBER,
            notes =
              "The FILL axis: 0 is the stroked drawing, 1 the solid one. Continuous, so a value between them is a partly filled icon.",
          ),
          property(
            "iconWeight",
            JsonValueTypeV1.NUMBER,
            notes = "The wght axis, 100 to 700. The face's own default is 400.",
          ),
          property(
            "iconGrade",
            JsonValueTypeV1.NUMBER,
            notes =
              "The GRAD axis, -25 to 200. Weight at a fixed footprint: it thickens the stroke without changing how much room the icon takes.",
          ),
          property(
            "iconOpticalSize",
            JsonValueTypeV1.NUMBER,
            notes =
              "The opsz axis, 20 to 48. The size the drawing is tuned for, which is not the size it is drawn at - that is sizeDp.",
          ),
          property(
            "iconAutoMirror",
            JsonValueTypeV1.BOOLEAN,
            notes =
              "Whether the icon flips in a right-to-left layout. A property because the font has no idea: an arrow is one glyph, and nothing in it says which way it points relative to the reader.",
          ),
          property(
            "contentDescription",
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.NULL,
            required = false,
          ),
          property("sizeDp", JsonValueTypeV1.NUMBER, required = false),
          property(
            "color",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
        )
      slotCapabilities = listOf()
      modifierCapabilities = compactModifiers
      group = "Icons"
    }
    .build()

private fun componentIconButton(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.IconButtonKt.IconButton"
      group = "Icon buttons"
      displayName = "Icon button"
      canvas = "material3/IconButton"
      traits = listOf("Action", "OverlayControl", "ToolbarItem")
      propertyCapabilities =
        listOf(
          property(
            "style",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("standard"),
                JsonPrimitive("filled"),
                JsonPrimitive("tonal"),
                JsonPrimitive("outlined"),
              ),
            notes =
              "Which Material 3 icon button: `standard` (IconButton, the default), `filled` (FilledIconButton), `tonal` (FilledTonalIconButton) or `outlined` (OutlinedIconButton). One builder component with the style as a property, as `m3/button` is; the catalog keeps a sticker per style.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property("selected", JsonValueTypeV1.BOOLEAN, required = false),
          property("alignment", JsonValueTypeV1.STRING, required = false),
          property(
            "contentDescription",
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.NULL,
            required = false,
          ),
          property("sizeDp", JsonValueTypeV1.NUMBER, required = false),
          property("onClickAction", JsonValueTypeV1.OBJECT, JsonValueTypeV1.NULL, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "content",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("IconContent"),
          )
        )
      modifierCapabilities = compactModifiers
    }
    .build()

private fun componentListItem(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.ListItemKt.ListItem"
      group = "Lists"
      displayName = "List item"
      canvas = "material3/ListItem"
      traits = listOf("ListItem", "GridItem")
      propertyCapabilities =
        listOf(
          property(
            "startAccentColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A 3dp bar down the leading edge, drawn behind the item. Empty draws none. A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          )
        )
      slotCapabilities =
        listOf(
          slot(
            "headline",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "supporting",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("AnyContent"),
          ),
          slot(
            "trailing",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("AnyContent"),
          ),
        )
      modifierCapabilities = wideModifiers
    }
    .build()

private fun componentPrimaryTabRow(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TabRowKt.PrimaryTabRow"
      displayName = "Tab row"
      canvas = "material3/PrimaryTabRow"
      traits = listOf("TabBar", "OrderedContent")
      propertyCapabilities =
        listOf(
          property(
            "selectedIndex",
            JsonValueTypeV1.INTEGER,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "An index, or a `state` read of a declared `int` variable — which is an object, and why `object` is one of the accepted types. It is the variable the tabs write.",
          )
        )
      slotCapabilities =
        listOf(
          slot(
            "tabs",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container"),
            traits = listOf("TabItem"),
          )
        )
      modifierCapabilities = wideModifiers
    }
    .build()

private fun componentLinearProgressIndicator(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.ProgressIndicatorKt.LinearProgressIndicator"
      displayName = "Linear progress indicator"
      canvas = "material3/LinearProgressIndicator"
      traits = listOf("Feedback", "Decoration")
      propertyCapabilities =
        listOf(
          property(
            "progress",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "0..1, a literal or a `state` read. Absent means the indeterminate indicator — Material's own distinction between a progress you know and one you do not.",
          ),
          property(
            "indeterminate",
            JsonValueTypeV1.BOOLEAN,
            required = false,
            notes =
              "Draws the running animation rather than a value. Pinned to its first frame on the canvas, because a render that animates is a render that cannot be diffed.",
          ),
        )
      slotCapabilities = listOf()
      modifierCapabilities = inputModifiers
    }
    .build()

private fun componentCircularProgressIndicator(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.ProgressIndicatorKt.CircularProgressIndicator"
      displayName = "Circular progress indicator"
      canvas = "material3/CircularProgressIndicator"
      traits = listOf("Feedback", "Decoration")
      propertyCapabilities =
        listOf(
          property(
            "progress",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "0..1, a literal or a `state` read. Absent means the indeterminate indicator — Material's own distinction between a progress you know and one you do not.",
          ),
          property(
            "indeterminate",
            JsonValueTypeV1.BOOLEAN,
            required = false,
            notes =
              "Draws the running animation rather than a value. Pinned to its first frame on the canvas, because a render that animates is a render that cannot be diffed.",
          ),
        )
      slotCapabilities = listOf()
      modifierCapabilities = inputModifiers
    }
    .build()

private fun componentProvideTextStyle(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TextKt.ProvideTextStyle"
      group = "Typography"
    }
    .build()

private fun componentRadioButton(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.RadioButtonKt.RadioButton"
      displayName = "Radio button"
      canvas = "material3/RadioButton"
      traits = listOf("SelectionControl", "Action")
      propertyCapabilities =
        listOf(
          property(
            "selected",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — which is what makes a group of them pick one option between them.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = selectionModifiers
    }
    .build()

private fun componentSearchBar(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.SearchBarKt.SearchBar"
      displayName = "Search bar"
      canvas = "material3/SearchBar"
      traits = listOf("TopBar", "SearchContent")
      propertyCapabilities =
        listOf(
          property("expanded", JsonValueTypeV1.BOOLEAN, required = true),
          property("shapeDp", JsonValueTypeV1.NUMBER, required = false),
          property("tonalElevationDp", JsonValueTypeV1.NUMBER, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "inputField",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Container"),
            traits = listOf("SearchInput"),
          ),
          slot(
            "expandedContent",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container"),
            traits = listOf("AnyContent"),
          ),
        )
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentSearchInputField(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.SearchBarDefaults.InputField"
      displayName = "Search input field"
      canvas = "material3/InputField"
      traits = listOf("SearchInput", "TextInput")
      propertyCapabilities =
        listOf(
          property("value", JsonValueTypeV1.OBJECT, JsonValueTypeV1.STRING, required = true),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property("readOnly", JsonValueTypeV1.BOOLEAN, required = false),
          property(
            "onQueryChangeAction",
            JsonValueTypeV1.OBJECT,
            JsonValueTypeV1.NULL,
            required = false,
          ),
          property(
            "onSearchAction",
            JsonValueTypeV1.OBJECT,
            JsonValueTypeV1.NULL,
            required = false,
          ),
        )
      slotCapabilities =
        listOf(
          slot(
            "placeholder",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "leadingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
          slot(
            "trailingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
        )
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentSlider(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.SliderKt.Slider"
      displayName = "Slider"
      canvas = "material3/Slider"
      traits = listOf("SelectionControl", "FormField")
      propertyCapabilities =
        listOf(
          property(
            "value",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "A literal, or a `state` read of a declared decimal variable — which is what makes dragging the canvas change the design's state rather than the slider's own memory.",
          ),
          property("valueFrom", JsonValueTypeV1.NUMBER, required = false),
          property("valueTo", JsonValueTypeV1.NUMBER, required = false),
          property(
            "steps",
            JsonValueTypeV1.INTEGER,
            required = false,
            notes =
              "Discrete stops between the ends, as Material counts them: 0 is a continuous slider.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = inputModifiers
    }
    .build()

private fun componentSurface(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.SurfaceKt.Surface"
      displayName = "Surface"
      canvas = "material3/Surface"
      traits = listOf("ScreenContent", "PaneContent", "GridItem", "ListItem", "RemoteAuthorable")
      propertyCapabilities =
        listOf(
          property(
            "containerColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit. Painted across the area the surface is measured to, and no further. A root surface with no size modifier wraps its content, so its containerColor is the frame's ground only when the root fills the frame (fillMaxSize); the ground outside it comes from environment.theme. The export reports ROOT_SURFACE_DOES_NOT_FILL_FRAME when a coloured root does not.",
          ),
          property(
            "contentColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("shapeDp", JsonValueTypeV1.NUMBER, required = false),
          property("tonalElevationDp", JsonValueTypeV1.NUMBER, required = false),
          property(
            "themePrimaryColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property(
            "themeBackgroundColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property(
            "themeSurfaceColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property(
            "themeContentColor",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("themeTypeScale", JsonValueTypeV1.NUMBER, required = false),
          property("themeCornerRadiusDp", JsonValueTypeV1.NUMBER, required = false),
          property(
            "themeDisplayTypeface",
            JsonValueTypeV1.STRING,
            notes =
              "The font family for the display roles (displayLarge, displayMedium, displaySmall) of everything this theme host contains. A family name: one of the editor's vendored faces or any Google Fonts family (\"Space Grotesk\", \"Michroma\"); a `google:` prefix is accepted. Generated code sets it as a `GoogleFont` family on the theme's typography. Unset keeps the platform's own face for these roles.",
          ),
          property(
            "themeHeadlineTypeface",
            JsonValueTypeV1.STRING,
            notes =
              "The font family for the headline roles (headlineLarge, headlineMedium, headlineSmall) of everything this theme host contains. A family name: one of the editor's vendored faces or any Google Fonts family (\"Space Grotesk\", \"Michroma\"); a `google:` prefix is accepted. Generated code sets it as a `GoogleFont` family on the theme's typography. Unset keeps the platform's own face for these roles.",
          ),
          property(
            "themeTitleTypeface",
            JsonValueTypeV1.STRING,
            notes =
              "The font family for the title roles (titleLarge, titleMedium, titleSmall) of everything this theme host contains. A family name: one of the editor's vendored faces or any Google Fonts family (\"Space Grotesk\", \"Michroma\"); a `google:` prefix is accepted. Generated code sets it as a `GoogleFont` family on the theme's typography. Unset keeps the platform's own face for these roles.",
          ),
          property(
            "themeBodyTypeface",
            JsonValueTypeV1.STRING,
            notes =
              "The font family for the body roles (bodyLarge, bodyMedium, bodySmall) of everything this theme host contains. A family name: one of the editor's vendored faces or any Google Fonts family (\"Space Grotesk\", \"Michroma\"); a `google:` prefix is accepted. Generated code sets it as a `GoogleFont` family on the theme's typography. Unset keeps the platform's own face for these roles.",
          ),
          property(
            "themeLabelTypeface",
            JsonValueTypeV1.STRING,
            notes =
              "The font family for the label roles (labelLarge, labelMedium, labelSmall) of everything this theme host contains. A family name: one of the editor's vendored faces or any Google Fonts family (\"Space Grotesk\", \"Michroma\"); a `google:` prefix is accepted. Generated code sets it as a `GoogleFont` family on the theme's typography. Unset keeps the platform's own face for these roles.",
          ),
          property(
            "themeTextStyle",
            JsonValueTypeV1.STRING,
            allowedValues =
              listOf(
                JsonPrimitive("displayLarge"),
                JsonPrimitive("displayMedium"),
                JsonPrimitive("displaySmall"),
                JsonPrimitive("headlineLarge"),
                JsonPrimitive("headlineMedium"),
                JsonPrimitive("headlineSmall"),
                JsonPrimitive("titleLarge"),
                JsonPrimitive("titleMedium"),
                JsonPrimitive("titleSmall"),
                JsonPrimitive("bodyLarge"),
                JsonPrimitive("bodyMedium"),
                JsonPrimitive("bodySmall"),
                JsonPrimitive("labelLarge"),
                JsonPrimitive("labelMedium"),
                JsonPrimitive("labelSmall"),
              ),
            notes =
              "The type role a text with no `style` of its own is set in, for everything this theme host contains — and with it that role's size and typeface. Both themes default to `bodyLarge`; choose `labelLarge` for a design whose plain text is mostly labels, or a display role for one that is mostly figures. Generated code provides it as `ProvideTextStyle(MaterialTheme.typography.<role>)` inside the theme.",
          ),
        )
      slotCapabilities =
        listOf(
          slot(
            "content",
            min = 0,
            max = null,
            ordered = true,
            roles = listOf("Scaffold", "Container", "Leaf"),
            traits = listOf("AnyContent"),
          )
        )
      modifierCapabilities =
        listOf(
          "size",
          "fillMaxSize",
          "fillMaxWidth",
          "padding",
          "clip",
          "matchParentSize",
          "alpha",
          "offset",
          "rotate",
          "scale",
          "zIndex",
          "testTag",
          "width",
          "height",
          "widthIn",
          "heightIn",
          "aspectRatio",
          "fillMaxHeight",
          "background",
          "border",
          "shadow",
          "wrapContentSize",
          "align",
          "alignHorizontal",
          "alignVertical",
          "weight",
        )
      group = "Surfaces"
    }
    .build()

private fun componentSwitch(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.SwitchKt.Switch"
      group = "Switch"
      displayName = "Switch"
      canvas = "material3/Switch"
      traits = listOf("SelectionControl", "Action")
      propertyCapabilities =
        listOf(
          property(
            "checked",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — which is an object, and why `object` is one of the accepted types.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = selectionModifiers
    }
    .build()

private fun componentTab(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TabKt.Tab"
      displayName = "Tab"
      canvas = "material3/Tab"
      traits = listOf("TabItem", "SelectionControl")
      propertyCapabilities =
        listOf(
          property(
            "selected",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — the same shapes `m3/filter-chip` accepts, and for the same reason: a tab whose selection cannot be read from state is a tab that cannot be selected.",
          )
        )
      slotCapabilities =
        listOf(
          slot(
            "text",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          )
        )
      modifierCapabilities = itemModifiers
    }
    .build()

private fun componentText(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TextKt.Text"
      group = "Typography"
      displayName = "Text"
      canvas = "material3/Text"
      traits = listOf("TextContent", "RemoteAuthorable")
      propertyCapabilities =
        listOf(
          property("text", JsonValueTypeV1.STRING, required = true),
          property(
            "style",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("displayLarge"),
                JsonPrimitive("displayMedium"),
                JsonPrimitive("displaySmall"),
                JsonPrimitive("headlineLarge"),
                JsonPrimitive("headlineMedium"),
                JsonPrimitive("headlineSmall"),
                JsonPrimitive("titleLarge"),
                JsonPrimitive("titleMedium"),
                JsonPrimitive("titleSmall"),
                JsonPrimitive("bodyLarge"),
                JsonPrimitive("bodyMedium"),
                JsonPrimitive("bodySmall"),
                JsonPrimitive("labelLarge"),
                JsonPrimitive("labelMedium"),
                JsonPrimitive("labelSmall"),
              ),
          ),
          property(
            "fontWeight",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("normal"),
                JsonPrimitive("medium"),
                JsonPrimitive("semiBold"),
                JsonPrimitive("bold"),
              ),
          ),
          property(
            "fontStyle",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues = listOf(JsonPrimitive("normal"), JsonPrimitive("italic")),
          ),
          property(
            "color",
            JsonValueTypeV1.STRING,
            required = false,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
          property("fontSizeSp", JsonValueTypeV1.NUMBER, required = false),
          property("lineHeightSp", JsonValueTypeV1.NUMBER, required = false),
          property("letterSpacingSp", JsonValueTypeV1.NUMBER, required = false),
          property("minLines", JsonValueTypeV1.INTEGER, required = false),
          property("maxLines", JsonValueTypeV1.INTEGER, required = false),
          property("softWrap", JsonValueTypeV1.BOOLEAN, required = false),
          property(
            "overflow",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(JsonPrimitive("clip"), JsonPrimitive("ellipsis"), JsonPrimitive("visible")),
          ),
          property(
            "textAlign",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("start"),
                JsonPrimitive("center"),
                JsonPrimitive("end"),
                JsonPrimitive("justify"),
              ),
          ),
          property(
            "textDecoration",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("none"),
                JsonPrimitive("underline"),
                JsonPrimitive("lineThrough"),
              ),
          ),
          property(
            "alignment",
            JsonValueTypeV1.STRING,
            required = false,
            allowedValues =
              listOf(
                JsonPrimitive("topStart"),
                JsonPrimitive("topCenter"),
                JsonPrimitive("topEnd"),
                JsonPrimitive("centerStart"),
                JsonPrimitive("center"),
                JsonPrimitive("centerEnd"),
                JsonPrimitive("bottomStart"),
                JsonPrimitive("bottomCenter"),
                JsonPrimitive("bottomEnd"),
              ),
          ),
          property("weight", JsonValueTypeV1.NUMBER, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentTextField(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TextFieldKt.TextField"
      displayName = "Text field"
      canvas = "material3/TextField"
      traits = listOf("TextInput", "FormField")
      propertyCapabilities =
        listOf(
          property(
            "value",
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A literal, or a `state` read of a declared variable — the same shape a search input's `value` takes, and what makes typing in the canvas change the design's state rather than a field's own memory.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property("readOnly", JsonValueTypeV1.BOOLEAN, required = false),
          property("singleLine", JsonValueTypeV1.BOOLEAN, required = false),
          property("isError", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "label",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "placeholder",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "supportingText",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "leadingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
          slot(
            "trailingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
        )
      modifierCapabilities = inputModifiers
    }
    .build()

private fun componentOutlinedTextField(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.OutlinedTextFieldKt.OutlinedTextField"
      displayName = "Outlined text field"
      canvas = "material3/OutlinedTextField"
      traits = listOf("TextInput", "FormField")
      propertyCapabilities =
        listOf(
          property(
            "value",
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = false,
            notes =
              "A literal, or a `state` read of a declared variable — the same shape a search input's `value` takes, and what makes typing in the canvas change the design's state rather than a field's own memory.",
          ),
          property("enabled", JsonValueTypeV1.BOOLEAN, required = false),
          property("readOnly", JsonValueTypeV1.BOOLEAN, required = false),
          property("singleLine", JsonValueTypeV1.BOOLEAN, required = false),
          property("isError", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities =
        listOf(
          slot(
            "label",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "placeholder",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "supportingText",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
          slot(
            "leadingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
          slot(
            "trailingIcon",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf("IconContent", "Action"),
          ),
        )
      modifierCapabilities = inputModifiers
    }
    .build()

private fun componentTimePicker(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TimePickerKt.TimePicker"
      displayName = "Time picker"
      canvas = "material3/TimePicker"
      traits = listOf("PickerContent", "TimeContent")
      propertyCapabilities =
        listOf(
          property(
            "mode",
            JsonValueTypeV1.STRING,
            required = true,
            allowedValues = listOf(JsonPrimitive("dial"), JsonPrimitive("input")),
            notes =
              "The clock face, or the two typed fields Material calls TimeInput — two composables over one state, spelled as one component the way `m3/progress-indicator` spells linear and circular.",
          ),
          property(
            "hour",
            JsonValueTypeV1.INTEGER,
            required = true,
            notes =
              "0..23 whatever `is24Hour` says: the state holds a 24-hour clock and renders it in the chosen one.",
          ),
          property("minute", JsonValueTypeV1.INTEGER, required = true),
          property("is24Hour", JsonValueTypeV1.BOOLEAN, required = false),
        )
      slotCapabilities = listOf()
      modifierCapabilities = standardModifiers
    }
    .build()

private fun componentSticker(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/ee.schimke.m3catalog.CatalogThemeKt.Sticker"
      excluded =
        "the catalog's own preview frame, not a Material component: every sticker is drawn inside one, the Design Kit publishes no node for it, and nobody places one in a design."
    }
    .build()

private fun componentMaterialExpressiveTheme(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.MaterialThemeKt.MaterialExpressiveTheme"
      excluded =
        "the theme scope every preview is wrapped in, not a component a design places. A design's theme is the document's, not a node's."
    }
    .build()

private fun componentNavigationSuiteItem(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record =
        "catalog/androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldKt.NavigationSuiteItem"
      displayName = "Navigation item"
      group = "Navigation bar"
      canvas = "material3/NavigationSuiteItem"
      traits = listOf("NavigationItem", "SelectionControl")
      propertyCapabilities =
        listOf(
          property(
            "selected",
            JsonValueTypeV1.BOOLEAN,
            JsonValueTypeV1.STRING,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "A flag, or a `stateEquals` read of a declared state variable — the shapes `m3/tab` accepts, for the same reason.",
          )
        )
      slotCapabilities =
        listOf(
          slot(
            "icon",
            min = 1,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf(),
          ),
          slot(
            "label",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Leaf"),
            traits = listOf("TextContent"),
          ),
        )
      modifierCapabilities = itemModifiers
    }
    .build()

private fun componentNavigationSuiteScaffold(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record =
        "catalog/androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldKt.NavigationSuiteScaffold"
      displayName = "Navigation suite"
      group = "Navigation bar"
      canvas = "material3/NavigationSuiteScaffold"
      shelfRole = "Scaffold"
      traits = listOf("ScreenContent", "AdaptiveNavigation")
      slotCapabilities =
        listOf(
          slot(
            "navigationItems",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container"),
            traits = listOf("NavigationItem"),
          ),
          slot(
            "primaryAction",
            min = 0,
            max = 1,
            ordered = false,
            roles = listOf("Container", "Leaf"),
            traits = listOf(),
          ),
          slot(
            "content",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container", "Scaffold", "Leaf"),
            traits = listOf(),
          ),
        )
      modifierCapabilities =
        listOf(
          "size",
          "fillMaxSize",
          "padding",
          "alpha",
          "offset",
          "rotate",
          "scale",
          "zIndex",
          "testTag",
          "width",
          "height",
          "widthIn",
          "heightIn",
          "aspectRatio",
          "fillMaxHeight",
          "background",
          "border",
          "shadow",
          "wrapContentSize",
          "align",
          "alignHorizontal",
          "alignVertical",
          "weight",
        )
    }
    .build()

private fun componentPrimaryScrollableTabRow(): UiBuilderAuthoredComponent =
  UiBuilderAuthoredComponent.Builder()
    .apply {
      record = "catalog/androidx.compose.material3.TabRowKt.PrimaryScrollableTabRow"
      displayName = "Scrollable tab row"
      canvas = "material3/PrimaryScrollableTabRow"
      traits = listOf("TabBar", "OrderedContent")
      propertyCapabilities =
        listOf(
          property(
            "selectedIndex",
            JsonValueTypeV1.INTEGER,
            JsonValueTypeV1.OBJECT,
            required = true,
            notes =
              "An index, or a `state` read of a declared `int` variable — the shapes `m3/primary-tab-row` accepts. It is the variable the tabs write.",
          )
        )
      slotCapabilities =
        listOf(
          slot(
            "tabs",
            min = 1,
            max = null,
            ordered = true,
            roles = listOf("Container"),
            traits = listOf("TabItem"),
          )
        )
      modifierCapabilities = wideModifiers
    }
    .build()

private fun builtinVariableFontText(): UiBuilderBuiltin =
  UiBuilderBuiltin.Builder("list-item")
    .apply {
      shelfRole = "Leaf"
      displayName = "Variable font text"
      group = "Typography"
      canvas = "m3/variable-font-text"
      traits = listOf("TextContent")
      properties =
        listOf(
          property(
            "text",
            JsonValueTypeV1.STRING,
            required = true,
            notes =
              "A literal: the outline is worked out from the font when the design exports, so the text cannot change at run time.",
          ),
          property(
            "font",
            JsonValueTypeV1.STRING,
            allowedValues = listOf(JsonPrimitive("robotoFlex"), JsonPrimitive("googleSansFlex")),
            notes = "The variable font the text is drawn from. Absent is Roboto Flex.",
          ),
          property(
            "wght",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            notes =
              "The `wght` axis: weight, 100–1000 in Roboto Flex and 1–1000 in Google Sans Flex. A literal holds it; a `state` read of a decimal variable animates it. Unset is the font's own 400.",
          ),
          property(
            "wdth",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            notes =
              "The `wdth` axis: width, 25–151 (100 is normal). A literal or a `state` read of a decimal variable. Unset is 100.",
          ),
          property(
            "slnt",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            notes =
              "The `slnt` axis: slant, -10–0 degrees. A literal or a `state` read of a decimal variable. Unset is upright.",
          ),
          property(
            "rond",
            JsonValueTypeV1.NUMBER,
            JsonValueTypeV1.OBJECT,
            notes =
              "The `ROND` axis: roundness, 0–100, which only Google Sans Flex has. A literal or a `state` read of a decimal variable. Unset is 0.",
          ),
          property("fontSizeSp", JsonValueTypeV1.NUMBER, notes = "The text's size. Absent is 32."),
          property(
            "color",
            JsonValueTypeV1.STRING,
            notes =
              "A colour, written as {\"type\":\"color\",\"value\":\"#RRGGBB\"} or as {\"type\":\"colorToken\",\"value\":\"primary\"} naming one of statusSemantics.colorTokens. A string wrapper, or a role the canvas does not draw, is refused at commit.",
          ),
        )
      code =
        UiBuilderBuiltinCode.Builder("VariableFontText")
          .apply { imports = listOf("ee.schimke.flexpress.compose.VariableFontText") }
          .build()
      wasm =
        UiBuilderBuiltinWasm.Builder()
          .apply {
            notes =
              "flexpress's VariableFontText draws the text from the font's outlines, its axes free to move. flexpress runs on the JVM only, so the canvas draws a stand-in: Text in the vendored variable font, instanced at the same axes."
          }
          .build()
    }
    .build()

private fun builtinSnackbarHost(): UiBuilderBuiltin =
  UiBuilderBuiltin.Builder("overlay")
    .apply {
      shelfRole = "Container"
      displayName = "Snackbar host"
      group = "Snackbar"
      canvas = "m3/snackbar-host"
      traits = listOf("SnackbarHost")
      properties =
        listOf(
          property(
            "visible",
            JsonValueTypeV1.BOOLEAN,
            required = true,
            notes =
              "Whether the host shows its snackbar. The scaffold's `snackbarHost` slot keeps the host's identity while it is hidden.",
          ),
          property("message", JsonValueTypeV1.STRING, required = false),
          property("actionLabel", JsonValueTypeV1.STRING, JsonValueTypeV1.NULL, required = false),
        )
      slots =
        mapOf(
          "snackbar" to
            buildJsonObject {
              put(
                "acceptedRoles",
                JsonArray(listOf(JsonPrimitive("Container"), JsonPrimitive("Leaf"))),
              )
              put("acceptedTraits", JsonArray(listOf(JsonPrimitive("AnyContent"))))
              put("ordered", JsonPrimitive(false))
              put("max", JsonPrimitive(1))
            }
        )
      modifierCapabilities = standardModifiers
      code =
        UiBuilderBuiltinCode.Builder("SnackbarHost")
          .apply {
            imports =
              listOf(
                "androidx.compose.material3.SnackbarHost",
                "androidx.compose.material3.rememberSnackbarHostState",
              )
          }
          .build()
      wasm =
        UiBuilderBuiltinWasm.Builder()
          .apply {
            notes =
              "No SnackbarHost sticker exists to record: m3-catalog's Snackbar.kt treats the host as a dispatcher and composes snackbars directly. Published so a design holding the scaffold's `snackbarHost` slot keeps validating once the catalog is owned."
          }
          .build()
    }
    .build()

private fun policySupersedes(): JsonElement = buildJsonObject {
  put(
    "m3/card",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/card"))
      put("properties", buildJsonObject { put("variant", JsonPrimitive("style")) })
    },
  )
  put(
    "m3/elevated-card",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/card"))
      put("setProperties", buildJsonObject { put("style", JsonPrimitive("elevated")) })
    },
  )
  put(
    "m3/outlined-card",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/card"))
      put("setProperties", buildJsonObject { put("style", JsonPrimitive("outlined")) })
    },
  )
  put(
    "m3/icon-button",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/icon-button"))
      put("properties", buildJsonObject { put("variant", JsonPrimitive("style")) })
    },
  )
  put(
    "m3/filled-icon-button",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/icon-button"))
      put("setProperties", buildJsonObject { put("style", JsonPrimitive("filled")) })
    },
  )
  put(
    "m3/filled-tonal-icon-button",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/icon-button"))
      put("setProperties", buildJsonObject { put("style", JsonPrimitive("tonal")) })
    },
  )
  put(
    "m3/outlined-icon-button",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/icon-button"))
      put("setProperties", buildJsonObject { put("style", JsonPrimitive("outlined")) })
    },
  )
  put(
    "m3/progress-indicator",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/linear-progress-indicator"))
      put(
        "variants",
        buildJsonObject {
          put("property", JsonPrimitive("variant"))
          put(
            "components",
            buildJsonObject {
              put("linear", JsonPrimitive("m3/linear-progress-indicator"))
              put("circular", JsonPrimitive("m3/circular-progress-indicator"))
            },
          )
        },
      )
    },
  )
  put(
    "m3/text-field",
    buildJsonObject {
      put("componentId", JsonPrimitive("m3/text-field"))
      put(
        "variants",
        buildJsonObject {
          put("property", JsonPrimitive("variant"))
          put(
            "components",
            buildJsonObject {
              put("filled", JsonPrimitive("m3/text-field"))
              put("outlined", JsonPrimitive("m3/outlined-text-field"))
            },
          )
        },
      )
    },
  )
}

private fun property(
  name: String,
  vararg types: JsonValueTypeV1,
  required: Boolean = false,
  allowedValues: List<JsonElement> = emptyList(),
  notes: String? = null,
): JsonElement =
  Json.encodeToJsonElement(
    PropertyCapabilityV1.serializer(),
    PropertyCapabilityV1.Builder(
        name,
        if (types.size == 1) JsonPrimitive(types.single().name.lowercase())
        else JsonArray(types.map { JsonPrimitive(it.name.lowercase()) }),
      )
      .apply {
        this.required = required
        this.allowedValues = allowedValues
        this.notes = notes
      }
      .build(),
  )

private fun slot(
  name: String,
  min: Int,
  max: Int?,
  ordered: Boolean,
  roles: List<String>,
  traits: List<String>,
): JsonElement =
  Json.encodeToJsonElement(
    SlotCapabilityV1.serializer(),
    SlotCapabilityV1.Builder(
        name,
        SlotCardinalityV1.Builder()
          .apply {
            this.min = min
            this.max = max
          }
          .build(),
        ordered,
      )
      .apply {
        acceptedRoles = roles
        acceptedTraits = traits
      }
      .build(),
  )

private val standardModifiers =
  listOf(
    "size",
    "fillMaxWidth",
    "padding",
    "alpha",
    "offset",
    "rotate",
    "scale",
    "zIndex",
    "testTag",
    "width",
    "height",
    "widthIn",
    "heightIn",
    "aspectRatio",
    "align",
    "alignHorizontal",
    "alignVertical",
    "weight",
  )

private val cardModifiers =
  listOf(
    "size",
    "fillMaxWidth",
    "padding",
    "clip",
    "alpha",
    "offset",
    "rotate",
    "scale",
    "zIndex",
    "testTag",
    "width",
    "height",
    "widthIn",
    "heightIn",
    "aspectRatio",
    "background",
    "border",
    "shadow",
    "wrapContentSize",
    "align",
    "alignHorizontal",
    "alignVertical",
    "weight",
  )

private val wideModifiers =
  listOf(
    "fillMaxWidth",
    "padding",
    "alpha",
    "offset",
    "rotate",
    "scale",
    "zIndex",
    "testTag",
    "width",
    "height",
    "widthIn",
    "heightIn",
    "aspectRatio",
    "align",
    "alignHorizontal",
    "alignVertical",
    "weight",
  )

private val selectionModifiers =
  listOf("size", "padding", "align", "alignHorizontal", "alignVertical", "weight")

private val compactModifiers =
  listOf(
    "size",
    "padding",
    "alpha",
    "offset",
    "rotate",
    "scale",
    "zIndex",
    "testTag",
    "width",
    "height",
    "widthIn",
    "heightIn",
    "aspectRatio",
    "align",
    "alignHorizontal",
    "alignVertical",
    "weight",
  )

private val inputModifiers =
  listOf("size", "fillMaxWidth", "padding", "align", "alignHorizontal", "alignVertical", "weight")

private val itemModifiers =
  listOf(
    "padding",
    "alpha",
    "offset",
    "rotate",
    "scale",
    "zIndex",
    "testTag",
    "width",
    "height",
    "widthIn",
    "heightIn",
    "aspectRatio",
    "align",
    "alignHorizontal",
    "alignVertical",
    "weight",
  )
