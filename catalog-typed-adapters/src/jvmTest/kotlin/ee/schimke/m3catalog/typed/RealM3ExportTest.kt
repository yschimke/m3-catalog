package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import ee.schimke.composeai.uibuilder.export.CatalogExportProjections
import ee.schimke.composeai.uibuilder.export.CatalogScreenStructure
import ee.schimke.composeai.uibuilder.export.ScreenExportGate
import ee.schimke.composeai.uibuilder.protocol.DesignDocumentV1
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * The pilot pair, exported end to end the way the editor exports it: the projections it publishes,
 * read back by compose-ui-builder's own readers, and every committed design through
 * `ScreenExportGate`. A design that is refused is listed in [REFUSED] with the reason, which
 * docs/design/TYPED_M3_REAL_RECORDS.md explains; one that starts emitting fails here until it
 * moves.
 */
class RealM3ExportTest {
  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  private val pair =
    TypedM3Catalog(discoverTypedM3Controls(), RealM3Records.publication()).generate()

  private val semantics =
    json
      .encodeToJsonElement(UiBuilderCatalogFile.serializer(), pair.catalog)
      .jsonObject
      .getValue("statusSemantics")
      .jsonObject

  private val root =
    generateSequence(File(".").absoluteFile) { it.parentFile }
      .first { File(it, "ui-builder/designs").isDirectory }

  /**
   * Every design the catalog commits: its templates and anything else under `ui-builder/designs`.
   */
  private val designs: List<String> =
    File(root, "ui-builder/designs")
      .listFiles { file -> file.extension == "json" }!!
      .map { it.name }
      .sorted()

  /**
   * compose-ui-builder's packaged foundation record (`layout/…`), which a host joins to every
   * catalog's record (`ComponentRecordSource.withFoundation`): the catalog's entries win, by
   * canonical id or by claimed id.
   */
  private val foundation: ComponentRecordFile =
    json.decodeFromString(
      File(
          System.getProperty("composeUiBuilderDir").also {
            require(!it.isNullOrBlank()) { "the export test needs -PcomposeUiBuilderDir" }
          },
          "docs/design/fixtures/ui-builder/compose-foundation-components-v1.json",
        )
        .readText()
    )

  /**
   * The record as a host serves it: each published component's id joined to the record entry its
   * policy names (`PublishedUiBuilderCatalog` does the same), plus the builder's foundation record.
   */
  private val record: ComponentRecordFile =
    pair.record
      .newBuilder()
      .also { b ->
        val ids =
          pair.catalog.statusSemantics.components.entries
            .mapNotNull { (id, policy) -> policy.record?.let { it to id } }
            .groupBy({ it.first }, { it.second })
        val own =
          pair.record.components.map { component ->
            val joined = ids[component.canonicalId].orEmpty()
            if (joined.isEmpty()) component
            else
              component
                .newBuilder()
                .also { it.componentIds = (component.componentIds + joined).distinct() }
                .build()
          }
        val taken = own.map { it.canonicalId }.toSet()
        val claimed = own.flatMapTo(mutableSetOf()) { it.componentIds }
        b.components =
          own +
            foundation.components.filterNot { candidate ->
              candidate.canonicalId in taken || candidate.componentIds.any { it in claimed }
            }
      }
      .build()

  private fun design(name: String): String = File(root, "ui-builder/designs/$name").readText()

  private fun export(name: String): ScreenExportGate.Outcome = exportText(design(name))

  private fun exportText(document: String): ScreenExportGate.Outcome =
    ScreenExportGate.export(
      json.decodeFromString<DesignDocumentV1>(document),
      record,
      CatalogExportProjections.from(semantics),
      CatalogScreenStructure.from(semantics),
    )

  private fun emitted(name: String): String =
    assertIs<ScreenExportGate.Outcome.Emitted>(export(name), name).source

  @Test
  fun `the pilot selects the component-record export and publishes the projections`() {
    val export = pair.catalog.statusSemantics.composeSourceExport!!
    assertEquals(TypedM3Catalog.COMPONENT_RECORDS to 1, export.adapter to export.version)
    assertEquals(
      setOf(
        "m3/button",
        "m3/card",
        "m3/icon",
        "m3/navigation-suite-item",
        "m3/navigation-suite-scaffold",
        "m3/text",
      ),
      CatalogExportProjections.from(semantics).keys,
    )
  }

  @Test
  fun `a navigation suite exports as the real scaffold, items and icons`() {
    val source = emitted("adaptive-navigation.json")
    for (expected in
      listOf(
        "NavigationSuiteScaffold(",
        "navigationItems = {",
        "NavigationSuiteItem(selected = true, onClick = {}",
        "Icon(imageVector = Icons.Filled.Home, contentDescription = \"Home\")",
        "Text(text = \"Your app\", style = MaterialTheme.typography.headlineMedium)",
      )) {
      assertContains(source, expected)
    }
  }

  @Test
  fun `a feed's cards export as the real card in the lazy grid`() {
    val source = emitted("adaptive-feed.json")
    assertContains(source, "LazyVerticalGrid(")
    assertContains(source, "Card {")
    assertContains(source, "Text(text = \"Item 1\", style = MaterialTheme.typography.titleLarge)")
  }

  @Test
  fun `a property the projection does not write is refused by name, not approximated`() {
    // The hand exporter writes a card's shape token; the projection refuses it until a codec can.
    val shaped =
      design("adaptive-feed.json")
        .replaceFirst(
          "\"componentId\": \"m3/card\",\n      \"properties\": {}",
          "\"componentId\": \"m3/card\",\n      \"properties\": " +
            "{\"shape\": {\"type\": \"string\", \"value\": \"medium\"}}",
        )
    val refused = assertIs<ScreenExportGate.Outcome.Refused>(exportText(shaped))
    assertContains(
      refused.reasons.joinToString("\n"),
      "node `card-1`.`shape` is not written by `m3/card`'s export projection",
    )
  }

  @Test
  fun `every committed design emits or is refused for a documented reason`() {
    val outcomes = designs.associateWith(::export)
    // The exported Kotlin, kept as evidence beside the test reports.
    System.getProperty("typedExportEvidence")
      ?.let { File(it) }
      ?.let { directory ->
        directory.mkdirs()
        for ((name, outcome) in outcomes) {
          File(directory, name.removeSuffix(".json") + ".kt")
            .writeText(
              when (outcome) {
                is ScreenExportGate.Outcome.Emitted -> outcome.source
                is ScreenExportGate.Outcome.Refused ->
                  outcome.reasons.joinToString("\n") { "// $it" }
              }
            )
        }
      }
    val refused =
      outcomes
        .filterValues { it is ScreenExportGate.Outcome.Refused }
        .mapValues { (_, it) -> (it as ScreenExportGate.Outcome.Refused).reasons }
    assertEquals(REFUSED.keys, refused.keys, "refused: $refused")
    for ((name, reasons) in refused) {
      assertContains(reasons.joinToString("\n"), REFUSED.getValue(name), message = name)
    }
    assertEquals(EMITTED, outcomes.keys - refused.keys)
  }

  companion object {
    /** The designs that export through the pilot pair. */
    val EMITTED: Set<String> =
      setOf(
        "adaptive-feed.json",
        "adaptive-navigation.json",
        "blank.json",
        "hello.json",
        "list-detail.json",
        "supporting-pane.json",
      )

    /** Design → a fragment of the reason it is refused, documented in TYPED_M3_REAL_RECORDS.md. */
    val REFUSED: Map<String, String> = emptyMap()
  }
}
