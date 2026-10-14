package ee.schimke.m3catalog.typed.real

import ee.schimke.composeai.discovery.AdapterValueCodec
import ee.schimke.composeai.discovery.AdapterValueCodecs
import ee.schimke.composeai.discovery.ComponentRecord
import ee.schimke.composeai.discovery.ExportProjection
import ee.schimke.composeai.discovery.FactorySignature
import ee.schimke.composeai.discovery.TypedComponentAdapter
import ee.schimke.composeai.discovery.WireColor
import ee.schimke.composeai.discovery.named
import kotlinx.serialization.json.JsonPrimitive

// The typed Material 3 definitions on the real Compose Material 3 composables (`RealM3Records`),
// each with the `ExportProjection` the builder lowers a design through. They cover the components
// the committed designs use and mirror what the builder's hand-written `compose-material3` export
// writes for them; docs/design/TYPED_M3_REAL_RECORDS.md lists what is still refused.

private const val MATERIAL3 = "androidx.compose.material3"
private const val MATERIAL_THEME = "$MATERIAL3.MaterialTheme"

/**
 * The colour roles the catalog publishes (`colorTokens`), each `MaterialTheme.colorScheme.<role>`.
 */
val M3_COLOR: ExportProjection.Codec.Color = ExportProjection.Codec.Color(MATERIAL_THEME)

/** Material 3's type scale, the roles `MaterialTheme.typography` has and `m3/text` offers. */
val M3_TYPOGRAPHY: Set<String> =
  setOf(
    "displayLarge",
    "displayMedium",
    "displaySmall",
    "headlineLarge",
    "headlineMedium",
    "headlineSmall",
    "titleLarge",
    "titleMedium",
    "titleSmall",
    "bodyLarge",
    "bodyMedium",
    "bodySmall",
    "labelLarge",
    "labelMedium",
    "labelSmall",
  )

/**
 * A string the builder stores, empty for unset, exported through [export]. The catalog's saved wire
 * values stay exactly as they are; only the export is typed.
 */
private fun exported(
  export: ExportProjection.Codec,
  allowed: Set<String>? = null,
): AdapterValueCodec<String> =
  object : AdapterValueCodec<String> by AdapterValueCodecs.String {
    override val stateType: String? = null
    override val exportCodec = export
    override val allowedValues = allowed
  }

private fun wireEnum(typeFqn: String, members: Map<String, String>) =
  exported(ExportProjection.Codec.Enum(typeFqn, members), members.keys)

private fun members(typeFqn: String, vararg wire: String) = wire.associateWith {
  "$typeFqn.${it.replaceFirstChar(Char::uppercaseChar)}"
}

private val OVERFLOW =
  "androidx.compose.ui.text.style.TextOverflow"
    .let { wireEnum(it, members(it, "clip", "ellipsis", "visible")) }

private val TEXT_ALIGN =
  "androidx.compose.ui.text.style.TextAlign"
    .let { wireEnum(it, members(it, "start", "center", "end", "justify")) }

private val FONT_WEIGHT =
  "androidx.compose.ui.text.font.FontWeight"
    .let { wireEnum(it, members(it, "normal", "medium", "semiBold", "bold")) }

private val FONT_STYLE =
  "androidx.compose.ui.text.font.FontStyle".let { wireEnum(it, members(it, "normal", "italic")) }

private val TEXT_DECORATION =
  "androidx.compose.ui.text.style.TextDecoration"
    .let { wireEnum(it, members(it, "none", "underline", "lineThrough")) }

private val STYLE =
  exported(
    ExportProjection.Codec.Token(
      MATERIAL_THEME,
      listOf("typography"),
      "androidx.compose.ui.text.TextStyle",
      M3_TYPOGRAPHY,
    ),
    M3_TYPOGRAPHY,
  )

private val COLOR = AdapterValueCodecs.color(M3_COLOR)

/** `m3/text`'s unset letter spacing, as the wrapper (`TypedTextDefaults`) spells it. */
const val LETTER_SPACING_UNSET: Float = Float.MIN_VALUE

/** A property kept on the shelf for the canvas, which no parameter of any target can carry. */
private val REFUSED = AdapterValueCodecs.String

data class RealTextProperties(
  val text: String,
  val style: String,
  val fontWeight: String,
  val fontStyle: String,
  val color: WireColor,
  val fontSizeSp: Float,
  val lineHeightSp: Float,
  val letterSpacingSp: Float,
  val minLines: Int,
  val maxLines: Int,
  val softWrap: Boolean,
  val overflow: String,
  val textAlign: String,
  val textDecoration: String,
  val alignment: String,
)

