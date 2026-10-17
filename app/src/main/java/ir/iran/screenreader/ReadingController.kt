package ir.iran.screenreader
import android.view.accessibility.AccessibilityNodeInfo
object ReadingController {
    fun nodeLabel(n: AccessibilityNodeInfo): String {
        val parts = linkedSetOf<String>()
        fun add(v: CharSequence?) { v?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let(parts::add) }
        add(n.contentDescription); add(n.text)
        if (android.os.Build.VERSION.SDK_INT >= 26) add(n.hintText)
        if (parts.isEmpty()) n.viewIdResourceName?.substringAfterLast(':')?.takeIf { it.isNotBlank() }?.let(parts::add)
        if (n.isCheckable) add(if (n.isChecked) "انتخاب شده" else "انتخاب نشده")
        if (!n.isEnabled) add("غیرفعال")
        return parts.joinToString("، ")
    }
    fun focusedNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (root.isAccessibilityFocused || root.isFocused) return AccessibilityNodeInfo.obtain(root)
        for (i in 0 until root.childCount) {
            val c = root.getChild(i) ?: continue
            val f = focusedNode(c); c.recycle()
            if (f != null) return f
        }
        return null
    }
}
