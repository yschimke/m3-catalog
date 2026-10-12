package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecord
import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.ExportProjectionJson
import ee.schimke.composeai.discovery.GeneratedAdapterCatalog
import ee.schimke.composeai.discovery.TypedAdapterCatalog
import ee.schimke.composeai.discovery.TypedComponentAdapter
import ee.schimke.composeai.discovery.UiBuilderAuthoredComponent
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import ee.schimke.composeai.discovery.UiBuilderCatalogs
import ee.schimke.composeai.discovery.UiBuilderComposeSourceExport
import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import kotlinx.serialization.json.jsonObject

/** The parallel pilot's catalog id. Production keeps `m3-catalog` and its committed JSON policy. */
const val TYPED_M3_CATALOG_ID = "m3-catalog-typed-pilot"

/**
 * The typed pilot's wrapper adapters, each built against its freshly discovered compiled record,
 * and optionally the [real]-record definitions its export goes through. Ids are the production ids,
 * so saved designs address them unchanged.
 */
class TypedM3Catalog(
  val discovered: ComponentRecordFile,
  /** The real-record definitions to export through; null publishes the wrappers alone. */
  val real: RealM3Publication? = null,
) {
  private fun record(name: String): ComponentRecord =
    discovered.components.single { it.symbol.name == name }

  val button = ButtonAdapter(record("M3Button"))
  val card = CardAdapter(record("M3Card"))
  val centerAlignedTopAppBar = CenterAlignedTopAppBarAdapter(record("M3CenterAlignedTopAppBar"))
  val checkbox = CheckboxAdapter(record("M3Checkbox"))
  val datePicker = DatePickerAdapter(record("M3DatePicker"))
  val dialog = DialogAdapter(record("M3Dialog"))
  val filterChip = FilterChipAdapter(record("M3FilterChip"))
  val floatingActionButton = FloatingActionButtonAdapter(record("M3FloatingActionButton"))
  val horizontalDivider = HorizontalDividerAdapter(record("M3HorizontalDivider"))
  val horizontalFloatingToolbar =
    HorizontalFloatingToolbarAdapter(record("M3HorizontalFloatingToolbar"))
  val icon = IconAdapter(record("M3Icon"))
  val iconButton = IconButtonAdapter(record("M3IconButton"))
  val listItem = ListItemAdapter(record("M3ListItem"))
  val primaryTabRow = PrimaryTabRowAdapter(record("M3PrimaryTabRow"))
  val linearProgressIndicator = LinearProgressIndicatorAdapter(record("M3LinearProgressIndicator"))
  val circularProgressIndicator =
    CircularProgressIndicatorAdapter(record("M3CircularProgressIndicator"))
  val provideTextStyle = ProvideTextStyleAdapter(record("M3ProvideTextStyle"))
  val radioButton = RadioButtonAdapter(record("M3RadioButton"))
  val searchBar = SearchBarAdapter(record("M3SearchBar"))
  val searchInputField = SearchInputFieldAdapter(record("M3SearchInputField"))
  val slider = SliderAdapter(record("M3Slider"))
  val surface = SurfaceAdapter(record("M3Surface"))
  val switch = SwitchAdapter(record("M3Switch"))
  val tab = TabAdapter(record("M3Tab"))
  val text = TextAdapter(record("M3Text"))
  val textField = TextFieldAdapter(record("M3TextField"))
  val outlinedTextField = OutlinedTextFieldAdapter(record("M3OutlinedTextField"))
  val timePicker = TimePickerAdapter(record("M3TimePicker"))
  val sticker = StickerAdapter(record("M3Sticker"))
  val materialExpressiveTheme = MaterialExpressiveThemeAdapter(record("M3MaterialExpressiveTheme"))
  val navigationSuiteItem = NavigationSuiteItemAdapter(record("M3NavigationSuiteItem"))
  val navigationSuiteScaffold = NavigationSuiteScaffoldAdapter(record("M3NavigationSuiteScaffold"))
  val primaryScrollableTabRow = PrimaryScrollableTabRowAdapter(record("M3PrimaryScrollableTabRow"))
  val variableFontText = VariableFontTextAdapter(record("M3VariableFontText"))
  val snackbarHost = SnackbarHostAdapter(record("M3SnackbarHost"))
  val radio
    get() = radioButton

  val adapters =
    listOf(
      button,
      card,
      centerAlignedTopAppBar,
      checkbox,
      datePicker,
      dialog,
      filterChip,
      floatingActionButton,
      horizontalDivider,
      horizontalFloatingToolbar,
      icon,
      iconButton,
      listItem,
      primaryTabRow,
      linearProgressIndicator,
      circularProgressIndicator,
      provideTextStyle,
      radioButton,
      searchBar,
      searchInputField,
      slider,
      surface,
      switch,
      tab,
      text,
      textField,
      outlinedTextField,
      timePicker,
      sticker,
      materialExpressiveTheme,
      navigationSuiteItem,
      navigationSuiteScaffold,
      primaryScrollableTabRow,
    )
  val builtinAdapters = listOf(variableFontText, snackbarHost)
  val runtimeAdapters = adapters + builtinAdapters

  fun generate(): M3CatalogOutput {
    val basePolicy = m3BuilderPolicy()
    val cover = UiBuilderCatalogs.CoverSheet(TYPED_M3_CATALOG_ID, "Material 3 — typed catalog")
    val portable =
      TypedAdapterCatalog.generate(
        discovered,
        cover,
        basePolicy
          .newBuilder()
          .apply {
            catalogId = cover.system
            components = emptyMap()
          }
          .build(),
        adapters,
      )
    val inferred = portable.catalog.statusSemantics.components
    val authored =
      basePolicy
        .newBuilder()
        .apply {
          catalogId = cover.system
          components =
            basePolicy.components.mapValues { (id, old) ->
              val current = inferred.getValue(id)
              old
                .newBuilder()
                .apply {
                  record = current.record
                  canvas = current.canvas
                  // Existing domain constraints remain authoritative; extra real wrapper
                  // parameters/slots
                  // are included so the portable record and renderer cannot hide supported entry
                  // points.
                  propertyCapabilities =
                    old.propertyCapabilities.orEmpty() +
                      current.propertyCapabilities.orEmpty().filter { candidate ->
                        old.propertyCapabilities.orEmpty().none {
                          it.jsonObject["name"] == candidate.jsonObject["name"]
                        }
                      }
                  slotCapabilities =
                    old.slotCapabilities.orEmpty() +
                      current.slotCapabilities.orEmpty().filter { candidate ->
                        old.slotCapabilities.orEmpty().none {
                          it.jsonObject["name"] == candidate.jsonObject["name"]
                        }
                      }
                }
                .build()
            }
        }
        .build()
    val published = real?.let { publish(authored, portable.record, it) }
    val record = published?.first ?: portable.record
    val full =
      requireNotNull(UiBuilderCatalogs.generate(record, cover, published?.second ?: authored))
    check(full.diagnostics.all { it.code == UiBuilderCatalogs.Diagnostics.COMPONENT_EXCLUDED }) {
      full.diagnostics.joinToString { it.message }
    }
    builtinAdapters.forEach { it.freeze() }
    return M3CatalogOutput(portable, full, record)
  }

  /**
   * [policy] and [record] exporting through [real], as wear-m3-catalog's pilot publishes: the real
   * records join the record as export targets kept off the shelf, each shelf component a real
   * definition covers carries that definition's projection (its wrapper still draws it), and the
   * catalog selects the component-record export, which reads them. No `code` block: an M3 screen's
   * root is its own scaffold (`layout/scaffold`, `m3/navigation-suite-scaffold`, a pane scaffold),
   * so there is no frame around it and nothing it declares for the records to read.
   */
  private fun publish(
    policy: UiBuilderPolicyFile,
    record: ComponentRecordFile,
    real: RealM3Publication,
  ): Pair<ComponentRecordFile, UiBuilderPolicyFile> {
    val byId = real.adapters.associateBy { it.id }
    val targets =
      real.adapters
        .flatMap { adapter -> listOf(adapter.component) + adapter.variantRecords }
        .distinctBy { it.canonicalId }
    val components =
      policy.components.mapValues { (id, component) ->
        val adapter = byId[id] ?: return@mapValues component
        component.newBuilder().also { it.exportProjection = projectionJson(adapter) }.build()
      } +
        targets.associate { target ->
          val owner = real.adapters.first { target.canonicalId in it.recordIds() }
          "$EXPORT_TARGET_PREFIX${target.symbol.name.lowercase()}" to
            UiBuilderAuthoredComponent.Builder()
              .also {
                it.record = target.canonicalId
                it.excluded = "an export target of ${owner.id}"
              }
              .build()
        }
    val published =
      policy
        .newBuilder()
        .apply {
          this.components = components
          composeSourceExport =
            UiBuilderComposeSourceExport.Builder(COMPONENT_RECORDS, COMPONENT_RECORDS_V1).build()
        }
        .build()
    val merged =
      record
        .newBuilder()
        .also { b ->
          b.components =
            record.components +
              targets.filter { t -> record.components.none { it.canonicalId == t.canonicalId } }
        }
        .build()
    return merged to published
  }

  private fun TypedComponentAdapter<*>.recordIds(): Set<String> =
    (listOf(component) + variantRecords).map { it.canonicalId }.toSet()

  private fun projectionJson(adapter: TypedComponentAdapter<*>) =
    ExportProjectionJson.encode(
      requireNotNull(adapter.projection) { "${adapter.id}: no projection" }
    )

  companion object {
    /** Policy ids of the real records published only as export targets. */
    const val EXPORT_TARGET_PREFIX: String = "m3/export-"

    /** compose-ui-builder's catalog-neutral component-record export adapter. */
    const val COMPONENT_RECORDS: String = "compose-component-records"
    const val COMPONENT_RECORDS_V1: Int = 1
  }
}

/** The real-record definitions a pilot pair exports through, read where discovery runs (JVM). */
class RealM3Publication(val adapters: List<TypedComponentAdapter<*>>)

class M3CatalogOutput(
  val portable: GeneratedAdapterCatalog,
  val catalog: UiBuilderCatalogFile,
  /** The published record: the portable pair's, plus any real export targets. */
  val record: ComponentRecordFile = portable.record,
)
