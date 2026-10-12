package ee.schimke.m3catalog.uid

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogOutlinedStars

/**
 * The kit's stars glyph in the shape a selection control draws it: [CatalogFilledStars] when
 * [selected], [CatalogOutlinedStars] otherwise. Decorative, and sized by the document's modifier.
 *
 * A graphic rather than a wrapper: the glyph is the catalog's own vector, which no record names,
 * and the swap follows the selection the document already binds on the control that holds it.
 */
@Composable
fun SelectionStarsIcon(selected: Boolean, modifier: Modifier = Modifier) {
  Icon(
    if (selected) CatalogFilledStars else CatalogOutlinedStars,
    contentDescription = null,
    modifier = modifier,
  )
}
