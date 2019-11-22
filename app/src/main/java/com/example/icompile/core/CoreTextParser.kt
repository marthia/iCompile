package com.example.icompile.core

import android.graphics.Point

class CoreTextParser {
    private lateinit var text: String
    private var position: Int = -1
    private var newPos: Int = -1
    private lateinit var location: Point

    private fun String.abortSyntax(): String {

        return "${this} \n" +
                "Line = ${currentLine()} \n" +
                "Character = ${text[position]} \n " +
                "at position ${location.x} , ${location.y}"
    }

    private fun clear() {
        text = ""
        position = 1
        location = Point(1, 1)
    }

    fun setText(l: String) {
        clear();
        text = l
    }

    private fun currentLine(): Pair<Int, Int> {
        var p1 = -1
        var p2 = -1

        for (i in position..0) {
            if (text[i] == '\n') {
                p1 = i
                break
            }
        }
        for (j in position..0) {
            if (text[j] == '\n') {
                p2 = j
                break
            }
        }
        return Pair(p1 + 1, p2 - p1 - 1)
    }

    fun isEof() =
        (position > text.lastIndex)

    private fun jumpTo(destIndex: Int): String {
        val location = Point(0, 0)
        val stringBuilder = StringBuilder()
        while (position <= destIndex) {

            stringBuilder.append(text[position])
            if (text[position] == '\n') {

                location.x++
                location.y = 1;
            } else {
                location.y++

                position++
            }
        }
        return stringBuilder.toString()
    }

    fun skipUnread(): String {
        var state = 0
        var p = -1
        loop@ for (i in position..text.lastIndex) {
            when (i) {
                0 ->
                    state = when {
                        text[i] == '/' -> 1
                        text[i].isWhitespace() -> 0
                        else -> {
                            p = i
                            break@loop
                        }
                    }

                1 ->
                    state = when {
                        text[i] == '*' -> 2
                        text[i] == '/' -> 4
                        else -> {
                            p = i
                            break@loop
                        }
                    }
                2 ->
                    state = if (text[i] == '*') 3
                    else 2
                3 ->
                    state = when {
                        text[i] == '*' -> 3
                        text[i] == '/' -> 0
                        else -> {
                            p = i
                            2
                        }
                    }
                4 ->
                    state = when {
                        text[i] == '\n' -> 0
                        else -> {
                            p = i
                            4
                        }
                    }
            }
        }
        return when (state) {
            0 -> jumpTo(p)

            1 -> jumpTo(p - 1)
            else -> "Invalid Comment".abortSyntax()
        }
    }

    fun isId(): Boolean {
        var p = -1
        var state = 0

        // skip all comments and whitespaces first
        skipUnread()

        loop@ for (i in position..text.lastIndex) {
            when (i) {
                0 ->
                    state = when {
                        text[p].isLetter() -> 1
                        else -> break@loop
                    }

                1 ->
                    state = when {
                        text[p].isLetterOrDigit() -> 1
                        else -> break@loop
                    }
            }
        }
        newPos = p
        return state == 1
    }

    fun isInt(): Boolean {
        var p = -1
        var state = 0

        // skip all comments and whitespaces first
        skipUnread()

        loop@ for (i in position..text.lastIndex) {
            when (i) {
                0 ->
                    state = when {
                        text[p] == '+' || text == "-" -> 1
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }

                1 ->
                    state = when {
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
                2 ->
                    state = when {
                        text[p].isDigit() -> 2
                        else -> break@loop
                    }
            }
        }
        newPos = p
        return state == 2
    }

    fun skipId(): String {
        return if (isId()) return jumpTo(newPos)
        else "Invalid Id".abortSyntax()
    }

    fun skipInt(): Int {
        return if (isInt()) jumpTo(newPos).toInt()
        else {
            "Invalid Int".abortSyntax()
            return -1
        }
    }




}