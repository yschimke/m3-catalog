package ee.schimke.m3catalog

import androidx.compose.runtime.Composable
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice

/**
 * The catalog's **variant axes**, in one place and in two forms.
 *
 * A variant matrix is the cross product of a few named axes — five sizes by two shapes, or five
 * sizes by three widths by two shapes — and the catalog expresses each cell as an
 * `@OverrideVariant` seeding the sticker's knobs. That leaves the same axis spelled out twice per
 * cell, in two unrelated places: the seed (`strings = ["size=xs", "shape=square"]`) and the
 * variant's name (`"xs-square"`). Nothing checks that they agree, and they have already drifted —
 * `IconButtons.kt` spells the default size explicitly in `s-narrow` and implicitly in `s-square`.
 *
 * This file is the single declaration those two forms are derived from:
 *
 * * [CatalogKnobAxis] is the **runtime** form. A sticker reads its size or width off the axis and
 *   gets an enum back, so nothing downstream compares knob strings by hand. Replaces the
 *   `previewOverrideString(...)` → `when (raw) { "narrow" -> …` round trip the resolvers used to
 *   do.
 * * [CatalogVariantAxis] is the **declarative** form, and [CatalogVariantMatrix] expands a list of
 *   them into the exact set of cells a component's `@OverrideVariant` block should carry — names
 *   and seeds both. `CatalogVariantMatrixTest` holds the authored annotations to it, so a size
 *   added to [CatalogSize] fails the build until every matrix that uses it carries its cells.
 *
 * The naming rule the expansion reproduces is the one already authored by hand, and it is not quite
 * "name the non-default values". **Size names itself on every cell, default included** —
 * `s-square`, `s-off` — while shape, width and selected appear only when they are off their
 * default. That is deliberate rather than an accident of authoring: size is the primary axis of the
 * kit's component sets, and a cell called `square` with no size in it reads as a shape variant of
 * nothing. See [CatalogVariantAxis.namesEveryValue].
 *
 * The cell whose every axis sits at its default is not a variant at all — it is the base render,
 * which is why an unseeded sticker stays byte-identical to what it published before its matrix
 * arrived. Note that this cell still has a non-empty *name* under the rule above (`s`), so the
 * expansion skips it on **seeds** being empty, not on the name being empty.
 */
interface CatalogKnob {
  /** This value's spelling in a `previewOverride*` seed, and the slug it contributes to a name. */
  val knob: String
}

/**
 * A **typed** knob axis: the values [E] may take, the one an unseeded render is, and the knob key
 * that carries it.
 *
 * [current] is what a sticker calls. It reads the knob as a string — that is the wire format the
 * renderer seeds through — and resolves it back to an enum entry, falling back to [default] rather
 * than throwing, so an unrecognised seed degrades to the published default instead of failing a
 * render mid-sheet.
 *
 * It reads it as a **closed** value set ([previewOverrideChoice]) rather than free text, so the
 * declared alphabet travels with the render in `previews/<id>.overrides.json` and a viewer offers
 * the axis as a picker. That is the difference between a size knob showing a text field reading `s`
 * — correct, and no help in discovering that `xs` / `m` / `l` / `xl` exist — and showing the five
 * values the enum already knows about. Nothing is enforced at render time: [current] still resolves
 * an off-set seed the same way it always did, by falling back to [default], so an old link keeps
 * behaving exactly as it did before the set was declared.
 */
class CatalogKnobAxis<E>(val key: String, val values: List<E>, val default: E)
  where E : Enum<E>, E : CatalogKnob {

  /**
   * The axis as the renderer publishes it: every value, labelled, in declaration order.
   *
   * Held rather than rebuilt, because [current] runs on every render of every cell and the set is
   * fixed the moment the axis is constructed.
   */
  private val options: List<PreviewOverrideOption> = values.map {
    PreviewOverrideOption(value = it.knob, label = labelFor(it))
  }

  /** The value this render should use: the [key] knob, resolved to [E], defaulting to [default]. */
  @Composable
  fun current(): E {
    val raw = previewOverrideChoice(key, default.knob, options)
    return values.firstOrNull { it.knob == raw } ?: default
  }

  /** This axis as a [CatalogVariantAxis], for the matrix expansion and the test that pins it. */
  fun declaration(namesEveryValue: Boolean = false): CatalogVariantAxis =
    CatalogVariantAxis(
      key = key,
      values = values.map { CatalogAxisValue(seed = it.knob, slug = it.knob) },
      default = default.knob,
      kind = CatalogSeedKind.STRING,
      namesEveryValue = namesEveryValue,
    )
}

/**
 * A picker label for one axis value: its enum entry name, spaced and sentence-cased.
 *
 * `ExtraSmall` reads `Extra small`, `Round` reads `Round`. The label is the only part of the option
 * a viewer shows; the **wire value stays [CatalogKnob.knob]** — the slug the sticker resolves, the
 * seeds spell, and the published variant names carry — so labelling an axis moves no render and
 * breaks no link. Derived rather than authored because the enum entry already says it: a separate
 * `label = "Extra small"` beside `ExtraSmall` is one more pair of strings that can disagree, which
 * is the failure this file exists to remove.
 */
