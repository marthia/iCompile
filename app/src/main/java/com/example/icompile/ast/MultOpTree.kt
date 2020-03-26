package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class MultOpTree(tok: Token) : AST() {
    override var symbol: Symbol? = null
    override var line: Int = 0

    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitMultOpTree(this)
    }


    override val type: String
        get() = "MultOp"

    /**
     * @param tok contains the Symbol that indicates the specific multiplying operator
     */
    init {
        symbol = tok.symbol
        line = tok.lineno
    }
}