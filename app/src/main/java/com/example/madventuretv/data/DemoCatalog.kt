package com.example.madventuretv.data

/**
 * Демо-каталог. Показывается, пока в ApiConfig не указан ключ OMDb,
 * чтобы приложение можно было запустить и проверить без интернета и регистрации.
 */
object DemoCatalog {

    val movies: List<Movie> = listOf(
        Movie(
            id = "demo-matrix",
            title = "The Matrix",
            year = "1999",
            plot = "Хакер Нео узнаёт, что привычный мир — симуляция машин, и вступает в борьбу за свободу людей.",
            genre = "Фантастика",
            rating = "8.7",
            runtime = "136 мин",
            director = "Лана и Лилли Вачовски",
            actors = "Киану Ривз, Лоренс Фишбёрн, Кэрри-Энн Мосс",
            demo = true
        ),
        Movie(
            id = "demo-inception",
            title = "Inception",
            year = "2010",
            plot = "Профессиональный вор проникает в сны и крадёт идеи, а однажды получает задачу внедрить мысль в чужое сознание.",
            genre = "Фантастика",
            rating = "8.8",
            runtime = "148 мин",
            director = "Кристофер Нолан",
            actors = "Леонардо ДиКаприо, Джозеф Гордон-Левитт, Эллен Пейдж",
            demo = true
        ),
        Movie(
            id = "demo-fury-road",
            title = "Mad Max: Fury Road",
            year = "2015",
            plot = "В выжженной пустоши Макс и Фуриоса уходят от армии тирана на грузовике, гружённом бензином.",
            genre = "Боевик",
            rating = "8.1",
            runtime = "120 мин",
            director = "Джордж Миллер",
            actors = "Том Харди, Шарлиз Терон, Николас Холт",
            demo = true
        ),
        Movie(
            id = "demo-dark-knight",
            title = "The Dark Knight",
            year = "2008",
            plot = "Бэтмен противостоит Джокеру — преступнику, который хочет погрузить Готэм в хаос.",
            genre = "Боевик",
            rating = "9.0",
            runtime = "152 мин",
            director = "Кристофер Нолан",
            actors = "Кристиан Бэйл, Хит Леджер, Аарон Экхарт",
            demo = true
        ),
        Movie(
            id = "demo-godfather",
            title = "The Godfather",
            year = "1972",
            plot = "Младший сын дона Корлеоне вынужден возглавить семейный бизнес и защитить клан.",
            genre = "Драма",
            rating = "9.2",
            runtime = "175 мин",
            director = "Фрэнсис Форд Коппола",
            actors = "Марлон Брандо, Аль Пачино, Джеймс Каан",
            demo = true
        ),
        Movie(
            id = "demo-gump",
            title = "Forrest Gump",
            year = "1994",
            plot = "Простодушный Форрест проходит через несколько десятилетий американской истории, не теряя доброты.",
            genre = "Драма",
            rating = "8.8",
            runtime = "142 мин",
            director = "Роберт Земекис",
            actors = "Том Хэнкс, Робин Райт, Гэри Синиз",
            demo = true
        ),
        Movie(
            id = "demo-hangover",
            title = "The Hangover",
            year = "2009",
            plot = "Трое друзей просыпаются в Вегасе после мальчишника и не могут вспомнить, где жених.",
            genre = "Комедия",
            rating = "7.7",
            runtime = "100 мин",
            director = "Тодд Филлипс",
            actors = "Брэдли Купер, Эд Хелмс, Зак Галифианакис",
            demo = true
        ),
        Movie(
            id = "demo-conjuring",
            title = "The Conjuring",
            year = "2013",
            plot = "Супруги-исследователи паранормальных явлений берутся за дело семьи, в доме которой поселилось зло.",
            genre = "Ужасы",
            rating = "7.5",
            runtime = "112 мин",
            director = "Джеймс Ван",
            actors = "Вера Фармига, Патрик Уилсон, Лили Тейлор",
            demo = true
        ),
        Movie(
            id = "demo-toy-story",
            title = "Toy Story",
            year = "1995",
            plot = "Ковбой Вуди ревнует к новому космическому рейнджеру и понимает, что дружба важнее.",
            genre = "Мультфильмы",
            rating = "8.3",
            runtime = "81 мин",
            director = "Джон Лассетер",
            actors = "Том Хэнкс, Тим Аллен, Дон Риклс",
            demo = true
        ),
        Movie(
            id = "demo-la-la-land",
            title = "La La Land",
            year = "2016",
            plot = "Джазовый пианист и начинающая актриса в Лос-Анджелесе выбирают между любовью и мечтой.",
            genre = "Мелодрама",
            rating = "8.0",
            runtime = "128 мин",
            director = "Дэмьен Шазелл",
            actors = "Райан Гослинг, Эмма Стоун, Джон Ледженд",
            demo = true
        )
    )

    fun byChannel(channel: Channel): List<Movie> {
        val genre = channel.demoGenre ?: return movies
        return movies.filter { it.genre.equals(genre, ignoreCase = true) }
    }

    fun search(query: String): List<Movie> {
        val q = query.trim()
        if (q.isEmpty()) return movies
        return movies.filter { it.title.contains(q, ignoreCase = true) }
    }
}
