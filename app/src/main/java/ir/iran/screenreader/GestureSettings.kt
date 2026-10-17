package ir.iran.screenreader

import android.accessibilityservice.AccessibilityService
import android.content.Context

object GestureSettings {
    private const val P = "iran_gestures_v5"

    enum class Action(val id: String, val title: String) {
        NEXT("next", "عنصر بعدی"),
        PREV("prev", "عنصر قبلی"),
        READ("read", "خواندن عنصر"),
        ACTIVATE("activate", "فعال‌سازی"),
        MENU("menu", "منوی اصلی"),
        STOP("stop", "توقف گفتار"),
        HOME("home", "خانه"),
        BACK("back", "بازگشت"),
        NOTIFICATIONS("notifications", "اعلان‌ها"),
        RECENTS("recents", "برنامه‌های اخیر"),
        GRANULARITY("granularity", "تغییر دانه‌بندی"),
        NONE("none", "هیچ")
    }

    private val defaults = mapOf(
        "swipe_left" to Action.NEXT.id,
        "swipe_right" to Action.PREV.id,
        "swipe_up" to Action.READ.id,
        "swipe_down" to Action.GRANULARITY.id,
        "swipe_up_down" to Action.MENU.id,
        "two_finger_down" to Action.MENU.id,
        "two_finger_up" to Action.STOP.id,
        "two_finger_left" to Action.BACK.id,
        "two_finger_right" to Action.RECENTS.id,
        "three_finger_up" to Action.HOME.id,
        "three_finger_down" to Action.NOTIFICATIONS.id
    )

    fun keyForGestureId(gestureId: Int): String? = when (gestureId) {
        AccessibilityService.GESTURE_SWIPE_LEFT -> "swipe_left"
        AccessibilityService.GESTURE_SWIPE_RIGHT -> "swipe_right"
        AccessibilityService.GESTURE_SWIPE_UP -> "swipe_up"
        AccessibilityService.GESTURE_SWIPE_DOWN -> "swipe_down"
        AccessibilityService.GESTURE_SWIPE_UP_AND_DOWN -> "swipe_up_down"
        AccessibilityService.GESTURE_2_FINGER_SWIPE_DOWN -> "two_finger_down"
        AccessibilityService.GESTURE_2_FINGER_SWIPE_UP -> "two_finger_up"
        AccessibilityService.GESTURE_2_FINGER_SWIPE_LEFT -> "two_finger_left"
        AccessibilityService.GESTURE_2_FINGER_SWIPE_RIGHT -> "two_finger_right"
        AccessibilityService.GESTURE_3_FINGER_SWIPE_UP -> "three_finger_up"
        AccessibilityService.GESTURE_3_FINGER_SWIPE_DOWN -> "three_finger_down"
        else -> null
    }

    fun gestureLabels(): List<Pair<String, String>> = listOf(
        "swipe_left" to "کشیدن راست ← چپ",
        "swipe_right" to "کشیدن چپ → راست",
        "swipe_up" to "کشیدن به بالا",
        "swipe_down" to "کشیدن به پایین",
        "swipe_up_down" to "بالا سپس پایین (شبیه L)",
        "two_finger_down" to "دو انگشت به پایین (منوی اصلی)",
        "two_finger_up" to "دو انگشت به بالا",
        "two_finger_left" to "دو انگشت به چپ",
        "two_finger_right" to "دو انگشت به راست",
        "three_finger_up" to "سه انگشت به بالا",
        "three_finger_down" to "سه انگشت به پایین"
    )

    fun getAction(c: Context, key: String): Action {
        val id = c.getSharedPreferences(P, 0).getString(key, defaults[key] ?: Action.NONE.id) ?: Action.NONE.id
        return Action.entries.find { it.id == id } ?: Action.NONE
    }

    fun setAction(c: Context, key: String, action: Action) {
        c.getSharedPreferences(P, 0).edit().putString(key, action.id).apply()
    }
}
