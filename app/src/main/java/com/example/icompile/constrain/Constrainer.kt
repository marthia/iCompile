package com.example.icompile.constrain

import com.example.icompile.ast.*
import com.example.icompile.lexer.Lexer
import com.example.icompile.parser.Parser
import com.example.icompile.visitor.ASTVisitor
import com.example.icompile.visitor.PrintVisitor
import java.util.*


/**
 * Constrainer object will visit the AST, gather/check variable
 * type information and decorate uses of variables with their
 * declarations; the decorations will be used by the code generator
 * to provide access to the frame offset of the variable for generating
 * load/store bytecodes; <br></br>
 * Note that when constraining expression trees we return the type tree
 * corresponding to the result type of the expression; e.g.
 * the result of constraining the tree for 1+2*3 will be the int type
 * tree
 */
class Constrainer(t: AST, parser: Parser) : ASTVisitor() {
    enum class ConstrainerErrors {
        BadAssignmentType, CallingNonFunction, ActualFormalTypeMismatch, NumberActualsFormalsDiffer,
        TypeMismatchInExpr, BooleanExprExpected, BadConditional, ReturnNotInFunction, BadReturnExpr
    }

    private var t // the AST to constrain
            : AST
    private val symtab: Table = Table()
    private val parser // parser used with this constrainer
            : Parser

    /**
     * The following comment refers to the functions stack
     * declared below the comment.
     * Whenever we start constraining a function declaration
     * we push the function decl tree which indicates we're
     * in a function (to ensure that we don't attempt to return
     * from the main program - return's are only allowed from
     * within functions); it also gives us access to the return
     * type to ensure the type of the expr that is returned is
     * the same as the type declared in the function header
     */
    private val functions: Stack<AST> = Stack<AST>()
    fun execute() {
        symtab.beginScope()
        t.accept(this)
    }

    /**
     * t is an IdTree; retrieve the pointer to its declaration
     */
    private fun lookup(t: AST): AST? {
        return (t as IdTree).symbol?.let { symtab.get(it) } as AST?
    }

    /**
     * Decorate the IdTree with the given decoration - its decl tree
     */
    private fun enter(t: AST, decoration: AST) {
/*
        System.out.println("enter: "+((IdTree)t).getSymbol().toString()
              + ": " + decoration.getNodeNum());
*/
        (t as IdTree).symbol?.let { symtab.put(it, decoration) }
    }

    /**
     * get the type of the current type tree
     * @param t is the type tree
     * @return the intrinsic tree corresponding to the type of t
     */
    private fun getType(t: AST): AST? {
        return if (t.javaClass === IntTypeTree::class.java) intTree else boolTree
    }

    fun decorate(t: AST, decoration: AST?) {
        t.decoration = decoration
    }

    /**
     * @return the decoration of the tree
     */
    fun decoration(t: AST): AST? {
        return t.decoration
    }

    /**
     * build the intrinsic trees; constrain them in the same fashion
     * as any other AST
     */
    private fun buildIntrinsicTrees() {
        val lex: Lexer = parser.lex
        trueTree = lex.newIdToken("true", -1, -1, -1)?.let { IdTree(it) }
        falseTree = lex.newIdToken("false", -1, -1, -1)?.let { IdTree(it) }
        readId = lex.newIdToken("read", -1, -1, -1)?.let { IdTree(it) }
        writeId = lex.newIdToken("write", -1, -1, -1)?.let { IdTree(it) }
        boolTree = lex.newIdToken("<<bool>>", -1, -1, -1)?.let { IdTree(it) }?.let {
            DeclTree().addKid(BoolTypeTree())
                .addKid(it)
        }
        boolTree?.getKid(2)?.let { decorate(it, boolTree) }
        intTree =
            lex.newIdToken("<<int>>", -1, -1, -1)?.let {
                IdTree(
                    it
                )
            }?.let { DeclTree().addKid(IntTypeTree()).addKid(it) }
        intTree?.getKid(2)?.let { decorate(it, intTree) }
        // to facilitate type checking; this ensures int decls and id decls
        // have the same structure

        // read tree takes no parms and returns an int
        readTree =
            readId?.let {
                FunctionDeclTree().addKid(IntTypeTree()).addKid(it)
                    .addKid(FormalsTree()).addKid(BlockTree())
            }

        // write tree takes one int parm and returns that value
        writeTree =
            writeId?.let { FunctionDeclTree().addKid(IntTypeTree()).addKid(it) }
        val decl: AST? = lex.newIdToken("dummyFormal", -1, -1, -1)?.let { IdTree(it) }?.let {
            DeclTree().addKid(IntTypeTree())
                .addKid(it)
        }
        val formals: AST? = decl?.let { FormalsTree().addKid(it) }
        if (formals != null) {
            writeTree!!.addKid(formals).addKid(BlockTree())
        }
        writeTree!!.accept(this)
        readTree!!.accept(this)
    }

