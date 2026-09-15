@file:CatalogGroup(name = "Layout", section = "Layout")

package ee.schimke.foundationcatalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.foundationcatalog.AltChild
import ee.schimke.foundationcatalog.Child
import ee.schimke.foundationcatalog.FoundationModes
import ee.schimke.foundationcatalog.Sticker

// The three layout primitives, and the whole reason this catalog exists: a design has to be put
// inside something, and a box is not a Material 3 component.
//
// The stickers carry no `@BuilderComponent`, and that is the one surprising thing in this module.
// Discovery scopes the component record to `material3` / `material` / `wear` library symbols, so
// `Box`, `Column` and `Row` do not enter it at all — a per-component policy here would attach to
// nothing, which is exactly what it did on the first run (`component.policy.orphaned`, eleven
// times). This vocabulary is therefore declared in `ui-builder.policy.json` as builtins, which is
// also what the schema's own rule asks for: a builtin is a component with no call site in this
// catalog's record. The stickers are the PICTURES for that shelf, and nothing else.
//
// The one `@BuilderComponent` in this module lives in Assets.kt and excludes `Sticker`, the theme
// wrapper — see there for why it sits on that preview rather than this one.

@CatalogComponent(
  id = "Layout/Box",
  noReference =
    "Not a kit component. `Box` is Compose's own stacking container — the Material 3 " +
      "Design Kit publishes no node for it, and this catalog reproduces no kit.",
  caption = "Stacks children on top of each other, aligned within the box.",
)
@FoundationModes
@Composable
fun BoxSticker() = Sticker {
  Box(contentAlignment = Alignment.Center) {
    Child(width = 96.dp, height = 64.dp)
    AltChild(width = 40.dp, height = 24.dp)
  }
}

@CatalogComponent(
  id = "Layout/Column",
  noReference = "Not a kit component — Compose's own vertical container.",
  caption = "Lays children out vertically, arranged and aligned.",
)
@FoundationModes
@Composable
fun ColumnSticker() = Sticker {
  Column(
    modifier = Modifier.padding(4.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Child()
    AltChild(width = 80.dp)
    Child()
  }
}

@CatalogComponent(
  id = "Layout/Row",
  noReference = "Not a kit component — Compose's own horizontal container.",
  caption = "Lays children out horizontally, arranged and aligned.",
)
@FoundationModes
@Composable
fun RowSticker() = Sticker {
  Row(
    modifier = Modifier.padding(4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Child(width = 40.dp)
    AltChild(width = 40.dp, height = 48.dp)
    Child(width = 40.dp)
  }
}
