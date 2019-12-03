package com.example.icompile.core

import android.graphics.Point
import kotlin.properties.Delegates

class CoreTextParser {
    var isError = false
    private lateinit var text: String
    private var position by Delegates.notNull<Int>()
    private var newPos by Delegates.notNull<Int>()
    private lateinit var location: Point

    private fun String.abortSyntax(): String {

        isError = true

        return "$this \n" +
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

    private fun currentLine(): String {
        val split = text.split("\n")
        return split[location.x]
    }

    private fun jumpTo(destIndex: Int): String {
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

    private fun skipUnread(): String {
        var p = position
        var state = 0

        loop@ while (p < text.lastIndex) {
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
            0 -> jumpTo(p)
            1 -> jumpTo(p - 1)
            else -> "Invalid Comment".abortSyntax()
        }
    }

    private fun isInt(): Boolean {

        // skip all comments and whitespaces first
        skipUnread()

        var p = position
        var state = 0

        loop@ while (p < text.lastIndex) {
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
                        else -> break@loop
                    }
                }
            }
            p++
        }
        newPos = p
        return state == 2
    }

    fun skipInt(): String {
        return if (isInt()) jumpTo(newPos)
        else "Invalid Int".abortSyntax()
    }

    private fun isFloat(): Boolean {
        // skip all comments and whitespaces first
        skipUnread()

        var p = position
        var state = 0

        loop@ while (p < text.lastIndex) {
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

    fun skipFloat(): String {
        return if (isFloat())
            jumpTo(newPos)
        else
            "invalid float".abortSyntax()
    }

    private fun isId(): Boolean {
        var p = position
        var state = 0

        // skip all comments and whitespaces first
        skipUnread()

        loop@ while (p < text.lastIndex) {
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

    fun skipId(): String {
        return if (isId()) return jumpTo(newPos)
        else "Invalid Id".abortSyntax()
    }

    private fun isStr(): Boolean {
        var p = position
        var state = 0

        // skip all comments and whitespaces first
        skipUnread()

        loop@ while (p < text.lastIndex) {
            when (state) {
                0 -> {
                    state = when {
                        text[p].toString() == "\'" -> 1
                        text[p] == '#' -> 3
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
                        text[p] == '#' -> 3
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
                        text[p] == '#' -> 3
                        else -> break@loop
                    }
                }
            }
            p++
        }
        newPos = p
        return state == 2 or 4
    }

    fun skipStr(): String {
        return if (isStr()) jumpTo(newPos)
        else
            "Invalid Int".abortSyntax()
    }

    private fun isKeyword(keyword: String): Boolean {
        skipUnread()

        newPos = position + keyword.length
        return text.slice(IntRange(position, keyword.length)) == keyword
    }

    fun skipKeyword(keyword: String): String {
        return if (isKeyword(keyword)) {
            jumpTo(newPos)
        } else "$keyword is expected".abortSyntax()
    }

    fun getKeywordAt(list: List<String>): Int {
        var i = 0
        var keyword: String

        skipUnread()

        while (i < list.lastIndex) {
            keyword = if (list[i][1] == '#')
                list[i]
            else
                list[i]

            when {
                (keyword == "str") and isStr() -> return i
                (keyword == "id") and isId() -> return i
                (keyword == "int") and isInt() -> return i
                (keyword == "float") and isInt() -> return i
            }
            i++
        }
        return -1
    }

}
