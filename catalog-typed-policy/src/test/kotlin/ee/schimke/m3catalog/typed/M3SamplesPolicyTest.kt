package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class M3SamplesPolicyTest {
  @Test
  fun `samples preserve their own identity and discovery owned shelf`() {
    val baseline = Json {
      ignoreUnknownKeys = true
    }
      .decodeFromString(
        UiBuilderPolicyFile.serializer(),
        File(System.getProperty("m3SamplesLegacyPolicy")).readText(),
      )
    assertEquals(baseline.newBuilder().apply { comment = null }.build(), m3SamplesPolicy())
    assertEquals("m3-samples", m3SamplesPolicy().catalogId)
    assertTrue(m3SamplesPolicy().components.isEmpty())
    assertTrue(m3SamplesPolicy().builtins.isEmpty())
    assertNull(m3SamplesPolicy().menu)
    assertNull(m3SamplesPolicy().frame)
  }
}
