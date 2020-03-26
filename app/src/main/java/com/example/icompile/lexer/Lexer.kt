package com.example.icompile.lexer

import android.util.Log

/**
 * The Lexer class is responsible for scanning the source file
 * which is a stream of characters and returning a stream of
 * tokens; each token object will contain the string (or access
 * to the string) that describes the token along with an
 * indication of its location in the source program to be used
 * for error reporting; we are tracking line numbers; white spaces
 * are space, tab, newlines
 */
class Lexer(sourceFile: String) {
    private var atEOF = false
    private var ch // next character to process
            : Char
    private var source: SourceReader?

    // positions in line of current token
    private var startPosition = 0
    private var endPosition = 0
    var lineNumber = 0

    /**
     * newIdTokens are either ids or reserved words; new id's will be inserted
     * in the symbol table with an indication that they are id's
     * @param id is the String just scanned - it's either an id or reserved word
     * @param startPosition is the column in the source file where the token begins
     * @param endPosition is the column in the source file where the token ends
     * @return the Token; either an id or one for the reserved words
     */
    fun newIdToken(
        id: String?,
        startPosition: Int,
        endPosition: Int,
        lineNumber: Int
    ): Token? {
        return Symbol.symbol(id.toString(), Tokens.Identifier)?.let {
            Token(
                startPosition, endPosition, lineNumber,
                it
            )
        }
    }


    /**
     * number tokens are inserted in the symbol table; we don't convert the
     * numeric strings to numbers until we load the bytecodes for interpreting;
     * this ensures that any machine numeric dependencies are deferred
     * until we actually run the program; i.e. the numeric constraints of the
     * hardware used to compile the source program are not used
     * @param number is the int String just scanned
     * @param startPosition is the column in the source file where the int begins
     * @param endPosition is the column in the source file where the int ends
     * @return the int or float Token
     */
    private fun newNumberToken(
        number: String,
        startPosition: Int,
        endPosition: Int,
        lineNumber: Int
    ): Token? {
        return Symbol.symbol(number, Tokens.INTeger)?.let {
            Token(
                startPosition, endPosition, lineNumber,
                it
            )
        }
    }


    private fun newFloatToken(
        number: String,
        startPosition: Int,
        endPosition: Int,
        lineNumber: Int
    ): Token? {
        return Symbol.symbol(number, Tokens.Float)?.let {
            Token(
                startPosition, endPosition, lineNumber,
                it
            )
        }
    }



    /**
     * build the token for operators (+ -) or separators (parens, braces)
     * filter out comments which begin with two slashes
     * @param s is the String representing the token
     * @param startPosition is the column in the source file where the token begins
     * @param endPosition is the column in the source file where the token ends
     * @return the Token just found
     */
    private fun makeToken(s: String, startPosition: Int, endPosition: Int, lineNumber: Int): Token? {
        if (s == "--") {  // filter comment
            try {
                val oldLine: Int = source!!.lineno
                do {
                    ch = source!!.read()
                } while (oldLine == source!!.lineno)
            } catch (e: Exception) {
                atEOF = true
            }
            return nextToken()
        }
        val sym: Symbol? = Symbol.symbol(s, Tokens.BogusToken) // be sure it's a valid token
        if (sym == null) {
            println("******** illegal character: $s")
            atEOF = true
            return nextToken()
        }
        return Token(startPosition, endPosition, lineNumber, sym)
    }


    /**
     * @return the next Token found in the source file
     */
    fun nextToken(): Token? { // ch is always the next char to process
        if (atEOF) {
            if (source != null) {
                source!!.close()
                source = null
            }
            return null
        }
        try {
            while (ch.isWhitespace()) {  // scan past whitespace

                ch = source!!.read()
                Log.i("readNext", ch.toString())

            }
        } catch (e: java.lang.Exception) {
            atEOF = true
            return nextToken()
        }
        startPosition = source!!.position
        endPosition = startPosition - 1
        lineNumber = source!!.lineno
        if (Character.isJavaIdentifierStart(ch)) {
            // return tokens for ids and reserved words, checking for appropriate REGEX pattern:
            var id = ""
            try {
                do {
                    endPosition++
                    id += ch
                    ch = source!!.read()
                } while (Character.isJavaIdentifierPart(ch))
            } catch (e: java.lang.Exception) {
                atEOF = true
            }
            return if (!id.matches("[A-Za-z][A-Za-z0-9]*".toRegex())) {
                println("******** illegal identifier: $id")
                atEOF = true
                nextToken()
            } else newIdToken(id, startPosition, endPosition, lineNumber)
        }
        if (Character.isDigit(ch)) {
            // return int and float tokens:
            var number = ""
            var isFloat = false
            try {
                do {
                    //flags the presence of a possible float, avoids having multiple '.' in one number:
                    if (ch == '.' && source!!.mightBeFloat()) {
                        isFloat = true
                    }
                    endPosition++
                    number += ch
                    ch = source!!.read()
                    //This proceeds if ch is an integer, or the first decimal after the leading number:
                } while (Character.isDigit(ch) || ch == '.' && !isFloat && endPosition >= startPosition)
            } catch (e: java.lang.Exception) {
                atEOF = true
            }
            return if (isFloat) {
                newFloatToken(number, startPosition, endPosition, lineNumber)
            } else {
                newNumberToken(number, startPosition, endPosition, lineNumber)
            }
        }

        // At this point the only tokens to check for are one or two
        // characters; we must also check for comments that begin with
        // 2 --'s
        val charOld = "" + ch
        var op = charOld
        val sym: Symbol?
        try {
            endPosition++
            ch = source!!.read()
            op += ch
            // check if valid 2 char operator; if it's not in the symbol
            // table then don't insert it since we really have a one char
            // token
            sym = Symbol.symbol(
                op,
                Tokens.BogusToken
            )
            if (sym == null) {  // it must be a one char token
                return makeToken(charOld, startPosition, endPosition, lineNumber)
            }
            endPosition++
            ch = source!!.read()
            return makeToken(op, startPosition, endPosition, lineNumber)
        } catch (e: java.lang.Exception) {
        }
        atEOF = true
        if (startPosition == endPosition) {
            op = charOld
        }
        return makeToken(op, startPosition, endPosition, lineNumber)
    }

    init {
        TokenType() // init token table
        source = SourceReader(sourceFile)
        ch = source?.read()!!
    }
}