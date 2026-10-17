package ir.iran.screenreader

import android.graphics.Bitmap

/** Stub — ML Kit barcode not bundled in this build. */
class BarcodeEngine {
    fun read(b: Bitmap, ok: (List<String>) -> Unit, fail: (Exception) -> Unit) {
        fail(Exception("Barcode scanning not available in this build"))
    }
}
