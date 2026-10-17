package ir.iran.screenreader
import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
class BarcodeEngine{fun read(b:Bitmap,ok:(List<String>)->Unit,fail:(Exception)->Unit){BarcodeScanning.getClient().process(InputImage.fromBitmap(b,0)).addOnSuccessListener{xs->ok(xs.mapNotNull{it.rawValue})}.addOnFailureListener{fail(it)}}}
