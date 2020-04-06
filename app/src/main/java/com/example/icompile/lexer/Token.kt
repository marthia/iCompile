package com.example.icompile.lexer

/** <pre>
 * The Token class records the information for a token:
 * 1. The Symbol that describes the characters in the token
 * 2. The starting column in the source file of the token and
 * 3. The ending column in the source file of the token
</pre> *
 */
class Token
/**
 * Create a new Token based on the given Symbol
 * @param leftPosition is the source file column where the Token begins
 * @param rightPosition is the source file column where the Token ends
 */(
    private val leftPosition: Int,
    private val rightPosition: Int,
    val lineno: Int,
    val symbol: Symbol
) {

    fun print() : String {
        return (
            "       " + symbol.toString() +
                    "             left: " + leftPosition +
                    " right: " + rightPosition + " line: " + lineno
                )
    }

    override fun toString(): String {
        return symbol.toString()
    }

    /**
     * @return the integer that represents the kind of symbol we have which
     * is actually the type of token associated with the symbol
     */
    val kind: Tokens
        get() = symbol.getKind()

}