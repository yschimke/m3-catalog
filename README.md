# FAB icon size — rendered evidence

Backing the visual evidence in the FAB icon-size pull request
(`agent/fab-icon-size`), held on its own branch so the images have a stable URL
without carrying binaries into `main`.

* `fab-icon.png` — before / after for both stickers.
* `renders/` — the individual `@Preview` PNGs on each side.

Measured from the opaque container in each render: the standard FAB is 56x56 on
both sides, and the extended FAB goes from a pinned 104x56 to a self-sized
107x56.
