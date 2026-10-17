package ir.iran.screenreader
import android.view.accessibility.AccessibilityNodeInfo
object FocusNavigator {
    enum class Granularity { CHARACTER, WORD, LINE, PARAGRAPH, CONTROL }
    private var nodes: MutableList<AccessibilityNodeInfo> = mutableListOf()
    private var index = -1
    private var granularity = Granularity.CONTROL
    fun setGranularity(g: Granularity) { granularity = g }
    fun getGranularity(): Granularity = granularity
    fun cycleGranularity(): Granularity {
        granularity = when (granularity) {
            Granularity.CONTROL -> Granularity.CHARACTER
            Granularity.CHARACTER -> Granularity.WORD
            Granularity.WORD -> Granularity.LINE
            Granularity.LINE -> Granularity.PARAGRAPH
            Granularity.PARAGRAPH -> Granularity.CONTROL
        }
        return granularity
    }
    fun rebuild(root: AccessibilityNodeInfo?) {
        clear(); if (root == null) return; collect(root); index = if (nodes.isEmpty()) -1 else 0
    }
    private fun collect(n: AccessibilityNodeInfo) {
        val label = ReadingController.nodeLabel(n)
        val useful = label.isNotBlank() || n.isClickable || n.isCheckable || n.isEditable || n.isFocusable
        if (useful && n.isVisibleToUser) nodes.add(AccessibilityNodeInfo.obtain(n))
        for (i in 0 until n.childCount) { val c = n.getChild(i) ?: continue; collect(c); c.recycle() }
    }
    fun clear() { nodes.forEach { try { it.recycle() } catch (_: Exception) {} }; nodes.clear(); index = -1 }
    fun current(): AccessibilityNodeInfo? = if (index in nodes.indices) nodes[index] else null
    fun next(): AccessibilityNodeInfo? { if (nodes.isEmpty()) return null; index = (index + 1).coerceAtMost(nodes.lastIndex); return current() }
    fun prev(): AccessibilityNodeInfo? { if (nodes.isEmpty()) return null; index = (index - 1).coerceAtLeast(0); return current() }
    fun first(): AccessibilityNodeInfo? { if (nodes.isEmpty()) return null; index = 0; return current() }
    fun size(): Int = nodes.size
    fun speakCurrent(speak: (String) -> Unit) {
        val n = current()
        if (n == null) { speak(EarconFeedback.EMPTY); return }
        val label = ReadingController.nodeLabel(n)
        speak(label.ifBlank { EarconFeedback.EMPTY })
    }
    fun activate(): Boolean {
        val n = current() ?: return false
        return n.performAction(AccessibilityNodeInfo.ACTION_CLICK) || n.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
    }
}
