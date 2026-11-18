package com.mypet.little.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/** صدای ساده تولیدشده با کد (بدون فایل صوتی بیرونی) */
class SoundManager {
    var enabled: Boolean = true

    fun play(freq: Int, ms: Int = 140) {
        if (!enabled) return
        Thread {
            try {
                val sr = 22050
                val n = sr * ms / 1000
                val buf = ShortArray(n)
                for (i in 0 until n) {
                    val env = 1.0 - i.toDouble() / n
                    val sweep = freq * (1.0 + 0.25 * i.toDouble() / n)
                    buf[i] = (sin(2.0 * PI * sweep * i / sr) * 9000.0 * env).toInt().toShort()
                }
                @Suppress("DEPRECATION")
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC, sr, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, n * 2, AudioTrack.MODE_STATIC
                )
                track.write(buf, 0, n)
                track.play()
                Thread.sleep(ms.toLong() + 80L)
                track.release()
            } catch (e: Exception) {
                // اگر صدا کار نکرد، برنامه ادامه می‌ده
            }
        }.start()
    }
}
