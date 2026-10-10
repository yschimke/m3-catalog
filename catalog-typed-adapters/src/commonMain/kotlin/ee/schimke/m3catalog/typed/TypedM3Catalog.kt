package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterValueCodecs
import ee.schimke.composeai.discovery.ComponentRecord
import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.TypedAdapterCatalog
import ee.schimke.composeai.discovery.TypedComponentAdapter
import ee.schimke.composeai.discovery.UI_BUILDER_POLICY_SCHEMA
import ee.schimke.composeai.discovery.UiBuilderCatalogs
import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ToggleAdapter(id: String, record: ComponentRecord) :
  TypedComponentAdapter<ToggleProperties>(id, record) {
  val checked =
    property(ToggleProperties::checked, AdapterValueCodecs.Boolean, ControlDefaults.checked)
  val enabled =
    property(
      ToggleProperties::enabled,
      AdapterValueCodecs.Boolean,
      ControlDefaults.enabled,
      bindable = false,
    )
  val change = stateChange(ToggleProperties::onCheckedChange, checked)
}

class RadioAdapter(record: ComponentRecord) :
  TypedComponentAdapter<RadioProperties>("m3/radio-button", record) {
  val selected =
    property(RadioProperties::selected, AdapterValueCodecs.Boolean, ControlDefaults.checked)
  val enabled =
    property(
      RadioProperties::enabled,
      AdapterValueCodecs.Boolean,
      ControlDefaults.enabled,
      bindable = false,
    )
  val click = event(RadioProperties::onClick)
}

class TypedM3Catalog(val discovered: ComponentRecordFile) {
  private fun record(name: String): ComponentRecord =
    discovered.components.single { it.symbol.name == name }

  val checkbox = ToggleAdapter("m3/checkbox", record("M3Checkbox"))
  val switch = ToggleAdapter("m3/switch", record("M3Switch"))
  val radio = RadioAdapter(record("M3RadioButton"))
  val adapters = listOf(checkbox, switch, radio)

  fun generate() =
    TypedAdapterCatalog.generate(
      discovered = discovered,
      cover =
        UiBuilderCatalogs.CoverSheet("m3-catalog-typed-pilot", "Material 3 — typed adapter pilot"),
      policy =
        UiBuilderPolicyFile.Builder(UI_BUILDER_POLICY_SCHEMA, "mobile")
          .apply {
            catalogId = "m3-catalog-typed-pilot"
            componentIdPrefix = "m3/"
            platformLabel = "Mobile"
            frame = buildJsonObject {
              put("adapter", "frame/rect")
              put("seedDevice", "id:pixel_6")
            }
          }
          .build(),
      adapters = adapters,
    )
}