private fun <E> labelFor(value: E): String where E : Enum<E>, E : CatalogKnob {
  val spaced = value.name.replace(Regex("(?<!^)(?=\\p{Lu})"), " ")
  return spaced.take(1) + spaced.drop(1).lowercase()
}

/** How an axis's value travels in an `@OverrideVariant` — which array the seed lives in. */
enum class CatalogSeedKind {
  /** `strings = ["key=value"]`. */
  STRING,
  /** `booleans = ["key=true"]`. */
  BOOLEAN,
}

/**
 * One value of an axis: what a seed spells it as, and the slug it contributes to a variant name.
 *
 * The two differ only where the seed is not a human-facing word — the toggle-button `selected` axis
 * seeds `true` / `false` but names itself `on` / `off`.
 */
data class CatalogAxisValue(val seed: String, val slug: String)

/**
 * An axis in the form the matrix expansion reads: key, values, the default, and the seed's type.
 */
data class CatalogVariantAxis(
  val key: String,
  val values: List<CatalogAxisValue>,
  /**
   * The [CatalogAxisValue.seed] an unseeded render resolves to; its cells drop the key entirely.
   */
  val default: String,
  val kind: CatalogSeedKind,
  /**
   * Whether every cell's name carries this axis's slug, or only the cells that are off [default].
   *
   * True for size and false for everything else, reproducing the authored convention: `s-square`
   * spells the default size, `xs` omits the default shape. A cell is still never *seeded* with a
   * default value on any axis — that would be a no-op — so this affects the name alone.
   */
  val namesEveryValue: Boolean = false,
)

/**
 * One cell of a matrix: the `@OverrideVariant` a component should carry for this combination.
 *
 * [strings] and [booleans] hold only the **non-default** axes, matching what the annotations were
 * authored to do — seeding a knob with its own default is a no-op the renderer resolves to the same
 * pixels, so a cell names only what actually moves.
 */
data class CatalogVariantCell(
  val name: String,
  val strings: Map<String, String>,
  val booleans: Map<String, String>,
)

/**
 * The cross product of [axes], minus the all-defaults cell.
 *
 * Cells come out in axis-major order (first axis varies slowest), which is the order the hand-typed
 * blocks were already written in for the size/shape matrices. Order is not load-bearing — the
 * variant's name is its identity — but keeping it stable makes the generated form diff cleanly
 * against what it replaces.
 */
class CatalogVariantMatrix(
  val axes: List<CatalogVariantAxis>,
  /**
   * Axes carried **beside** the cross product rather than multiplied into it — the T-shape.
   *
   * Each contributes one cell per non-default value, holding every other axis at its default, so
   * `state` adds a single `disabled` cell rather than doubling the matrix. That is the rule the
   * catalog compares by: every value of every axis gets a render, no combination of two of them
   * does. Crossing `state` into the buttons' size x shape would turn nine cells into eighteen and
   * say nothing the nine plus one do not — a disabled extra-large square button tells you what
   * `disabled` and `xl-square` already told you separately.
   *
   * They are a separate list rather than another entry in [axes] because the difference is not
   * cosmetic: [axes] multiply, these add.
   */
  val alongside: List<CatalogVariantAxis> = emptyList(),
) {

  val cells: List<CatalogVariantCell> = expand() + beside()

  /** One cell per non-default value of each [alongside] axis; see its KDoc for why not crossed. */
  private fun beside(): List<CatalogVariantCell> = alongside.flatMap { axis ->
    axis.values
      .filter { it.seed != axis.default }
      .map { value ->
        val seeds = mapOf(axis.key to value.seed)
        when (axis.kind) {
          CatalogSeedKind.STRING -> CatalogVariantCell(value.slug, seeds, emptyMap())
          CatalogSeedKind.BOOLEAN -> CatalogVariantCell(value.slug, emptyMap(), seeds)
        }
      }
  }

  private fun expand(): List<CatalogVariantCell> {
    var combinations: List<List<CatalogAxisValue>> = listOf(emptyList())
    for (axis in axes) {
      combinations = combinations.flatMap { prefix -> axis.values.map { prefix + it } }
    }
    return combinations.mapNotNull { combination ->
      val slugs = mutableListOf<String>()
      val strings = mutableMapOf<String, String>()
      val booleans = mutableMapOf<String, String>()
      for ((axis, value) in axes.zip(combination)) {
        val isDefault = value.seed == axis.default
        if (axis.namesEveryValue || !isDefault) slugs.add(value.slug)
        // Never seed a knob with its own default: the renderer resolves an unseeded knob to exactly
        // that value, so the seed would be a no-op that only makes the annotation longer.
        if (isDefault) continue
        when (axis.kind) {
          CatalogSeedKind.STRING -> strings[axis.key] = value.seed
          CatalogSeedKind.BOOLEAN -> booleans[axis.key] = value.seed
        }
      }
      // The all-defaults combination is the base render, not a variant — emitting it would bake a
      // second capture identical to the sticker. Detected on the seeds rather than the name,
      // because `namesEveryValue` leaves that cell with a name (`s`) even though it moves nothing.
      if (strings.isEmpty() && booleans.isEmpty()) null
      else CatalogVariantCell(slugs.joinToString("-"), strings.toMap(), booleans.toMap())
    }
  }
}

