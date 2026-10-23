package ir.iran.shahrbanoorebuild

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsVitsModelConfig
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Bundled offline neural Persian voice. It does not call Android's system TTS.
 * Model assets are provisioned during the GitHub Actions build.
 */
object NaturalPersianVoice {
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "ShahrbanooNaturalVoice").apply { isDaemon = true }
    }
    @Volatile private var engine: OfflineTts? = null
    @Volatile private var currentTrack: AudioTrack? = null
    private val released = AtomicBoolean(false)

    fun speak(context: Context, text: String, speed: Float = 0.92f) {
        if (released.get() || text.isBlank()) return
        stopPlayback()
        val appContext = context.applicationContext
        executor.execute {
            if (released.get()) return@execute
            try {
                val tts = engine ?: createEngine(appContext).also { engine = it }
                val audio = tts.generate(text, sid = 0, speed = speed.coerceIn(0.7f, 1.25f))
                if (released.get() || audio.samples.isEmpty()) return@execute
                val pcm = ShortArray(audio.samples.size) { i ->
                    (audio.samples[i].coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE).toInt().toShort()
                }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(audio.sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(pcm.size * 2)
                    .build()
                currentTrack = track
                track.write(pcm, 0, pcm.size, AudioTrack.WRITE_BLOCKING)
                if (!released.get() && currentTrack === track) {
                    track.play()
                    while (!released.get() && currentTrack === track &&
                        track.playbackHeadPosition < pcm.size && track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        Thread.sleep(25)
                    }
                }
                if (currentTrack === track) currentTrack = null
                runCatching { track.stop() }
                track.release()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (_: Exception) {
                // Keep the game usable if the neural voice cannot initialize.
            }
        }
    }

    @Synchronized private fun createEngine(context: Context): OfflineTts {
        val root = File(context.filesDir, "shahrbanoo_natural_fa")
        val model = File(root, "model_fa.onnx")
        val tokens = File(root, "tokens_fa.txt")
        val espeak = File(root, "espeak-ng-data")
        if (!model.exists() || !tokens.exists() || !espeak.isDirectory) {
            root.deleteRecursively()
            root.mkdirs()
            copyAsset(context, "tts/model_fa.onnx", model)
            copyAsset(context, "tts/tokens_fa.txt", tokens)
            copyAssetDirectory(context, "tts/espeak-ng-data", espeak)
        }
        require(model.length() > 0L && tokens.length() > 0L && espeak.isDirectory) {
            "Bundled Persian voice assets are missing"
        }
        val vits = OfflineTtsVitsModelConfig(
            model = model.absolutePath,
            lexicon = "",
            tokens = tokens.absolutePath,
            dataDir = espeak.absolutePath,
            noiseScale = 0.667f,
            noiseScaleW = 0.8f,
            lengthScale = 1.0f
        )
        val config = OfflineTtsConfig(
            model = OfflineTtsModelConfig(
                vits = vits,
                numThreads = Runtime.getRuntime().availableProcessors().coerceIn(1, 4),
                debug = false,
                provider = "cpu"
            )
        )
        return OfflineTts(config = config)
    }

    private fun copyAsset(context: Context, assetPath: String, target: File) {
        target.parentFile?.mkdirs()
        context.assets.open(assetPath).use { input ->
            FileOutputStream(target).use { output -> input.copyTo(output) }
        }
    }

    private fun copyAssetDirectory(context: Context, assetPath: String, target: File) {
        val children = context.assets.list(assetPath) ?: emptyArray()
        if (children.isEmpty()) {
            target.parentFile?.mkdirs()
            context.assets.open(assetPath).use { input ->
                FileOutputStream(target).use { output -> input.copyTo(output) }
            }
        } else {
            target.mkdirs()
            for (child in children) copyAssetDirectory(context, "$assetPath/$child", File(target, child))
        }
    }

    fun stopPlayback() {
        val track = currentTrack
        currentTrack = null
        if (track != null) {
            runCatching { track.pause() }
            runCatching { track.flush() }
            runCatching { track.stop() }
            runCatching { track.release() }
        }
    }

    fun release() {
        if (!released.compareAndSet(false, true)) return
        stopPlayback()
        executor.shutdownNow()
        runCatching { engine?.release() }
        engine = null
    }
}
