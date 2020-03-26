package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class AssignTree : AST {
    override var symbol: Symbol? = null
    override var line = 0

    constructor() {}
    constructor(tok: Token) {
        symbol = tok.symbol
        line = tok.lineno
    }

    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitAssignTree(this)
    }


    override val type: String
        get() = "AssignTree"
}