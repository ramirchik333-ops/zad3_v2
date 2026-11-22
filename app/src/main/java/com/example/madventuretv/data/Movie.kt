package com.example.madventuretv.data

/** Модель фильма, которую используют и сетка каналов, и экран подробностей. */
data class Movie(
    val id: String = "",
    val title: String = "",
    val year: String = "",
    val poster: String = "",
    val plot: String = "",
    val genre: String = "",
    val rating: String = "",
    val runtime: String = "",
    val director: String = "",
    val actors: String = "",
    val demo: Boolean = false
)
