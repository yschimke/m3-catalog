package ee.schimke.m3catalog.uid

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.CatalogImagePlaceholder

/**
 * The kit's `M3/.add-on/placeholder image`, as a component a `.uid` document can place.
 *
 * A builder-facing signature over [CatalogImagePlaceholder]: the builder writes scalar values, and
 * a `Shape` parameter is something it cannot spell today, so the corner is a radius in dp.
 */
@Composable
fun ImagePlaceholder(
  modifier: Modifier = Modifier,
  cornerRadiusDp: Float = 24f,
  scaleBasis: Float = 188f,
) {
  CatalogImagePlaceholder(modifier, RoundedCornerShape(cornerRadiusDp.dp), scaleBasis)
}