    /**
     * Constrain the program tree - visit its kid
     */
    override fun visitProgramTree(t: AST?): Any? {
        buildIntrinsicTrees()
        if (t != null) {
            this.t = t
        }
        t!!.getKid(1)!!.accept(this)
        return null
    }

    /**
     * Constrain the Block tree:<br></br>
     *  1. open a new scope,  1. constrain the kids in this new scope,  1. close the
     * scope removing any local declarations from this scope
     */
    override fun visitBlockTree(t: AST?): Any? {
        symtab.beginScope()
        visitKids(t!!)
        symtab.endScope()
        return null
    }

    /**
     * Constrain the FunctionDeclTree:
     *  1. Enter the function name in the current scope,  1. enter the formals
     * in the function scope and  1. constrain the body of the function
     */
    override fun visitFunctionDeclTree(t: AST?): Any? {
        val fname: AST? = t?.getKid(2)
        val returnType: AST? = t?.getKid(1)
        val formalsTree: AST? = t?.getKid(3)
        val bodyTree: AST? = t?.getKid(4)
        functions.push(t)
        fname?.let { enter(it, t) } // enter function name in CURRENT scope
        returnType?.let { decorate(it, getType(returnType)) }
        symtab.beginScope() // new scope for formals and body
        if (formalsTree != null) {
            visitKids(formalsTree)
        } // all formal names go in new scope
        bodyTree!!.accept(this)
        symtab.endScope()
        functions.pop()
        return null
    }

    /**
     * Constrain the Call tree:<br></br>
     * check that the number and types of the actuals match the
     * number and type of the formals
     */
    override fun visitCallTree(t: AST?): Any? {
        val fct: AST?
        val fname = t?.getKid(1)
        val fctType: AST?
        t?.let { visitKids(it) }
        fct = fname?.let { lookup(it) }
        if (fct?.javaClass != FunctionDeclTree::class.java) {
            constraintError(ConstrainerErrors.CallingNonFunction)
        }
        fctType = fct?.getKid(1)?.let { decoration(it) }
        t?.let { decorate(it, fctType) }
        decorate(t?.getKid(1)!!, fct)
        // now check that the number/types of actuals match the
        // number/types of formals
        fct?.let { checkArgDecls(t, it) }
        return fctType
    }

    private fun checkArgDecls(caller: AST, fct: AST) {
        // check number and types of args/formals match
        val formals: AST? = fct.getKid(3)
        val actualKids: Iterator<AST> = caller.kids.iterator()
        val formalKids: Iterator<AST> = formals?.kids!!.iterator()
        actualKids.next() // skip past fct name
        while (actualKids.hasNext()) {
            try {
                val actualDecl: AST? = decoration(actualKids.next())
                val formalDecl: AST = formalKids.next()
                if (actualDecl?.getKid(2)?.let { decoration(it) } !==
                    formalDecl.getKid(2)?.let { decoration(it) }
                ) {
                    constraintError(ConstrainerErrors.ActualFormalTypeMismatch)
                }
            } catch (e: Exception) {
                constraintError(ConstrainerErrors.NumberActualsFormalsDiffer)
            }
        }
        if (formalKids.hasNext()) {
            constraintError(ConstrainerErrors.NumberActualsFormalsDiffer)
        }
        return
    }

    /**
     * Constrain the Decl tree:<br></br>
     *  1. decorate to the corresponding intrinsic type tree,  1. enter the
     * variable in the current scope so later variable references can
     * retrieve the information in this tree
     */
    override fun visitDeclTree(t: AST?): Any? {
        val idTree: AST? = t?.getKid(2)
        idTree?.let { enter(it, t) }
        val typeTree: AST? = t?.getKid(1)?.let { getType(it) }
        idTree?.let { decorate(it, typeTree) }
        return null
    }

    /**
     * Constrain the *If* tree:<br></br>
     * check that the first kid is an expression that is a boolean type
     */
    override fun visitIfTree(t: AST?): Any? {
        if (t!!.getKid(1)!!.accept(this) !== boolTree) {
            constraintError(ConstrainerErrors.BadConditional)
        }
        t!!.getKid(2)!!.accept(this)
        t.getKid(3)!!.accept(this)
        return null
    }

