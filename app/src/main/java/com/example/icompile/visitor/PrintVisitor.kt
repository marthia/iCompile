package com.example.icompile.visitor

import com.example.icompile.ast.*

/**
 * PrintVisitor is used to visit an AST and print it using
 * appropriate indentation:<br></br>
 * <pre>
 * 1. root
 * 2.   Kid1
 * 3.   Kid2
 * 4.     Kid21
 * 5.     Kid22
 * 6.     Kid23
 * 7.   Kid3
</pre> *
 */
class PrintVisitor : ASTVisitor() {
    private var indent = 0
    private fun printSpaces(num: Int) {
        var s = ""
        for (i in 0 until num) {
            s += ' '
        }
        print(s)
    }

    /**
     * Print the tree
     * @param s is the String for the root of t
     * @param t is the tree to print - print the information
     * in the node at the root (e.g. decoration) and its kids
     * indented appropriately
     */
    fun print(s: String?, t: AST): String? {
        // assume less than 1000 nodes; no problem for csc 413
        var s = s
        val num: Int = t.nodeNum
        val decoration: AST? = t.decoration
        val decNum = decoration?.label ?: -1
        var spaces = ""
        if (num < 100) spaces += " "
        if (num < 10) spaces += " "
        print("$num:$spaces")
        printSpaces(indent)
        if (decNum != -1) {
            s += " Dec: $decNum"
        }
        val lab: String = t.label
        if (lab.isNotEmpty()) {
            s += " Label: " + t.label
        }
        if (t.javaClass === IdTree::class.java) {
            val offset: Int = (t as IdTree).frameOffset
            if (offset >= 0) {
                s += " Addr: $offset"
            }
        }
//        println(s)
        indent += 2
        visitKids(t)
        indent -= 2

        return s
    }

    //public //Object visitIntTree(AST t) { print("Int: "+((IntTree)t).getSymbol().toString(),t);  return null; }
    //public Object visitAddOpTree(AST t) { print("AddOp: "+((AddOpTree)t).getSymbol().toString(),t);  return null; }
    //public Object visitMultOpTree(AST t) { print("MultOp: "+((MultOpTree)t).getSymbol().toString(),t);  return null; }
    override fun visitProgramTree(t: AST?): Any? {
        print("Program", t!!)
        return null
    }

    override fun visitBlockTree(t: AST?): Any? {
        print("Block", t!!)
        return null
    }

    override fun visitFunctionDeclTree(t: AST?): Any? {
        print("FunctionDecl", t!!)
        return null
    }

    override fun visitCallTree(t: AST?): Any? {
        print("Call", t!!)
        return null
    }

    override fun visitDeclTree(t: AST?): Any? {
        print("Decl", t!!)
        return null
    }

    override fun visitIntTypeTree(t: AST?): Any? {
        print("IntType", t!!)
        return null
    }

    override fun visitBoolTypeTree(t: AST?): Any? {
        print("BoolType", t!!)
        return null
    }

    override fun visitFormalsTree(t: AST?): Any? {
        print("Formals", t!!)
        return null
    }

    override fun visitActualArgsTree(t: AST?): Any? {
        print("ActualArgs", t!!)
        return null
    }

    override fun visitIfTree(t: AST?): Any? {
        print("If", t!!)
        return null
    }

    override fun visitWhileTree(t: AST?): Any? {
        print("While", t!!)
        return null
    }

    override fun visitReturnTree(t: AST?): Any? {
        print("Return", t!!)
        return null
    }

    override fun visitAssignTree(t: AST?): Any? {
        print("Assign", t!!)
        return null
    }

    override fun visitIntTree(t: AST?): Any? {
        print("Int: " + (t as IntTree).symbol.toString(), t)
        return null
    }

    override fun visitIdTree(t: AST?): Any? {
        print("Id: " + (t as IdTree).symbol.toString(), t)
        return null
    }

    override fun visitRelOpTree(t: AST?): Any? {
        print("RelOp: " + (t as RelOpTree).symbol.toString(), t)
        return null
    }

    override fun visitAddOpTree(t: AST?): Any? {
        print("AddOp: " + (t as AddOpTree).symbol.toString(), t)
        return null
    }

    override fun visitMultOpTree(t: AST?): Any? {
        print("MultOp: " + (t as MultOpTree).symbol.toString(), t)
        return null
    }
}