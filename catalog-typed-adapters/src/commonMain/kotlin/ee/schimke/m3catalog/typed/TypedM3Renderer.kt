package ee.schimke.m3catalog.typed

import ee.schimke.composeai.uibuilder.renderer.sdk.canvasAdapterRegistry
import ee.schimke.composeai.uibuilder.renderer.sdk.register

fun typedM3CanvasAdapters(catalog: TypedM3Catalog) = canvasAdapterRegistry {
  register(catalog.checkbox) {
    M3Checkbox(
      checked = value(catalog.checkbox.checked),
      enabled = value(catalog.checkbox.enabled),
      onCheckedChange = callback(catalog.checkbox.change),
      modifier = modifier,
    )
  }
  register(catalog.switch) {
    M3Switch(
      checked = value(catalog.switch.checked),
      enabled = value(catalog.switch.enabled),
      onCheckedChange = callback(catalog.switch.change),
      modifier = modifier,
    )
  }
  register(catalog.radio) {
    M3RadioButton(
      selected = value(catalog.radio.selected),
      enabled = value(catalog.radio.enabled),
      onClick = callback(catalog.radio.click),
      modifier = modifier,
    )
  }
}
