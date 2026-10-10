package ee.schimke.m3catalog.typed

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.json.JsonPrimitive

class M3ValueCodecsTest {
  @Test
  fun `numeric properties accept typed state values and refuse malformed scalars`() {
    assertEquals(0.5f, M3ValueCodecs.Float.decode(JsonPrimitive(0.5)))
    assertEquals(2, M3ValueCodecs.Int.decode(JsonPrimitive(2)))
    assertFailsWith<IllegalArgumentException> { M3ValueCodecs.Int.decode(JsonPrimitive(2.5)) }
    assertFailsWith<IllegalArgumentException> { M3ValueCodecs.Float.decode(JsonPrimitive("0.5")) }
    assertFailsWith<IllegalArgumentException> {
      M3ValueCodecs.Float.decode(JsonPrimitive("invalid"))
    }
    assertFailsWith<IllegalArgumentException> {
      M3ValueCodecs.Float.encode(Float.POSITIVE_INFINITY)
    }
    assertEquals("number", M3ValueCodecs.Int.stateType)
  }
}
