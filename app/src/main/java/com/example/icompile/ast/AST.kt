package com.example.icompile.ast

import com.example.icompile.lexer.Symbol
import com.example.icompile.visitor.ASTVisitor
import java.util.*

/**
 * The AST Abstract class is the Abstract Syntax Tree representation;
 * each node contains 1.  references to its kids,  1. its unique node number
 * used for printing/debugging,  1. its decoration used for constraining
 * and code generation, and  1. a label for code generation
 * The AST is built by the Parser
 */
abstract class AST {


    var kids: ArrayList<AST>
        protected set

    var nodeNum: Int
        protected set

    open var line = 0
        protected set

    var decoration: AST? = null

    open var symbol: Symbol? = null

    var label = "" // label for generated code of tree

    open val type: String
        get() = "AST"

    /**
     * get the AST corresponding to the kid
     * @param i is the number of the needed kid; it starts with kid number one
     * @return the AST for the indicated kid
     */
    fun getKid(i: Int): AST? {
        return if (i <= 0 || i > kidCount()) {
            null
        } else kids[i - 1]
    }

    /**
     * @return the number of kids at this node
     */
    fun kidCount(): Int {
        return kids.size
    }

    /**
     * accept the visitor for this node - this method must be defined in each of
     * the subclasses of AST
     * @param v is the ASTVisitor visiting this node (currently, a printer,
     * constrainer and code generator)
     * @return the desired Object, as determined by the visitor
     */
    abstract fun accept(v: ASTVisitor?): Any?
    fun addKid(kid: AST): AST {
        kids.add(kid)
        return this
    }

//    fun getSymbol(): Symbol? {
//        return symbol
//    }

    companion object {
        var NodeCount = 0
    }

    init {
        kids = ArrayList()
        NodeCount++
        nodeNum = NodeCount
    }
}