package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecord
import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.GeneratedAdapterCatalog
import ee.schimke.composeai.discovery.TypedAdapterCatalog
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import ee.schimke.composeai.discovery.UiBuilderCatalogs
import kotlinx.serialization.json.jsonObject

class TypedM3Catalog(val discovered: ComponentRecordFile) {
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
    val cover = UiBuilderCatalogs.CoverSheet("m3-catalog-typed-pilot", "Material 3 — typed catalog")
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
    val full = requireNotNull(UiBuilderCatalogs.generate(portable.record, cover, authored))
    check(full.diagnostics.all { it.code == UiBuilderCatalogs.Diagnostics.COMPONENT_EXCLUDED }) {
      full.diagnostics.joinToString { it.message }
    }
    builtinAdapters.forEach { it.freeze() }
    return M3CatalogOutput(portable, full)
  }
}

class M3CatalogOutput(val portable: GeneratedAdapterCatalog, val catalog: UiBuilderCatalogFile) {
  val record: ComponentRecordFile
    get() = portable.record
}
