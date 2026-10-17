package ir.iran.screenreader

import android.content.Context

object HistoryStore {
    private const val P = "iran_reader_history"
    fun add(c: Context, text: String) {
        if (text.isBlank()) return
        val p = c.getSharedPreferences(P, Context.MODE_PRIVATE)
        val old = p.getStringSet("items", emptySet()).orEmpty().toMutableList()
        old.remove(text); old.add(0, text); while (old.size > 100) old.removeAt(old.lastIndex)
        p.edit().putStringSet("items", old.toSet()).apply()
    }
    fun all(c: Context): List<String> =
        c.getSharedPreferences(P, Context.MODE_PRIVATE).getStringSet("items", emptySet()).orEmpty().toList()
}
