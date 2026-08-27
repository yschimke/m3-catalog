package ee.schimke.m3catalog

/**
 * One matrix annotation as it is **declared**: the class name it is emitted under, the KDoc it
 * carries, and the matrix whose cells it spells out.
 *
 * `CatalogMatrixAnnotations.kt` is generated from this list (see `MatrixAnnotationsGenerator.kt`),
 * so the cells exist once — in [CatalogVariantMatrices] — rather than twice, and adding a value to
 * an axis is a one-line edit plus a regenerate rather than thirty hand-typed lines in the right
 * order with the right default-elision (#107).
 *
 * The [doc] is the part a generator cannot derive and the part worth keeping by hand: why the
 * matrix has the shape it has. Paragraphs are separated by a blank line and re-wrapped to the
 * repo's column width on emit, so it is written here as prose rather than as pre-wrapped lines.
 */
data class CatalogMatrixDeclaration(
  val annotation: String,
  val doc: String,
  val matrix: CatalogVariantMatrix,
)

/**
 * Every generated matrix annotation, in the order `CatalogMatrixAnnotations.kt` declares them.
 *
 * The interaction-state annotations are **not** here: they seed no axis — they tell the renderer to
 * drive real hover, focus or press against the composed node — so they are not a cross product and
 * stay hand-authored in `CatalogInteractionAnnotations.kt`.
 */
val CatalogMatrixDeclarations: List<CatalogMatrixDeclaration> =
  listOf(
    CatalogMatrixDeclaration(
      annotation = "SizeShapeMatrix",
      doc =
        """
        The **button family matrix**: five sizes x two shapes, plus the disabled state beside them.

        Ten cells, and every one of the five common buttons carries exactly these — which is why this annotation exists. Written out per component they were fifty near-identical lines maintained in five places.
        """,
      matrix = CatalogVariantMatrices.SizeShape,
    ),
    CatalogMatrixDeclaration(
      annotation = "IconButtonMatrix",
      doc =
        """
        The **icon button matrix**: five sizes x three widths x two shapes, plus disabled.

        Icon buttons carry a width axis the plain buttons do not, so this is thirty cells rather than ten — a hundred and twenty lines across the four emphases before it was declared once.
        """,
      matrix = CatalogVariantMatrices.IconButton,
    ),
    CatalogMatrixDeclaration(
      annotation = "SelectedToggleButtonMatrix",
      doc =
        """
        The toggle-button matrix for a component **authored selected** (filled, tonal).

        Twenty cells: five sizes x two shapes x two selected states, plus disabled. The unsuffixed cells are the selected ones, so the cells that turn it off are named `-off` — which is why there are two of these annotations rather than one. See [UnselectedToggleButtonMatrix].
        """,
      matrix = CatalogVariantMatrices.toggleButton(selectedByDefault = true),
    ),
    CatalogMatrixDeclaration(
      annotation = "UnselectedToggleButtonMatrix",
      doc =
        """
        The toggle-button matrix for a component **authored unselected** (outlined, elevated).

        The mirror of [SelectedToggleButtonMatrix]: same twenty cells, but the unsuffixed ones are the unselected renders and the cells that turn it on are named `-on`. The default a component was authored in decides which of the two it carries, because that default is what the sticker's `catalogToggleSelected(default = …)` passes and what the naming is relative to.
        """,
      matrix = CatalogVariantMatrices.toggleButton(selectedByDefault = false),
    ),
    CatalogMatrixDeclaration(
      annotation = "SliderSizeMatrix",
      doc =
        """
        The **slider matrix**: the size axis alone, four cells.

        Its base is extra small rather than small — the size a bare `Slider(...)` is, and the first variant of the kit's slider sets — so `s` is a cell here where it is the unnamed base everywhere else. Nothing is crossed into it: the size carries the track's corner, and the `steps`, `track` and `status` knobs the slider stickers already had stay beside it as single-axis variants.
        """,
      matrix = CatalogVariantMatrices.SliderSize,
    ),
    CatalogMatrixDeclaration(
      annotation = "TypeScaleMatrix",
      doc =
        """
        The **type scale matrix**: the fifteen roles of [TypeScaleRole], fourteen cells beside the base render.

        One axis and no cross product, so the cells are the roles themselves. They were hand-typed beside a `when` over the same fifteen literals and a second list of display labels, with nothing relating the three (#103); the enum now carries all of it.
        """,
      matrix = CatalogVariantMatrices.TypeScale,
    ),
    CatalogMatrixDeclaration(
      annotation = "CornerScaleMatrix",
      doc =
        """
        The **corner scale matrix**: the ten radius tokens of [CornerScaleToken], nine cells beside the base render.

        `none` is the base — an unseeded specimen draws the unrounded box — so the nine cells are the tokens that round it, up to the 50% corner `full` names.
        """,
      matrix = CatalogVariantMatrices.CornerScale,
    ),
    CatalogMatrixDeclaration(
      annotation = "ColorSchemeMatrix",
      doc =
        """
        The **colour mode matrix**: the six schemes of [CatalogSchemeChoice], five cells beside the base render.

        Light and dark at standard, medium and high contrast — the kit's own six modes. The base render is the standard light scheme, so the cells are the other five.
        """,
      matrix = CatalogVariantMatrices.ColorScheme,
    ),
  )
