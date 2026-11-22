package com.example.madventuretv.data

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Р—Р°РіСЂСѓР·РєР° РґР°РЅРЅС‹С… РёР· OMDb API (https://www.omdbapi.com).
 *
 * РЎРµС‚РµРІС‹Рµ Р·Р°РїСЂРѕСЃС‹ РІС‹РїРѕР»РЅСЏСЋС‚СЃСЏ РІ С„РѕРЅРѕРІРѕРј РїСѓР»Рµ РїРѕС‚РѕРєРѕРІ,
 * СЂРµР·СѓР»СЊС‚Р°С‚ РІРѕР·РІСЂР°С‰Р°РµС‚СЃСЏ РІ РіР»Р°РІРЅС‹Р№ РїРѕС‚РѕРє.
 */
object OmdbRepository {

    private val executor = Executors.newFixedThreadPool(3)
    private val main = Handler(Looper.getMainLooper())
    private val cache = ConcurrentHashMap<String, List<Movie>>()

    const val ERROR_API_KEY = "api_key"

    /** РџРѕРґР±РѕСЂРєР° С„РёР»СЊРјРѕРІ РґР»СЏ РєР°РЅР°Р»Р°: РЅРµСЃРєРѕР»СЊРєРѕ Р·Р°РїСЂРѕСЃРѕРІ OMDb СЃРєР»РµРёРІР°СЋС‚СЃСЏ РІ РѕРґРЅСѓ Р»РµРЅС‚Сѓ. */
    fun loadChannel(channel: Channel, callback: (movies: List<Movie>?, error: String?) -> Unit) {
        if (!ApiConfig.hasKey) {
            main.post { callback(DemoCatalog.byChannel(channel), null) }
            return
        }

        val cacheKey = "channel:${channel.title}"
        val cached = cache[cacheKey]
        if (cached != null) {
            main.post { callback(cached, null) }
            return
        }

        executor.execute {
            try {
                val found = LinkedHashMap<String, Movie>()
                for (seed in channel.seeds) {
                    for (movie in searchSync(seed)) {
                        if (movie.id.isNotBlank() && !found.containsKey(movie.id)) {
                            found[movie.id] = movie
                        }
                    }
                }
                val movies = found.values.filter { it.poster.isNotBlank() }.take(24)
                cache[cacheKey] = movies
                main.post { callback(movies, null) }
            } catch (e: Exception) {
                main.post { callback(null, e.message) }
            }
        }
    }

    /** РџРѕРёСЃРє С„РёР»СЊРјР° РїРѕ РЅР°Р·РІР°РЅРёСЋ. */
    fun search(query: String, callback: (movies: List<Movie>?, error: String?) -> Unit) {
        val normalized = query.trim()
        if (!ApiConfig.hasKey) {
            main.post { callback(DemoCatalog.search(normalized), null) }
            return
        }

        val cacheKey = "search:${normalized.lowercase()}"
        val cached = cache[cacheKey]
        if (cached != null) {
            main.post { callback(cached, null) }
            return
        }

        executor.execute {
            try {
                val movies = searchSync(normalized).filter { it.poster.isNotBlank() }
                cache[cacheKey] = movies
                main.post { callback(movies, null) }
            } catch (e: Exception) {
                main.post { callback(null, e.message) }
            }
        }
    }

    /** РџРѕР»РЅР°СЏ РёРЅС„РѕСЂРјР°С†РёСЏ Рѕ С„РёР»СЊРјРµ РґР»СЏ СЌРєСЂР°РЅР°-С„СЂР°РіРјРµРЅС‚Р°. */
    fun loadDetails(movie: Movie, callback: (movie: Movie?, error: String?) -> Unit) {
        if (movie.demo || !ApiConfig.hasKey || movie.id.isBlank()) {
            callback(movie, null)
            return
        }
        executor.execute {
            try {
                val details = detailsSync(movie.id) ?: movie
                main.post { callback(details, null) }
            } catch (e: Exception) {
                main.post { callback(movie, e.message) }
            }
        }
    }

    private fun searchSync(query: String): List<Movie> {
        val url = "${ApiConfig.BASE_URL}?apikey=${ApiConfig.OMDB_API_KEY}" +
                "&s=${encode(query)}&type=movie"
        val json = JSONObject(httpGet(url))
        checkResponse(json)
        if (json.optString("Response") != "True") return emptyList()

        val array = json.optJSONArray("Search") ?: return emptyList()
        val result = ArrayList<Movie>(array.length())
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            result.add(
                Movie(
                    id = item.optString("imdbID"),
                    title = item.optString("Title"),
                    year = item.optString("Year"),
                    poster = clean(item.optString("Poster"))
                )
            )
        }
        return result
    }

    private fun detailsSync(imdbId: String): Movie? {
        val url = "${ApiConfig.BASE_URL}?apikey=${ApiConfig.OMDB_API_KEY}" +
                "&i=${encode(imdbId)}&plot=full"
        val json = JSONObject(httpGet(url))
        checkResponse(json)
        if (json.optString("Response") != "True") return null

        return Movie(
            id = json.optString("imdbID"),
            title = json.optString("Title"),
            year = json.optString("Year"),
            poster = clean(json.optString("Poster")),
            plot = clean(json.optString("Plot")),
            genre = clean(json.optString("Genre")),
            rating = clean(json.optString("imdbRating")),
            runtime = clean(json.optString("Runtime")),
            director = clean(json.optString("Director")),
            actors = clean(json.optString("Actors"))
        )
    }

    private fun checkResponse(json: JSONObject) {
        if (json.optString("Response") == "True") return
        val message = json.optString("Error")
        if (message.contains("key", ignoreCase = true)) throw RuntimeException(ERROR_API_KEY)
    }

    private fun clean(value: String): String = if (value == "N/A") "" else value

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")

    private fun httpGet(address: String): String {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(address).openConnection() as HttpURLConnection).apply {
                connectTimeout = 12_000
                readTimeout = 15_000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "MadventureTV/1.0 (Android TV)")
            }
            val code = connection.responseCode
            if (code !in 200..299) throw RuntimeException("HTTP $code")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection?.disconnect()
        }
    }
}

