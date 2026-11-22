package com.example.madventuretv.data

/**
 * НАСТРОЙКА API.
 *
 * 1. Зарегистрируйтесь на https://www.omdbapi.com/apikey.aspx
 * 2. Получите бесплатный ключ (придёт на e-mail, активируется по ссылке).
 * 3. Вставьте ключ в константу OMDB_API_KEY вместо текста "ВСТАВЬТЕ_КЛЮЧ_OMDB".
 *
 * Только в этой строке нужно менять ключ — во всём остальном приложении
 * он подставляется автоматически.
 */
object ApiConfig {

    const val OMDB_API_KEY: String = "ВСТАВЬТЕ_КЛЮЧ_OMDB"

    const val BASE_URL: String = "https://www.omdbapi.com/"

    /** Пока ключ не указан, приложение показывает демо-каталог. */
    val hasKey: Boolean
        get() = OMDB_API_KEY.isNotBlank() &&
                !OMDB_API_KEY.contains("ВСТАВЬТЕ", ignoreCase = true)
}
