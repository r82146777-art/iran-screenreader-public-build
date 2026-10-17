package ir.iran.screenreader
import android.content.Context
object PronunciationStore {
    private const val P = "iran_pronunciation"
    fun put(c: Context, from: String, to: String) = c.getSharedPreferences(P, 0).edit().putString(from, to).apply()
    fun apply(c: Context, text: String): String {
        var out = text
        for ((k, v) in c.getSharedPreferences(P, 0).all)
            if (k.isNotBlank() && v is String) out = out.replace(k, v)
        return out
    }
}