    override fun visitWhileTree(t: AST?): Any? {
        if (t!!.getKid(1)!!.accept(this) !== boolTree) {
            constraintError(ConstrainerErrors.BadConditional)
        }
        t!!.getKid(2)!!.accept(this)
        return null
    }

    /**
     * Constrain the Return tree:<br></br>
     * Check that the returned expression type matches the type indicated
     * in the function we're returning from
     */
    override fun visitReturnTree(t: AST?): Any? {
        if (functions.empty()) {
            constraintError(ConstrainerErrors.ReturnNotInFunction)
        }
        val currentFunction: AST = functions.peek()
        decorate(t!!, currentFunction)
        val returnType: AST? = currentFunction.getKid(1)?.let { decoration(it) }
        if (t.getKid(1)!!.accept(this) !== returnType) {
            constraintError(ConstrainerErrors.BadReturnExpr)
        }
        return null
    }

    /**
     * Constrain the Assign tree:<br></br>
     * be sure the types of the right-hand-side expression and variable
     * match; when we constrain an expression we'll return a reference
     * to the intrinsic type tree describing the type of the expression
     */
    override fun visitAssignTree(t: AST?): Any? {
        val idTree = t?.getKid(1)
        val idDecl = lookup(idTree!!)
        var typeTree: AST? = null
        decorate(idTree, idDecl)

        val kid = idDecl?.getKid(2)
        kid?.let { typeTree = decoration(it) }

        // now check that the types of the expr and id are the same
        // visit the expr tree and get back its type
        if (t.getKid(2)!!.accept(this) !== typeTree) {
            constraintError(ConstrainerErrors.BadAssignmentType)
        }
        return null
    }

    override fun visitIntTree(t: AST?): Any? {
        decorate(t!!, intTree)
        return intTree
    }

    override fun visitIdTree(t: AST?): AST? {
        val decl: AST? = lookup(t!!)
        decorate(t, decl)
        return decl?.getKid(2)?.let { decoration(it) }
    }

    override fun visitRelOpTree(t: AST?): Any? {
        val leftOp = t?.getKid(1)
        val rightOp = t?.getKid(2)
        if (leftOp!!.accept(this) as AST? !== rightOp!!.accept(this) as AST?) {
            constraintError(ConstrainerErrors.TypeMismatchInExpr)
        }
        decorate(t!!, boolTree)
        return boolTree
    }

    /**
     * Constrain the expression tree with an adding op at the root:<br></br>
     * e.g. t1 + t2<br></br>
     * check that the types of t1 and t2 match, if it's a plus tree
     * then the types must be a reference to the intTree
     * @return the type of the tree
     */
    override fun visitAddOpTree(t: AST?): Any? {
        val leftOpType = t?.getKid(1)?.accept(this) as AST?
        val rightOpType = t?.getKid(2)?.accept(this) as AST?
        if (leftOpType !== rightOpType) {
            constraintError(ConstrainerErrors.TypeMismatchInExpr)
        }
        decorate(t!!, leftOpType)
        return leftOpType
    }


    fun constraintError(err: ConstrainerErrors) {
        val v1 = PrintVisitor()
        v1.visitProgramTree(t)
        println("****CONSTRAINER ERROR: $err   ****")
        // System.exit(1)
        return
    }

    override fun visitMultOpTree(t: AST?): Any? {
        return visitAddOpTree(t)
    }

    override fun visitIntTypeTree(t: AST?): Any? {
        return null
    }

    override fun visitBoolTypeTree(t: AST?): Any? {
        return null
    }

    override fun visitFormalsTree(t: AST?): Any? {
        return null
    }

    override fun visitActualArgsTree(t: AST?): Any? {
        return null
    }

    companion object {
        /**
         * readTree, writeTree, intTree, boolTree,falseTree, trueTree
         * are AST's that will be constructed (intrinsic trees) for
         * every program. They are constructed in the same fashion as
         * source program trees to ensure consisten processing of
         * functions, etc.
         */
        var readTree: AST? = null
        var writeTree: AST? = null
        var intTree: AST? = null
        var boolTree: AST? = null
        var falseTree: AST? = null
        var trueTree: AST? = null
        var readId: AST? = null
        var writeId: AST? = null
    }

    init {
        this.t = t
        this.parser = parser
    }
}