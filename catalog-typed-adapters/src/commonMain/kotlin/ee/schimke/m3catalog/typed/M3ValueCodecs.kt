package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterValueCodec
import ee.schimke.composeai.discovery.AdapterValueCodecs

/**
 * The shared strict codecs, with the state types the catalog's numeric properties bind to.
 *
 * Delegated rather than implemented: the codec interface gains members with defaults (`editor`),
 * and an implementing class compiled against one jvm-default mode fails with `AbstractMethodError`
 * against the other, which the tools build's discovery jar and its screen-model differ in.
 */
object M3ValueCodecs {
  val Float: AdapterValueCodec<Float> =
    object : AdapterValueCodec<Float> by AdapterValueCodecs.Float {
      override val stateType = "number"
    }
  val Int: AdapterValueCodec<Int> =
    object : AdapterValueCodec<Int> by AdapterValueCodecs.Int {
      override val stateType = "number"
    }
}
