package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.FactorySignature
import ee.schimke.composeai.discovery.TypedAdapterDiscovery
import ee.schimke.composeai.discovery.TypedAdapterDiscovery.LibraryCallable
import ee.schimke.composeai.discovery.TypedComponentAdapter
import ee.schimke.m3catalog.typed.real.RealButtonAdapter
import ee.schimke.m3catalog.typed.real.RealCardAdapter
import ee.schimke.m3catalog.typed.real.RealIconAdapter
import ee.schimke.m3catalog.typed.real.RealNavigationSuiteItemAdapter
import ee.schimke.m3catalog.typed.real.RealNavigationSuiteScaffoldAdapter
import ee.schimke.m3catalog.typed.real.RealTextAdapter

/**
 * The real Compose Material 3 composables the typed definitions export to, read from the library
 * metadata on this module's JVM classpath (Compose Multiplatform's desktop artifact publishes the
 * same `androidx.compose.material3` API the Android export calls). Nothing is loaded or run.
 */
object RealM3Records {
  private const val MATERIAL3 = "androidx.compose.material3"
  private const val COLOR = "androidx.compose.ui.graphics.Color"
  private const val NAVIGATION_SUITE = "$MATERIAL3.adaptive.navigationsuite"

  /** The module the records are published under; production's own discovery is `catalog`. */
  const val MODULE: String = "m3-catalog-typed-pilot"

  /** The content overload, without the clickable one's `onClick` (picked when a click is bound). */
  private fun card(name: String) =
    LibraryCallable("$MATERIAL3.CardKt", name, excluding = setOf("onClick"))

  private fun button(name: String) =
    LibraryCallable("$MATERIAL3.ButtonKt", name, excluding = setOf("shapes"))

  val callables: List<LibraryCallable> =
    listOf(
      // `text: String`, not `AnnotatedString`; a `color: Color`, not the `ColorProducer` overload.
      LibraryCallable(
        "$MATERIAL3.TextKt",
        "Text",
        types = mapOf("text" to "kotlin.String", "color" to COLOR),
      ),
      // The vector overload; `painter` and `bitmap` are the image ones.
      LibraryCallable(
        "$MATERIAL3.IconKt",
        "Icon",
        types =
          mapOf(
            "imageVector" to "androidx.compose.ui.graphics.vector.ImageVector",
            "tint" to COLOR,
          ),
      ),
      // The `shape` overloads the hand exporter calls, not the expressive `shapes: ButtonShapes`
      // ones; `fab` is a separate family with no `enabled`.
      button("Button"),
      button("FilledTonalButton"),
      button("TextButton"),
      LibraryCallable("$MATERIAL3.FloatingActionButtonKt", "FloatingActionButton"),
      card("Card"),
      card("ElevatedCard"),
      card("OutlinedCard"),
      LibraryCallable("$NAVIGATION_SUITE.NavigationSuiteScaffoldKt", "NavigationSuiteItem"),
      // The slot overload the builder's three slots are, not the `NavigationSuiteScope` DSL one.
      LibraryCallable(
        "$NAVIGATION_SUITE.NavigationSuiteScaffoldKt",
        "NavigationSuiteScaffold",
        declaring = setOf("navigationItems", "primaryActionContent"),
      ),
    )

  /** The `CardDefaults` and `ButtonDefaults` factories the card and button projections call. */
  val factoryCallables: List<TypedAdapterDiscovery.Callable> =
    listOf("card", "elevatedCard", "outlinedCard").flatMap { kind ->
      listOf("${kind}Colors", "${kind}Elevation").map {
        TypedAdapterDiscovery.Callable("$MATERIAL3.CardDefaults", it)
      }
    } +
      listOf("button", "filledTonalButton", "textButton").map {
        TypedAdapterDiscovery.Callable("$MATERIAL3.ButtonDefaults", "${it}Colors")
      }

  fun discover(): ComponentRecordFile =
    TypedAdapterDiscovery.discoverLibrary(MODULE, "release", callables)

  fun factories(): List<FactorySignature> =
    TypedAdapterDiscovery.discoverFactories(factoryCallables)

  /** What the pilot pair publishes to export through the real records. */
  fun publication(): RealM3Publication = RealM3Publication(adapters())

  /** The real-record definitions, each on the record discovery read for it. */
  fun adapters(): List<TypedComponentAdapter<*>> {
    val records = discover().components.associateBy { it.symbol.name }
    return listOf(
      RealTextAdapter(records.getValue("Text")),
      RealIconAdapter(records.getValue("Icon")),
      RealButtonAdapter(
        records.getValue("Button"),
        records.getValue("FilledTonalButton"),
        records.getValue("TextButton"),
        records.getValue("FloatingActionButton"),
        factories(),
      ),
      RealCardAdapter(
        records.getValue("Card"),
        records.getValue("ElevatedCard"),
        records.getValue("OutlinedCard"),
        factories(),
      ),
      RealNavigationSuiteItemAdapter(records.getValue("NavigationSuiteItem")),
      RealNavigationSuiteScaffoldAdapter(records.getValue("NavigationSuiteScaffold")),
    )
  }
}
