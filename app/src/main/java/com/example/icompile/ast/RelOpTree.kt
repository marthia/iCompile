package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class RelOpTree(tok: Token) : AST() {
    override var symbol: Symbol? = null
    override var line: Int = 0

    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitRelOpTree(this)
    }


    override val type: String
        get() = "RelOp"

    /**
     * @param tok contains the Symbol which indicates the specific relational operator
     */
    init {
        symbol = tok.symbol
        line = tok.lineno
    }
}