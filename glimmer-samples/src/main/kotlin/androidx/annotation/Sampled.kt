package androidx.annotation

/**
 * A local stand-in for AndroidX's `@Sampled`, which **no published artifact provides**.
 *
 * The second copy of `samples-catalog/src/main/kotlin/androidx/annotation/Sampled.kt`, and
 * deliberately a copy rather than a shared source set: these are two modules with no common
 * compilation — one Compose Multiplatform desktop, one Android — and a module whose job is to
 * compile vendored upstream bytes should not gain a dependency on a sibling catalog to do it. See
 * that file for the full reasoning; briefly, `Sampled` is an AOSP-internal marker the docs tooling
 * reads, `androidx.annotation:annotation` does not contain it (checked, both the KMP and `-jvm`
 * jars, 1.9.1 through 1.11.0-rc01), and `androidx.annotation:annotation-sampled` does not exist on
 * Google Maven.
 *
 * Declaring it keeps the vendored tree byte-identical to upstream, which stripping the annotation
 * on every import would not.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Sampled
