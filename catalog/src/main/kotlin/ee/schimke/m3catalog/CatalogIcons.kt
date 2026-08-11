package ee.schimke.m3catalog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** The kit's selected `stars` placeholder: a filled circle with a star knocked out. */
val CatalogFilledStars: ImageVector by lazy {
  figmaStars(
    name = "CatalogFilledStars",
    pathData =
      "M6 16L10 12.95L14 16L12.5 11.05L16.5 8.2H11.6L10 3L8.4 8.2H3.5" +
        "L7.5 11.05L6 16ZM10 20C8.61667 20 7.31667 19.7375 6.1 19.2125" +
        "C4.88333 18.6875 3.825 17.975 2.925 17.075C2.025 16.175 1.3125 15.1167" +
        " 0.7875 13.9C0.2625 12.6833 0 11.3833 0 10C0 8.61667 0.2625 7.31667" +
        " 0.7875 6.1C1.3125 4.88333 2.025 3.825 2.925 2.925C3.825 2.025 4.88333" +
        " 1.3125 6.1 0.7875C7.31667 0.2625 8.61667 0 10 0C11.3833 0 12.6833" +
        " 0.2625 13.9 0.7875C15.1167 1.3125 16.175 2.025 17.075 2.925C17.975" +
        " 3.825 18.6875 4.88333 19.2125 6.1C19.7375 7.31667 20 8.61667 20 10" +
        "C20 11.3833 19.7375 12.6833 19.2125 13.9C18.6875 15.1167 17.975 16.175" +
        " 17.075 17.075C16.175 17.975 15.1167 18.6875 13.9 19.2125C12.6833" +
        " 19.7375 11.3833 20 10 20Z",
    scale = 1f,
    translation = 2f,
  )
}

/** The kit's unselected `stars` placeholder: an outlined circle with a filled star. */
val CatalogOutlinedStars: ImageVector by lazy {
  figmaStars(
    name = "CatalogOutlinedStars",
    pathData =
      "M5 13.3333L8.33333 10.7917L11.6667 13.3333L10.4167 9.20833L13.75" +
        " 6.83333H9.66667L8.33333 2.5L7 6.83333H2.91667L6.25 9.20833L5 13.3333Z" +
        "M8.33333 16.6667C7.18056 16.6667 6.09722 16.4479 5.08333 16.0104" +
        "C4.06944 15.5729 3.1875 14.9792 2.4375 14.2292C1.6875 13.4792 1.09375" +
        " 12.5972 0.65625 11.5833C0.21875 10.5694 0 9.48611 0 8.33333C0 7.18056" +
        " 0.21875 6.09722 0.65625 5.08333C1.09375 4.06944 1.6875 3.1875 2.4375" +
        " 2.4375C3.1875 1.6875 4.06944 1.09375 5.08333 0.65625C6.09722 0.21875" +
        " 7.18056 0 8.33333 0C9.48611 0 10.5694 0.21875 11.5833 0.65625C12.5972" +
        " 1.09375 13.4792 1.6875 14.2292 2.4375C14.9792 3.1875 15.5729 4.06944" +
        " 16.0104 5.08333C16.4479 6.09722 16.6667 7.18056 16.6667 8.33333" +
        "C16.6667 9.48611 16.4479 10.5694 16.0104 11.5833C15.5729 12.5972" +
        " 14.9792 13.4792 14.2292 14.2292C13.4792 14.9792 12.5972 15.5729" +
        " 11.5833 16.0104C10.5694 16.4479 9.48611 16.6667 8.33333 16.6667Z" +
        "M8.33333 15C10.1944 15 11.7708 14.3542 13.0625 13.0625C14.3542 11.7708" +
        " 15 10.1944 15 8.33333C15 6.47222 14.3542 4.89583 13.0625 3.60417" +
        "C11.7708 2.3125 10.1944 1.66667 8.33333 1.66667C6.47222 1.66667 4.89583" +
        " 2.3125 3.60417 3.60417C2.3125 4.89583 1.66667 6.47222 1.66667 8.33333" +
        "C1.66667 10.1944 2.3125 11.7708 3.60417 13.0625C4.89583 14.3542 6.47222" +
        " 15 8.33333 15Z",
    scale = 1.2f,
    translation = 2f,
  )
}

private fun figmaStars(
  name: String,
  pathData: String,
  scale: Float,
  translation: Float,
): ImageVector =
  ImageVector.Builder(
      name = name,
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
    )
    .apply {
      addGroup(
        scaleX = scale,
        scaleY = scale,
        translationX = translation,
        translationY = translation,
      )
      addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill = SolidColor(Color.Black),
      )
      clearGroup()
    }
    .build()
