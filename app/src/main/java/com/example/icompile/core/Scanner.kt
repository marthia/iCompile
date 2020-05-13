package com.example.icompile.core

import android.graphics.Point
import com.example.icompile.util.ScannerUtil
import kotlin.properties.Delegates


object Scanner {
    var isError = false
    private lateinit var text: String
    private var position by Delegates.notNull<Int>()
    private var newPos by Delegates.notNull<Int>()
    private lateinit var location: Point

    fun abortSyntax(s: String): String {

        isError = true

        return "$s \n" +
                "Line = ${currentLine()} \n" +
                "Character = ${text[position]} \n " +
                "at position ${location.x} , ${location.y}"
    }

    private fun clear() {
        text = ""
        position = 0
        location = Point(0, 0)
        isError = false
    }

    fun setText(s: String) {

        clear()

        text = s
    }

    fun reset() {
        position = 0
        isError = false
        location = Point(0, 0)
    }


    // get text at current line
    private fun currentLine(): String {
        val lines = text.split("\n")
        return lines[location.x]
    }

    private fun getTextAndMoveTo(destIndex: Int): String {
        val stringBuilder = StringBuilder()
        while (position < destIndex) {

            stringBuilder.append(text[position])
            if (text[position].toString() == "\n") {

                location.x++
                location.y = 1

            } else
                location.y++

            position++
        }
        return stringBuilder.toString()
    }

    fun getTokenInList(list: List<String>): Int {

        var keyword: String

        skipBlanks()

        repeat(list.size) {

            keyword = list[it]

            if (keyword.startsWith("#")) {
                keyword = keyword.replace("#", "")
            }

            if (
                keyword == "str" && isStr() ||
                keyword == "float" && isFloat() ||
                keyword == "int" && isInt() ||
                keyword == "id" && isId() ||
                isKeyword(keyword)

            ) return it
        }
        return -1
    }


    // check if the character(s) at current position is
    // either of these main groups
    private fun isInt(): Boolean {

        // skip all comments and whitespaces first
        skipBlanks()

        var p = position
        var state = 0

        loop@ while (p <= text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p] == '+' || text == "-" -> 1
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
                }

                1, 2 -> {
                    state = when {
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
                }
            }
            p++
        }
        newPos = p // save current p for the next iterations
        return state == 2 // return true only if reach the end of loop with state equals 2
    }

    private fun isFloat(): Boolean {
        // skip all comments and whitespaces first
        skipBlanks()

        var p = position
        var state = 0

        loop@ while (p <= text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p] == '+' || text == "-" -> 1
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
                }
                1 -> {
                    state = when {
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
                }
                2 -> {
                    state = when {
                        text[p].isDigit() -> 2
                        text[p] == '.' -> 3
                        text[p].toLowerCase() == 'e' -> 5
                        else -> break@loop
                    }
                }
                3 -> {
                    state = when {
                        text[p].isDigit() -> 4
                        else -> break@loop
                    }
                }
                4 -> {
                    state = when {
                        text[p].isDigit() -> 4
                        text[p].toLowerCase() == 'e' -> 5
                        else -> break@loop
                    }
                }

                5 -> {
                    state = when {
                        text[p].isDigit() -> 7
                        text[p] == '+' || text == "-" -> 6
                        else -> break@loop
                    }
                }

                6 -> {
                    state = when {
                        text[p].isDigit() -> 7
                        else -> break@loop
                    }
                }
                7 -> {
                    state = when {
                        text[p].isDigit() -> 7
                        else -> break@loop
                    }
                }
            }
            p++
        }

        newPos = p
        return state == 2 or 4 or 7
    }

    private fun isId(): Boolean {
        var p = position
        var state = 0

        // skip all comments and whitespaces first
        skipBlanks()

        loop@ while (p <= text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p].isLetter() -> 1
                        else -> break@loop
                    }
                }
                1 -> {
                    state = when {
                        text[p].isLetterOrDigit() -> 1
                        else -> break@loop

                    }
                }
            }
            p++
        }
        newPos = p
        return state == 1
    }

    private fun isStr(): Boolean {
        var p = position
        var state = 0

        // skip all comments and whitespaces first
        skipBlanks()

        loop@ while (p <= text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p].toString() == "\'" -> 1
                        ScannerUtil.containsSpecialCharacter(text[p].toString()) -> 3
                        else -> break@loop

                    }
                }
                1 -> {
                    state = when {
                        text[p].toString() == "\'" -> 2
                        text[p] == '\n' -> break@loop
                        else -> 1
                    }
                }
                2 -> {
                    state = when {
                        text[p].toString() == "\'" -> 1
                        ScannerUtil.containsSpecialCharacter(text[p].toString()) -> 3
                        else -> break@loop
                    }
                }
                3 -> {
                    state = when {
                        text[p].isDigit() -> 4
                        else -> break@loop
                    }
                }

                4 -> {
                    state = when {
                        text[p].isDigit() -> 4
                        text[p].toString() == "\'" -> 1
                        ScannerUtil.containsSpecialCharacter(text[p].toString()) -> 3
                        else -> break@loop
                    }
                }
            }
            p++
        }
        newPos = p
        return state == 2 or 4
    }

    fun isKeyword(keyword: String): Boolean {
        skipBlanks()

        newPos = position + keyword.length
        var result = false
        try {
            result = text.slice(IntRange(position, newPos - 1)) == keyword
        } catch (e: Exception) {
        }
        return result
    }
    // end of checks

    // get item and move position to the beginning of
    // the next item
    fun skipInt(): String {
        return if (isInt()) getTextAndMoveTo(newPos)
        else abortSyntax("Invalid Int")
    }

    fun skipFloat(): String {
        return if (isFloat())
            getTextAndMoveTo(newPos)
        else
            abortSyntax("invalid float")
    }

    fun skipId(): String {
        return if (isId()) return getTextAndMoveTo(newPos)
        else abortSyntax("Invalid Id")
    }

    fun skipStr(): String {
        return if (isStr()) getTextAndMoveTo(newPos)
        else
            abortSyntax("Invalid String")
    }

    fun getToken(keyword: String): String {
        return if (isKeyword(keyword)) {
            getTextAndMoveTo(newPos)
        } else abortSyntax("$keyword is expected")
    }

    private fun skipBlanks(): String {
        var p = position
        var state = 0

        loop@ while (p <= text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p] == '/' -> 1
                        text[p].isWhitespace() -> 0
                        else -> break@loop
                    }
                }

                1 -> {
                    state = when {
                        text[p] == '*' -> 2
                        text[p] == '/' -> 4
                        else -> break@loop

                    }
                }
                2 -> {
                    state = when {
                        (text[p] == '*') -> 3
                        else -> 2
                    }
                }
                3 -> {
                    state = when {
                        text[p] == '*' -> 3
                        text[p] == '/' -> 0
                        else -> 2
                    }
                }
                4 -> {
                    state = when {
                        text[p].toString() == "\n" -> 0
                        else -> 4
                    }
                }
            }
            p++
        }
        return when (state) {
            0 -> getTextAndMoveTo(p)
            1 -> getTextAndMoveTo(p - 1)
            else -> abortSyntax("Invalid Comment")
        }
    }
    // end of skips

    fun skipExpVal(): String {
        return SkipExpVal.execute()
    }

    fun skipRegularExpression(): String {
        return SkipRegular.execute()
    }

}
