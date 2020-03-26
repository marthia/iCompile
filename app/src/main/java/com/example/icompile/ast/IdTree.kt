package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.lexer.Token
import com.example.icompile.visitor.ASTVisitor

class IdTree(tok: Token) : AST() {
    override var symbol: Symbol? = null
    /**
     * @return the frame offset for this variable - used by codegen
     */
    /**
     * @param i is the offset for this variable as determined by the code generator
     */
    var frameOffset = -1 // stack location for codegen
    override var line: Int = 0

    override fun accept(v: ASTVisitor?): Any? {
        return v?.visitIdTree(this)
    }


    override val type: String
        get() = "Id"

    /**
     * @param tok - record the symbol from the token Symbol
     */
    init {
        symbol = tok.symbol
        line = tok.lineno
    }
}