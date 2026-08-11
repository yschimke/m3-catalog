package ee.schimke.m3catalog

import androidx.compose.ui.text.intl.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr

class CatalogFontCoverageTest {

  @Test
  fun `translated locales select their deterministic bundled face`() {
    val expected =
      mapOf(
        "ar" to "NotoSansArabic.ttf",
        "hi" to "NotoSansDevanagari.ttf",
        "th" to "NotoSansThai.ttf",
        "ja" to "NotoSansJP-Regular.otf",
        "ko" to "NotoSansKR-Regular.otf",
        "zh-CN" to "NotoSansSC-Regular.otf",
        "zh-TW" to "NotoSansTC-Regular.otf",
        "zh-Hant" to "NotoSansTC-Regular.otf",
      )

    for ((tag, resource) in expected) {
      assertEquals(resource, catalogFontResourceFor(Locale(tag)), tag)
    }
    assertNull(catalogFontResourceFor(Locale("en-US")))
    assertNull(catalogFontResourceFor(Locale("ru")))
  }

  @Test
  fun `bundled faces cover time picker copy and localized digits`() {
    val samples =
      mapOf(
        "NotoSansArabic.ttf" to "اختيار الوقت إدخال الوقت إلغاء موافق ٠١٢٣٤٥٦٧٨٩",
        "NotoSansDevanagari.ttf" to "समय चुनें समय डालें रद्द करें ठीक है ०१२३४५६७८९",
        "NotoSansThai.ttf" to "เลือกเวลา ป้อนเวลา ยกเลิก ตกลง ๐๑๒๓๔๕๖๗๘๙",
        "NotoSansJP-Regular.otf" to "時刻を選択 時刻を入力 キャンセル OK 0123456789",
        "NotoSansKR-Regular.otf" to "시간 선택 시간 입력 취소 확인 0123456789",
        "NotoSansSC-Regular.otf" to "选择时间 输入时间 取消 确定 0123456789",
        "NotoSansTC-Regular.otf" to "選取時間 輸入時間 取消 確定 0123456789",
      )

    for ((resource, sample) in samples) {
      val bytes = checkNotNull(javaClass.getResourceAsStream("/fonts/$resource")).readBytes()
      Data.makeFromBytes(bytes).use { data ->
        checkNotNull(FontMgr.default.makeFromData(data)).use { typeface ->
          for (codePoint in sample.codePoints().toArray().filterNot(Character::isWhitespace)) {
            assertNotEquals(
              0,
              typeface.getUTF32Glyph(codePoint).toInt(),
              "$resource has no glyph for ${String(Character.toChars(codePoint))} " +
                "(U+${codePoint.toString(16).uppercase()})",
            )
          }
        }
      }
    }
  }
}
