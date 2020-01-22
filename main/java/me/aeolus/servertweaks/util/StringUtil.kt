package me.aeolus.servertweaks.util

import java.util.*

object StringUtil {

    private val romanNumeralMap = TreeMap<Int, String>()

    init {

        romanNumeralMap[1000] = "M"
        romanNumeralMap[900] = "CM"
        romanNumeralMap[500] = "D"
        romanNumeralMap[400] = "CD"
        romanNumeralMap[100] = "C"
        romanNumeralMap[90] = "XC"
        romanNumeralMap[50] = "L"
        romanNumeralMap[40] = "XL"
        romanNumeralMap[10] = "X"
        romanNumeralMap[9] = "IX"
        romanNumeralMap[5] = "V"
        romanNumeralMap[4] = "IV"
        romanNumeralMap[1] = "I"
    }

    fun romanCapitilizationSingleWorld(s : String) = s[0].toUpperCase() + s.substring(1).toLowerCase()

    fun romanCapitilizationSentence(s : String) : String {

        val splitWords = s.split(' ')

        var final = ""

        for ( x in splitWords ) final += "${romanCapitilizationSingleWorld(x)} "

        return final.trimEnd()
    }

    fun intToRomanNumerals(int : Int) : String {

        return if(int == 0) ""
        else {

            val flooredKey = romanNumeralMap.floorKey(int)

            if(int == flooredKey) romanNumeralMap[int]!!
            else romanNumeralMap[flooredKey] + intToRomanNumerals(int - flooredKey)

        }


    }

}