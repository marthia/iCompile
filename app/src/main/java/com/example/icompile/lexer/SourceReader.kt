package com.example.icompile.lexer

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * This class is used to manage the source program input stream;
 * each read request will return the next usable character; it
 * maintains the source column position of the character
 */
class SourceReader(sourceFile: String) {
    private lateinit var source: BufferedReader

    /**
     * @return the line number of the character just read in
     */
    var lineno = 0

    /**
     * @return the position of the character just read in
     */
    // line number of source program
    var position = 0 // position of last character processed = 0
    private var isPriorEndLine =
        true // if true then last character read was newline

    // so read in the next line
    private lateinit var nextLine: String
    private var possibleFloat = false
    fun close() {
        try {
            source.close()
        } catch (e: Exception) {
        }
    }

    /**
     * read next char; track line #, character position in line<br></br>
     * return space for newline
     * @return the character just read in
     * @IOException is thrown for IO problems such as end of file
     */
    @Throws(IOException::class)
    fun read(): Char {
        if (isPriorEndLine) {
            lineno++
            position = -1
            nextLine = source.readLine()
            possibleFloat = false

            isPriorEndLine = false
        }
        if (nextLine.isEmpty()) {

            isPriorEndLine = true
            return ' '
        }
        position++
        if (position >= nextLine.length) {
            isPriorEndLine = true
            return ' '
        }

        //Used to check if the '.' is in the middle of a numeric expression,
        //Checks if following character is a int, and not end of token:
        if (nextLine[position] == '.' && position < nextLine.length) {
            if (Character.isDigit(nextLine[position + 1])) {
                possibleFloat = true
            }
        }
        return nextLine[position]
    }

    /**
     * @return checks for the status of a floating point number being input:
     */
    fun mightBeFloat(): Boolean {
        return possibleFloat
    }

    /**
     * Construct a new SourceReader
     * @param sourceFile the String describing the user's source file
     * @exception IOException is thrown if there is an I/O problem
     */
    init {
        try {
            source =
                BufferedReader(FileReader(sourceFile))
        } catch (e: Exception) {
            println(e)
        }
    }
}