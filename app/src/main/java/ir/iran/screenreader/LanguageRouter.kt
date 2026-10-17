package ir.iran.screenreader
import java.util.Locale
object LanguageRouter {
    private val fa = Regex("[\\u06AF\\u0686\\u067E\\u0698\\u0600-\\u06FF]")
    fun localeFor(text: String): Locale =
        if (fa.containsMatchIn(text)) Locale("fa", "IR") else Locale.getDefault()
}
