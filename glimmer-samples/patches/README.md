# Glimmer sample patches

Small fixes to the vendored AndroidX Glimmer samples, applied by `scripts/import-samples.mjs` after
the copy. The contract is `samples/patches/README.md`'s, verbatim: a fix is **a patch with a stated
reason, never an edit to a vendored file**, and a patch that stops applying fails the import rather
than disappearing.

Cut one against a fresh import:

```
node scripts/import-samples.mjs --manifest glimmer-samples/import.json \
  --quarantine glimmer-samples/quarantine.json --patches glimmer-samples/patches \
  --out /tmp/glimmer-fresh
```

Empty so far, and worth keeping that way: unlike the material3 corpus, these samples import
`androidx.xr.glimmer` and little else, and this module renders on Android — the platform they were
written for.
