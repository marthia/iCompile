package com.example.icompile.lexer

import java.util.*

/**
 * The Symbol class is used to store all user strings along with
 * an indication of the kind of strings they are; e.g. the id "abc" will
 * store the "abc" in name and Sym.Tokens.Identifier in kind
 */
class Symbol private constructor(private val name: String, // token kind of symbol
                                 private val kind: Tokens
) {

    override fun toString(): String {
        return name
    }

    fun getKind(): Tokens {
        return kind
    }

    companion object {
        // symbols contains all strings in the source program
        private val symbols =
            HashMap<String, Symbol>()

        /**
         * Return the unique symbol associated with a string.
         * Repeated calls to <tt>symbol("abc")</tt> will return the same Symbol.
         */
        fun symbol(
            newTokenString: String,
            kind: Tokens
        ): Symbol? {
            var s =
                symbols[newTokenString]
            if (s == null) {
                if (kind === Tokens.BogusToken) {  // bogus string so don't enter into symbols
                    return null
                }
                //System.out.println("new symbol: "+u+" Kind: "+kind);
                s = Symbol(newTokenString, kind)
                symbols[newTokenString] = s
            }
            return s
        }
    }

}