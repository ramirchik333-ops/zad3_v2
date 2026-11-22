package com.example.madventuretv.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.example.madventuretv.R
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * РњРёРЅРёРјР°Р»СЊРЅС‹Р№ Р·Р°РіСЂСѓР·С‡РёРє РїРѕСЃС‚РµСЂРѕРІ: СЃРµС‚СЊ + РєСЌС€ РІ РїР°РјСЏС‚Рё + СЃРєСЂСѓРіР»РµРЅРёРµ СѓРіР»РѕРІ.
 * РЎС‚РѕСЂРѕРЅРЅРёРµ Р±РёР±Р»РёРѕС‚РµРєРё (Glide/Coil) РЅРµ РёСЃРїРѕР»СЊР·СѓСЋС‚СЃСЏ.
 */
object ImageLoader {

    private val cache = object : LruCache<String, Bitmap>(
        (Runtime.getRuntime().maxMemory() / 8).toInt()
    ) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    private val executor = Executors.newFixedThreadPool(4)
    private val main = Handler(Looper.getMainLooper())

    fun load(
        url: String?,
        imageView: ImageView,
        radiusDp: Float = 12f,
        aspect: Float = 16f / 9f,
        placeholder: Int = R.drawable.poster_placeholder
    ) {
        imageView.setImageResource(placeholder)
        if (url.isNullOrBlank() || url == "N/A") return

        val key = "$url|$radiusDp|$aspect"
        imageView.tag = key

        val cached = cache.get(key)
        if (cached != null) {
            imageView.setImageDrawable(round(imageView, cached, radiusDp))
            return
        }

        executor.execute {
            val bitmap = download(url) ?: return@execute
            val cropped = crop(bitmap, aspect) ?: bitmap
            main.post {
                cache.put(key, cropped)
                if (imageView.tag == key) {
                    imageView.setImageDrawable(round(imageView, cropped, radiusDp))
                }
            }
        }
    }

    private fun round(view: ImageView, bitmap: Bitmap, radiusDp: Float): RoundedBitmapDrawable {
        val drawable = RoundedBitmapDrawableFactory.create(view.resources, bitmap)
        drawable.cornerRadius = radiusDp * view.resources.displayMetrics.density
        drawable.isCircular = false
        return drawable
    }

    private fun crop(bitmap: Bitmap, aspect: Float): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) return null

        val current = width.toFloat() / height
        if (kotlin.math.abs(current - aspect) < 0.02f) return bitmap

        return if (current > aspect) {
            val newWidth = (height * aspect).toInt().coerceAtLeast(1)
            val left = (width - newWidth) / 2
            Bitmap.createBitmap(bitmap, left, 0, newWidth, height)
        } else {
            val newHeight = (width / aspect).toInt().coerceAtLeast(1)
            val top = (height - newHeight) / 2
            Bitmap.createBitmap(bitmap, 0, top, width, newHeight)
        }
    }

    private fun download(address: String): Bitmap? {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(address).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 15_000
                setRequestProperty("User-Agent", "MadventureTV/1.0 (Android TV)")
            }
            val bytes = connection.inputStream.use { it.readBytes() }

            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

            var sample = 1
            while (bounds.outWidth / sample > 900) sample *= 2

            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}

