package ir.iran.screenreader

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeReader {
    fun nowFa(): String = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date())
}
