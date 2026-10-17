package ir.iran.screenreader
import java.util.Locale
object LanguageRouter {
    private val fa = Regex("[\u06AF\u0686\u067E\u0698]")
    fun localeFor(text: String): Locale = when {
        fa.containsMatchIn(text) -> Locale("fa", "IR")
        Regex("[\u0600-\u06FF]").containsMatchIn(text) -> Locale("fa", "IR")
        else -> Locale.ENGLISH
    }
}
