package com.pilou.woca.simpleClass

import java.util.*

class Card {
    var id:Int = 0
    var deck_id:Int = 0
    var word:String = ""
    var word_color:Int = 0
    var word_example:String = ""
    var is_learned:Boolean = false
    var tags:MutableList<String> = mutableListOf()
    var creationDate:Date = Date("31/07/1991")

    var translation_1:String = ""
    var translation_1_color:Int = 0
    var translation_1_example:String = ""

    var translation_2:String = ""
    var translation_2_color:Int = 0
    var translation_2_example:String = ""

    var translation_3:String = ""
    var translation_3_color:Int = 0
    var translation_3_example:String = ""
}