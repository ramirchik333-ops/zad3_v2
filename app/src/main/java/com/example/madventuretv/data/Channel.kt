package com.example.madventuretv.data

/**
 * Канал = жанр. Для каждого канала заданы поисковые запросы к OMDb API
 * (OMDb не умеет искать «по жанру», поэтому канал собирает подборку
 * из нескольких тематических запросов и склеивает результаты).
 */
data class Channel(
    val title: String,
    val demoGenre: String?,
    val seeds: List<String>
)

object Channels {

    val list: List<Channel> = listOf(
        Channel("Все", null, listOf("Marvel", "Star", "Love")),
        Channel("Боевик", "боевик", listOf("Terminator", "Rambo", "Die Hard")),
        Channel("Комедия", "комедия", listOf("Hangover", "Ace Ventura", "Vacation")),
        Channel("Драма", "драма", listOf("Godfather", "Green Mile", "Forrest Gump")),
        Channel("Фантастика", "фантастика", listOf("Alien", "Matrix", "Blade Runner")),
        Channel("Ужасы", "ужасы", listOf("Conjuring", "Insidious", "Scream")),
        Channel("Мультфильмы", "мультфильмы", listOf("Toy Story", "Shrek", "Frozen")),
        Channel("Мелодрама", "мелодрама", listOf("Notebook", "Titanic", "Pride"))
    )
}
