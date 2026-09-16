package androidx.annotation

/**
 * A local stand-in for AndroidX's `@Sampled`, which **no published artifact provides**.
 *
 * The same declaration `:samples-catalog` carries, for the same reason and with the same rule: it
 * keeps the vendored tree byte-identical to upstream, because the alternative is rewriting every
 * imported file to strip an annotation and making each one a modified file forever. `Sampled` is an
 * AOSP-internal marker the docs tooling reads to pair a `@sample` tag with the function it names,
 * and it is deliberately not shipped — `androidx.annotation:annotation` does not contain it and
 * `androidx.annotation:annotation-sampled` does not exist on Google Maven.
 *
 * Declared per module rather than shared: these two modules have no source set in common (one is
 * material3, one is foundation, and both compile upstream's bytes under upstream's packages), and a
 * module depending on another module's `androidx.annotation` package to compile upstream code would
 * be a worse coupling than eight lines of annotation.
 *
 * It carries no behaviour and nothing reads it at runtime — `scripts/samples-previews.mjs` reads
 * the annotation from **source text**, not from the classpath. If AndroidX ever publishes the real
 * one, delete this file in both modules.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Sampled
