# Glimmer transparent capture evidence

`after.png` is `ButtonSticker`, rendered with:

```shell
./gradlew :glimmer-catalog:composePreviewRender -PcomposePreview.filter=ButtonSticker
```

The PNG retains an alpha channel and transparent pixels outside the Glimmer surface. The previous
published capture is commit `7db889b53125c7f231ca267c0e5bbe35e4ce193b` on
`design-artifacts/glimmer-catalog`, at `images/button/ideal__default.png`; it painted a full black
rectangle because every Glimmer sticker requested an opaque black preview background.
