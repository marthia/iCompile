package com.example.icompile.visitor

import com.example.icompile.ast.AST

/**
 * ASTVisitor class is the root of the Visitor hierarchy for visiting
 * various AST's; each visitor asks each node in the AST it is given
 * to *accept* its visit; <br></br>
 * each subclass **must** provide all of the visitors mentioned
 * in this class; <br></br>
 * after visiting a tree the visitor can return any Object of interest<br></br>
 * e.g. when the constrainer visits an expression tree it will return
 * a reference to the type tree representing the type of the expression
 */
abstract class ASTVisitor {
    fun visitKids(t: AST) {
        for (kid in t.kids) {
            kid.accept(this)
        }
        return
    }

    abstract fun visitIntTree(t: AST?): Any?
    abstract fun visitAddOpTree(t: AST?): Any?
    abstract fun visitActualArgsTree(t: AST?): Any?
    abstract fun visitAssignTree(t: AST?): Any?
    abstract fun visitBlockTree(t: AST?): Any?
    abstract fun visitBoolTypeTree(t: AST?): Any?
    abstract fun visitCallTree(t: AST?): Any?
    abstract fun visitDeclTree(t: AST?): Any?
    abstract fun visitFormalsTree(t: AST?): Any?
    abstract fun visitFunctionDeclTree(t: AST?): Any?
    abstract fun visitIdTree(t: AST?): Any?
    abstract fun visitMultOpTree(t: AST?): Any?
    abstract fun visitIfTree(t: AST?): Any?
    abstract fun visitIntTypeTree(t: AST?): Any?
    abstract fun visitProgramTree(t: AST?): Any?
    abstract fun visitRelOpTree(t: AST?): Any?
    abstract fun visitReturnTree(t: AST?): Any?
    abstract fun visitWhileTree(t: AST?): Any?
}