/** `m3/text` as `material3.Text(text: String, …)`, every property the shelf offers. */
class RealTextAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RealTextProperties>("m3/text", record) {
  val text = property(RealTextProperties::text, AdapterValueCodecs.String, "", bindable = false)
  val style = wireProperty(RealTextProperties::style, STYLE, "")
  val fontWeight = wireProperty(RealTextProperties::fontWeight, FONT_WEIGHT, "")
  val fontStyle = wireProperty(RealTextProperties::fontStyle, FONT_STYLE, "")
  val color = wireProperty(RealTextProperties::color, COLOR, WireColor.Unset)
  // Positive: the wrapper's 0 is "the style's own", never a 0.sp size or line height.
  val fontSizeSp =
    wireProperty(RealTextProperties::fontSizeSp, AdapterValueCodecs.unit("sp", positive = true), 0f)
  val lineHeightSp =
    wireProperty(
      RealTextProperties::lineHeightSp,
      AdapterValueCodecs.unit("sp", positive = true),
      0f,
    )
  // The wrapper's unset is `Float.MIN_VALUE` (the style's own spacing); an authored 0 is a real
  // 0.sp, drawn and exported as such.
  val letterSpacingSp =
    wireProperty(
      RealTextProperties::letterSpacingSp,
      AdapterValueCodecs.unit("sp"),
      LETTER_SPACING_UNSET,
    )
  val minLines = property(RealTextProperties::minLines, AdapterValueCodecs.Int, 1, bindable = false)
  val maxLines =
    property(RealTextProperties::maxLines, AdapterValueCodecs.Int, Int.MAX_VALUE, bindable = false)
  val softWrap =
    property(RealTextProperties::softWrap, AdapterValueCodecs.Boolean, true, bindable = false)
  val overflow = wireProperty(RealTextProperties::overflow, OVERFLOW, "")
  val textAlign = wireProperty(RealTextProperties::textAlign, TEXT_ALIGN, "")
  val textDecoration = wireProperty(RealTextProperties::textDecoration, TEXT_DECORATION, "")
  // `Modifier.align(…)` is a member of the parent box's `BoxScope`, which a projection's top-level
  // modifier link cannot name; the hand exporter writes it through the slot's scope.
  val alignment = wireProperty(RealTextProperties::alignment, REFUSED, "")

  init {
    exportAs(
      target(
        record,
        mapOf(
          "text" to from(text),
          "style" to from(style),
          "color" to from(color),
          "fontWeight" to from(fontWeight),
          "fontStyle" to from(fontStyle),
          "fontSize" to from(fontSizeSp),
          "lineHeight" to from(lineHeightSp),
          "letterSpacing" to from(letterSpacingSp, omitWhen = JsonPrimitive(LETTER_SPACING_UNSET)),
          "textDecoration" to from(textDecoration, omitWhen = JsonPrimitive("none")),
          "textAlign" to from(textAlign),
          "minLines" to from(minLines, omitWhen = JsonPrimitive(1)),
          "maxLines" to from(maxLines, omitWhen = JsonPrimitive(Int.MAX_VALUE)),
          "softWrap" to from(softWrap, omitWhen = JsonPrimitive(true)),
          "overflow" to from(overflow),
        ),
      )
    )
    exportRefuses(alignment)
  }
}

data class RealIconProperties(
  val iconKey: String,
  val sizeDp: Float,
  val contentDescription: String,
  val color: WireColor,
  val iconName: String,
  val iconStyle: String,
  val iconFill: String,
  val iconWeight: String,
  val iconGrade: String,
  val iconOpticalSize: String,
  val iconAutoMirror: String,
)

/**
 * `m3/icon` as `material3.Icon(imageVector, contentDescription, tint)`: the key is written from the
 * host's Material icon table. A set `sizeDp` becomes `Modifier.size(…)`, as the hand exporter
 * writes it; unset, the icon keeps the library's 24dp. The Material Symbols properties draw an
 * outline the host records in the design's assets, which no `Icon` parameter carries.
 */
class RealIconAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RealIconProperties>("m3/icon", record) {
  val iconKey = wireProperty(RealIconProperties::iconKey, AdapterValueCodecs.materialIcon(), "")
  val sizeDp =
    wireProperty(RealIconProperties::sizeDp, AdapterValueCodecs.unit("dp", positive = true), 0f)
  val contentDescription =
    wireProperty(RealIconProperties::contentDescription, AdapterValueCodecs.String, "")
  val color = wireProperty(RealIconProperties::color, COLOR, WireColor.Unset)
  val iconName = wireProperty(RealIconProperties::iconName, REFUSED, "")
  val iconStyle = wireProperty(RealIconProperties::iconStyle, REFUSED, "")
  val iconFill = wireProperty(RealIconProperties::iconFill, REFUSED, "")
  val iconWeight = wireProperty(RealIconProperties::iconWeight, REFUSED, "")
  val iconGrade = wireProperty(RealIconProperties::iconGrade, REFUSED, "")
  val iconOpticalSize = wireProperty(RealIconProperties::iconOpticalSize, REFUSED, "")
  val iconAutoMirror = wireProperty(RealIconProperties::iconAutoMirror, REFUSED, "")

  init {
    exportAs(
      target(
        record,
        mapOf(
          "imageVector" to from(iconKey),
          "contentDescription" to from(contentDescription),
          "tint" to from(color),
        ),
        modifier =
          listOf(
            ExportProjection.ModifierLink(
              "androidx.compose.foundation.layout.size",
              listOf(from(sizeDp)),
            )
          ),
      )
    )
    exportRefuses(
      iconName,
      iconStyle,
      iconFill,
      iconWeight,
      iconGrade,
      iconOpticalSize,
      iconAutoMirror,
    )
  }
}

data class RealCardProperties(
  val style: String,
  val containerColor: WireColor,
  val elevationDp: Float,
  val shape: String,
  val stableKey: String,
)

/**
 * `m3/card` as Material 3's three cards, picked by `style` as the hand exporter picks them:
 * `filled` (the default) is `Card`, `elevated` is `ElevatedCard` and `outlined` is `OutlinedCard`,
 * each with its own `CardDefaults` colours and elevation. The children are the card's `ColumnScope`
 * content. An unset elevation is the wrapper's -1, which keeps each card's own resting elevation; 0
 * is a real, flat one.
 */
class RealCardAdapter(
  filled: ComponentRecord,
  private val elevated: ComponentRecord,
  private val outlined: ComponentRecord,
  factories: List<FactorySignature>,
) : TypedComponentAdapter<RealCardProperties>("m3/card", filled) {
  val style =
    wireProperty(
      RealCardProperties::style,
      exported(ExportProjection.Codec.Text, setOf("filled", "elevated", "outlined")),
      "",
    )
  val containerColor = wireProperty(RealCardProperties::containerColor, COLOR, WireColor.Unset)
  val elevationDp =
    wireProperty(RealCardProperties::elevationDp, AdapterValueCodecs.unit("dp"), -1f)
  // A shape token, constant or corner radius, which the hand exporter resolves through three
  // tables a single codec cannot state yet.
  val shape = wireProperty(RealCardProperties::shape, REFUSED, "")
  // The lazy item key the canvas uses; no card parameter carries it.
  val stableKey = wireProperty(RealCardProperties::stableKey, REFUSED, "")
  val content = slot("content", required = true)

  private fun card(
    record: ComponentRecord,
    defaults: String,
    variants: Set<String>?,
    factories: List<FactorySignature>,
  ) =
    target(
      record,
      mapOf(
        "colors" to
          call(
            factories.named("$MATERIAL3.CardDefaults.${defaults}Colors"),
            "containerColor" to from(containerColor),
          ),
        "elevation" to
          call(
            factories.named("$MATERIAL3.CardDefaults.${defaults}Elevation"),
            "defaultElevation" to from(elevationDp, omitWhen = JsonPrimitive(-1)),
          ),
      ),
      variants = variants,
    )

  init {
    exportAs(
      card(filled, "card", null, factories),
      card(filled, "card", setOf("filled"), factories),
      card(elevated, "elevatedCard", setOf("elevated"), factories),
      card(outlined, "outlinedCard", setOf("outlined"), factories),
      variant = style,
    )
    exportRefuses(shape, stableKey)
  }
}

