package com.example.icompile.core

import android.graphics.Point
import android.util.Log
import kotlin.properties.Delegates


class Scanner : IScanner {

    var isError = false

    private lateinit var text: String

    private var position by Delegates.notNull<Int>()

    private var newPos by Delegates.notNull<Int>()

    private lateinit var location: Point

    override fun getErrorInfo(message: String): String {

        isError = true

        // if the unexpected character is located at the of the file show appropriate error message
        val v = if (position >= text.length)
            "unexpected end of the file"
        else text[position].toString()

        val result =
            "LINE:  ${currentLine()} \n\n" +
                    "CHARACTER:  $v \n\n " +
                    "POSITION:  ${location.x} , ${location.y}\n\n" +
                    "MESSAGE:  $message"


        Log.e("AbortSyntax", result)
        return result
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

    override fun getTokenInList(list: List<String>): Int {

        var keyword: String

        skipBlanks()

        repeat(list.size) {

            keyword = list[it]

            if (keyword.startsWith("#")) {
                keyword = keyword.replace("#", "")
            }

            if (
                keyword == "str" && isStr() ||
                keyword == "int" && isInt() ||
                keyword == "float" && isFloat() ||
                keyword == "id" && isId() ||
                isKeyword(keyword)

            ) return it
        }
        return -1
    }

    /**
     * check if the character(s) at current position is
     * either of these main groups
     *
     * @return true if the current token is Int
     *
     * */
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
                        text[p] == '+' || text == "-" -> 6
                        text[p].isDigit() -> 7
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
        return arrayOf(2, 4, 7).contains(state)
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
                        text[p].toString() == "\"" -> 1
                        else -> break@loop
                    }
                }

                1 -> {
                    state = when {
                        text[p].toString() == "\\" -> 2
                        text[p].toString() == "\"" -> 3
                        text[p].equals("\n") -> break@loop
                        else -> 1
                    }
                }

                2 -> {
                    state = when {
                        text[p].equals("\n") -> break@loop
                        else -> 1
                    }
                }
                3 -> {
                    break@loop
                }
            }
            p++
        }
        newPos = p
        return state == 3
    }

    override fun isKeyword(keyword: String): Boolean {
        skipBlanks()

        newPos = position + keyword.length
        var result = false
        try {
            result = text.slice(IntRange(position, newPos - 1)) == keyword
        } catch (e: Exception) {
        }
        return result
    }

    // get item and move position to the beginning of
    // the next character
    @Throws(SyntaxError::class)
    override fun skipInt(): String {
        return if (isInt()) getTextAndMoveTo(newPos)
        else throw SyntaxError("Invalid Int")
    }

    @Throws(SyntaxError::class)
    override fun skipFloat(): String {
        return if (isFloat())
            getTextAndMoveTo(newPos)
        else
            throw SyntaxError("invalid float")
    }

    @Throws(SyntaxError::class)
    override fun skipId(): String {

        if (isId()) return getTextAndMoveTo(newPos)
        else throw SyntaxError("Invalid Id")
    }

    @Throws(SyntaxError::class)
    override fun skipStr(): String {
        return if (isStr()) getTextAndMoveTo(newPos)
        else
            throw SyntaxError("Invalid String")
    }

    @Throws(SyntaxError::class)
    override fun getToken(keyword: String): String {
        return if (isKeyword(keyword)) {
            getTextAndMoveTo(newPos)
        } else throw SyntaxError("$keyword is expected")
    }

    @Throws(SyntaxError::class)
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
            else -> throw SyntaxError("Invalid Comment")
        }
    }

    // end of skips

}
