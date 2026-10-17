package ir.iran.screenreader
import android.view.accessibility.AccessibilityNodeInfo
object ReadingController {
    fun nodeLabel(n: AccessibilityNodeInfo): String {
        val parts = linkedSetOf<String>()
        fun add(v: CharSequence?) { v?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let(parts::add) }
        add(n.contentDescription); add(n.text)
        if (android.os.Build.VERSION.SDK_INT >= 26) add(n.hintText)
        if (parts.isEmpty()) n.viewIdResourceName?.substringAfterLast(':')?.takeIf { it.isNotBlank() }?.let(parts::add)
        if (n.isCheckable) add(if (n.isChecked) "\u0627\u0646\u062a\u062e\u0627\u0628 \u0634\u062f\u0647" else "\u0627\u0646\u062a\u062e\u0627\u0628 \u0646\u0634\u062f\u0647")
        if (!n.isEnabled) add("\u063a\u06cc\u0631\u0641\u0639\u0627\u0644")
        return parts.joinToString("\u060c ")
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
    fun isLikelyVisualApp(n: AccessibilityNodeInfo): Boolean {
        var texty = 0; var total = 0
        fun walk(x: AccessibilityNodeInfo) {
            total++; if (!x.text.isNullOrBlank() || !x.contentDescription.isNullOrBlank()) texty++
            for (i in 0 until x.childCount) { val c = x.getChild(i) ?: continue; walk(c); c.recycle() }
        }
        walk(n); return total > 0 && texty * 5 < total
    }
}
