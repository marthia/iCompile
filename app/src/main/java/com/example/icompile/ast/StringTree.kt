package com.example.icompile.ast


import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class StringTree(tok: Token) : AST() {
    override var symbol: Symbol? = null
    override var line: Int = 0


    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitStringTree(this)
    }


    override val type: String
        get() = "string"

    /**
     * @param tok is the Token containing the String representation of the integer
     * literal; we keep the String rather than converting to an integer value
     * so we don't introduce any machine dependencies with respect to integer
     * representations
     */
    init {
        symbol = tok.symbol
        line = tok.lineno
    }
}