// --- The shape and width axes --------------------------------------------------------------------
//
// `CatalogSize` lives in `CatalogSizes.kt` beside the per-size dimensions it carries; these two are
// pure axes with no dimensions of their own, so they sit here with the axis machinery.

/**
 * The expressive **shape** axis: round (the default) or square.
 *
 * Was a bare `previewOverrideString("shape", "round") == "square"` comparison in four separate
 * resolvers, each of which had to spell both literals correctly to agree with the others.
 */
enum class CatalogShape(override val knob: String) : CatalogKnob {
  Round("round"),
  Square("square");

  companion object {
    val Axis = CatalogKnobAxis("shape", entries, Round)
  }
}

/**
 * The **state** axis, which the kit publishes for every button-family component.
 *
 * Only two of its values are here. The kit's `State` also carries `Hovered`, `Focused` and
 * `Pressed`, and a static `@Preview` cannot reach any of them — they need an input device — so
 * declaring them would expand every matrix with cells no render can fill, which is the failure this
 * file exists to prevent, pointed the other way. `Disabled` is the one that is reachable, and
 * leaving it undeclared is what left twelve components comparing against a kit variant with no
 * candidate render.
 *
 * Keyed `state` rather than a boolean `enabled`: the kit's axis IS `State`, so the knob spells what
 * the design map has to resolve against. Seeding `enabled=false` first resolved nothing at all —
 * the resolver refusing a plausible-but-wrong translation, working as intended.
 */
enum class CatalogState(override val knob: String) : CatalogKnob {
  Enabled("enabled"),
  Disabled("disabled");

  companion object {
    val Axis = CatalogKnobAxis("state", entries, Enabled)
  }
}

/**
 * The icon button **width** axis, which the plain buttons do not carry: an icon button's container
 * can be narrower or wider than uniform at the same size.
 */
enum class CatalogIconWidth(override val knob: String) : CatalogKnob {
  Narrow("narrow"),
  Uniform("uniform"),
  Wide("wide");

  companion object {
    val Axis = CatalogKnobAxis("width", entries, Uniform)
  }
}

/**
 * The toggle-button **selected** axis.
 *
 * A boolean knob rather than a string one, because that is what `catalogToggleSelected` reads and
 * what the viewer offers as a checkbox — so the axis declares [CatalogSeedKind.BOOLEAN] and maps
 * its `true` / `false` seeds onto the `on` / `off` slugs the variant names already use.
 *
 * Its default is **per component**, not per catalog: the filled and tonal toggles were authored
 * selected and the outlined and elevated ones unselected, so the axis is built with the component's
 * own default and the unsuffixed cells are whichever state that is.
 */
fun catalogSelectedAxis(selectedByDefault: Boolean): CatalogVariantAxis =
  CatalogVariantAxis(
    key = "selected",
    values = listOf(CatalogAxisValue("true", "on"), CatalogAxisValue("false", "off")),
    default = selectedByDefault.toString(),
    kind = CatalogSeedKind.BOOLEAN,
  )

/**
 * The three matrices the catalog actually fans out, named once so the annotations that spell them
 * out and the test that checks those annotations read from the same declaration.
 */
object CatalogVariantMatrices {

  /** Size names itself on every cell; see the file KDoc for why it is the one axis that does. */
  private val size = CatalogSize.Axis.declaration(namesEveryValue = true)

  /** Carried beside every button-family matrix, never crossed into it. */
  private val state = listOf(CatalogState.Axis.declaration())

  /** Buttons: five sizes x two shapes, plus disabled. Ten cells. */
  val SizeShape =
    CatalogVariantMatrix(listOf(size, CatalogShape.Axis.declaration()), alongside = state)

  /** Icon buttons: five sizes x three widths x two shapes, plus disabled. Thirty cells. */
  val IconButton =
    CatalogVariantMatrix(
      listOf(size, CatalogIconWidth.Axis.declaration(), CatalogShape.Axis.declaration()),
      alongside = state,
    )

  /** Toggle buttons: [SizeShape] plus the selected axis, plus disabled. Twenty cells. */
  fun toggleButton(selectedByDefault: Boolean) =
    CatalogVariantMatrix(
      listOf(size, CatalogShape.Axis.declaration(), catalogSelectedAxis(selectedByDefault)),
      alongside = state,
    )
}
