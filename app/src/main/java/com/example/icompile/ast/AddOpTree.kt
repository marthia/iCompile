package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class AddOpTree(tok: Token) : AST() {
    override var symbol: Symbol? = null
    override var line = 0

    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitAddOpTree(this)
    }


    override val type: String
        get() = "AddOp"

    init {
        symbol = tok.symbol
        line = tok.lineno
    }
}