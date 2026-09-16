# Patches for the vendored foundation samples

Empty, and that is the goal state rather than an oversight.

The vendored tree under `../src/main/kotlin/upstream/` is upstream's bytes, re-fetched
byte-identically on every import. A fix is a patch here — never an edit in place — with its reason
in its own header, so the next import re-applies it and a patch that stops applying **fails** the
import rather than silently reverting the fix it carried.

Prefer a patch to a quarantine entry wherever the fix is small enough to state: quarantine is
per file, so it takes out every sample in the file, including the ones that render perfectly well.
See `samples/patches/README.md` for the two the material3 corpus needed and how they are written.
