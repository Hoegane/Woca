package com.pilou.woca.simpleClass

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*{
    "vote_count": 2026,
    "id": 19404,
    "title": "Example Movie",
    "genre_ids": [
        35,
        18,
        10749
    ],
    "overview": "Overview of example movie"
}*/

@JsonClass(generateAdapter = true)
data class Movie (
    @Json(name = "vote_count") val voteCount: Int = -1,
    val id: Int,
    val title: String,
    @Json(name = "genre_ids") val genres: List<Genre>,
    val overview: String
)

data class Genre(val id: Int, val name: String)



@JsonClass(generateAdapter = true)
data class test (
    /*@Json(name = "vote_count") val voteCount: Int = -1,
    val id: Int,
    val title: String,
    @Json(name = "genre_ids") val genres: List<Genre>,
    val overview: String*/

    val cards: List<Card>
)
data class JsonDeck (
    val cards: List<Card> = listOf()
)

data class JsonCards(val id: Int, val name: String)

//data class JsonCard()

/*{
    "cards": [{
            "id": 0,
            "word": "het broer",
            "word_example": "je suis un example ",
            "translation_1": "the brother",
            "translation_1_example": "kjhekwh",
            "translation_2": "test 2",
            "translation_2_example": "je suis un test 3",
            "translation_3": "test 2",
            "translation_3_example": "je suis un test 3"
        },
        {
            "id": 0,
            "word": "de kantine",
            "word_example": "example 1",
            "translation_1": "the cantine",
            "translation_1_example": "boubou1"
        },
        {
            "id": 0,
            "word": "zitten",
            "word_example": "example 2",
            "translation_1": "to sit",
            "translation_1_example": "boubou2"
        },
        {
            "id": 0,
            "word": "deze",
            "word_example": "example 3",
            "translation_1": "this",
            "translation_1_example": "boubou3"
        }
    ]
}
*/