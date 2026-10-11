package ee.schimke.m3catalog.uid

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogOutlinedStars

/**
 * The kit's filled-stars glyph ([CatalogFilledStars]) with a content description, for a document
 * whose icon IS the control's label: a FAB names its action this way, where [StarsIcon] is the
 * decorative glyph beside a text label.
 */
@Composable
fun DescribedStarsIcon(modifier: Modifier = Modifier, contentDescription: String? = null) {
  Icon(CatalogFilledStars, contentDescription = contentDescription, modifier = modifier)
}

/**
 * The kit's favourite glyph for an icon-button toggle: [CatalogFilledStars] when [selected], else
 * [CatalogOutlinedStars]. The swap is a graphic choice the builder cannot spell (its icon keys are
 * the Material set), so the glyph pair is one component with a boolean the document binds. The
 * boolean is `selected` because the exporter types a binding by its property name, and `selected`
 * is one of the names it reads as a `Boolean`.
 */
@Composable
fun FavouriteStarsIcon(
  modifier: Modifier = Modifier,
  selected: Boolean = true,
  contentDescription: String? = null,
) {
  Icon(
    if (selected) CatalogFilledStars else CatalogOutlinedStars,
    contentDescription = contentDescription,
    modifier = modifier,
  )
}