data class RealNavigationSuiteItemProperties(
  val selected: Boolean,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

/**
 * `m3/navigation-suite-item` as `NavigationSuiteItem(selected, onClick, icon, label)`, which the
 * scaffold's `navigationItems` lays out for the window's navigation type. `selected` is the call's
 * own parameter, so a `stateEquals` read reaches it as the hand exporter writes it.
 */
class RealNavigationSuiteItemAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RealNavigationSuiteItemProperties>("m3/navigation-suite-item", record) {
  val selected =
    property(RealNavigationSuiteItemProperties::selected, AdapterValueCodecs.Boolean, false)
  val enabled =
    property(
      RealNavigationSuiteItemProperties::enabled,
      AdapterValueCodecs.Boolean,
      true,
      bindable = false,
    )
  val onClick = event(RealNavigationSuiteItemProperties::onClick)
  val icon = slot("icon", required = true)
  val label = slot("label")

  init {
    exportAs(
      target(
        record,
        mapOf(
          "selected" to from(selected),
          "enabled" to from(enabled, omitWhen = JsonPrimitive(true)),
        ),
        events = mapOf("click" to "onClick"),
      )
    )
  }
}

data class RealNavigationSuiteScaffoldProperties(
  val containerColor: WireColor,
  val contentColor: WireColor,
)

/**
 * `m3/navigation-suite-scaffold` as the `navigationItems` overload of `NavigationSuiteScaffold`:
 * the items are its `navigationItems`, the builder's `primaryAction` its `primaryActionContent`,
 * and the screen its `content`.
 */
class RealNavigationSuiteScaffoldAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RealNavigationSuiteScaffoldProperties>(
    "m3/navigation-suite-scaffold",
    record,
  ) {
  val containerColor =
    wireProperty(RealNavigationSuiteScaffoldProperties::containerColor, COLOR, WireColor.Unset)
  val contentColor =
    wireProperty(RealNavigationSuiteScaffoldProperties::contentColor, COLOR, WireColor.Unset)
  val navigationItems = slot("navigationItems", required = true)
  val primaryAction = slot("primaryAction")
  val content = slot("content", required = true)

  init {
    exportAs(
      target(
        record,
        mapOf("containerColor" to from(containerColor), "contentColor" to from(contentColor)),
        slots = mapOf("primaryAction" to "primaryActionContent"),
      )
    )
  }
}

data class RealButtonProperties(
  val style: String,
  val enabled: Boolean,
  val containerColor: WireColor,
  val selected: Boolean,
  val onClick: () -> Unit,
)

/**
 * `m3/button` as Material 3's buttons, picked by `style` as the hand exporter picks them: `filled`
 * (the default) is `Button`, `filledTonal` is `FilledTonalButton`, `text` is `TextButton`, each
 * with its own `ButtonDefaults` colours, and `fab` is `FloatingActionButton`, which has no
 * `enabled` and takes `containerColor` directly. A click is the call's `onClick`; the children are
 * the button's `RowScope` content (a FAB's plain content).
 */
class RealButtonAdapter(
  button: ComponentRecord,
  private val filledTonal: ComponentRecord,
  private val text: ComponentRecord,
  private val fab: ComponentRecord,
  factories: List<FactorySignature>,
) : TypedComponentAdapter<RealButtonProperties>("m3/button", button) {
  val style =
    wireProperty(
      RealButtonProperties::style,
      exported(ExportProjection.Codec.Text, setOf("filled", "filledTonal", "text", "fab")),
      "",
    )
  val enabled =
    property(RealButtonProperties::enabled, AdapterValueCodecs.Boolean, true, bindable = false)
  val containerColor = wireProperty(RealButtonProperties::containerColor, COLOR, WireColor.Unset)
  // A toolbar item's selection, which the canvas draws and no button parameter carries.
  val selected = wireProperty(RealButtonProperties::selected, AdapterValueCodecs.Boolean, false)
  val onClick = event(RealButtonProperties::onClick)
  val content = slot("content", required = true)

  private fun button(
    record: ComponentRecord,
    defaults: String,
    variants: Set<String>?,
    factories: List<FactorySignature>,
  ) =
    target(
      record,
      mapOf(
        "enabled" to from(enabled, omitWhen = JsonPrimitive(true)),
        "colors" to
          call(
            factories.named("$MATERIAL3.ButtonDefaults.${defaults}Colors"),
            "containerColor" to from(containerColor),
          ),
      ),
      variants = variants,
      events = mapOf("click" to "onClick"),
    )

  init {
    exportAs(
      button(button, "button", null, factories),
      button(button, "button", setOf("filled"), factories),
      button(filledTonal, "filledTonalButton", setOf("filledTonal"), factories),
      button(text, "textButton", setOf("text"), factories),
      target(
        fab,
        mapOf("containerColor" to from(containerColor)),
        variants = setOf("fab"),
        events = mapOf("click" to "onClick"),
      ),
      variant = style,
    )
    exportRefuses(selected)
  }
}
