package com.example.icompile.parser

import com.example.icompile.ast.*
import com.example.icompile.lexer.Lexer
import com.example.icompile.lexer.Token
import com.example.icompile.lexer.Tokens
import java.util.*


/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the **Abstract Syntax Tree** representation for the source
 * program<br></br>
 * Following is the Grammar we are using:<br></br>
 * <pre>
 * PROGRAM -> �program� BLOCK ==> program
 *
 * BLOCK -> �{� D* S* �}�  ==> block
 *
 * D -> TYPE NAME                    ==> decl
 * -> TYPE NAME FUNHEAD BLOCK      ==> functionDecl
 *
 * TYPE  ->  �int�
 * ->  �boolean�
 *
 * FUNHEAD  -> '(' (D list ',')? ')'  ==> formals<br></br>
 *
 * S -> �if� E �then� BLOCK �else� BLOCK  ==> if
 * -> �while� E BLOCK               ==> while
 * -> �return� E                    ==> return
 * -> BLOCK
 * -> NAME �=� E                    ==> assign<br></br>
 *
 * E -> SE
 * -> SE �==� SE   ==> =
 * -> SE �!=� SE   ==> !=
 * -> SE �<�  SE   ==> <
 * -> SE �<=� SE   ==> <=
 *
 * SE  ->  T
 * ->  SE �+� T  ==> +
 * ->  SE �-� T  ==> -
 * ->  SE �|� T  ==> or
 *
 * T  -> F
 * -> T �*� F  ==> *
 * -> T �/� F  ==> /
 * -> T �&� F  ==> and
 *
 * F  -> �(� E �)�
 * -> NAME
 * -> <int>
 * -> NAME '(' (E list ',')? ')' ==> call<br></br>
 *
 * NAME  -> <id>
</id></int></pre> *
 */
class Parser(sourceProgram: String) {
    private var currentToken: Token? = null
    var lex: Lexer
    private val relationalOps = EnumSet.of(
        Tokens.Equal,
        Tokens.NotEqual,
        Tokens.Less,
        Tokens.LessEqual
    )
    private val addingOps = EnumSet.of(
        Tokens.Plus,
        Tokens.Minus,
        Tokens.Or
    )
    private val multiplyingOps = EnumSet.of(
        Tokens.Multiply,
        Tokens.Divide,
        Tokens.And
    )

    // build tree with current token's relation
    val relationTree: AST?
        get() {  // build tree with current token's relation
            val kind: Tokens = currentToken!!.kind
            return if (relationalOps.contains(kind)) {
                val t: AST = RelOpTree(currentToken!!)
                scan()
                t
            } else {
                null
            }
        }

    private val addOperTree: AST?
        get() {
            val kind: Tokens = currentToken!!.kind
            return if (addingOps.contains(kind)) {
                val t: AST = AddOpTree(currentToken!!)
                scan()
                t
            } else {
                null
            }
        }

    private val multOperTree: AST?
        get() {
            val kind: Tokens = currentToken!!.kind
            return if (multiplyingOps.contains(kind)) {
                val t: AST = MultOpTree(currentToken!!)
                scan()
                t
            } else {
                null
            }
        }

    /**
     * Execute the parse command
     *
     * @return the AST for the source program
     * @exception Exception - pass on any type of exception raised
     */
    @Throws(Exception::class)
    fun execute(): AST? {

        return rProgram()

    }

    /**
     * <
     * pre>
     * Program -> 'program' block ==> program
     *
     *
     * @return the program tree
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rProgram(): AST {
        // note that rProgram actually returns a ProgramTree; we use the
        // principle of substitutability to indicate it returns an AST
        val t: AST = ProgramTree()
        expect(Tokens.Program)
        t.addKid(rBlock())
        return t
    }

    /**
     * <
     * pre>
     * block -> '{' d* s* '}' ==> block
     *
     *
     * @return block tree
     * @exception SyntaxError - thrown for any syntax error e.g. an expected
     * left brace isn't found
     */
    @Throws(SyntaxError::class)
    fun rBlock(): AST {
        expect(Tokens.LeftBrace)
        val t: AST = BlockTree()
        while (startingDecl()) {  // get decls
            t.addKid(rDecl())
        }
        while (startingStatement()) {  // get statements
            t.addKid(rStatement())
        }
        expect(Tokens.RightBrace)
        return t
    }

    private fun startingDecl(): Boolean {
        return isNextTok(Tokens.Int) || isNextTok(Tokens.Boolean) || isNextTok(Tokens.String)
    }

    private fun startingStatement(): Boolean {
        return (isNextTok(Tokens.If) || isNextTok(Tokens.While) || isNextTok(Tokens.Return)
                || isNextTok(Tokens.LeftBrace) || isNextTok(Tokens.Identifier))
    }

    /**
     * <
     * pre>
     * d -> type name ==> decl -> type name funcHead block ==> functionDecl
     *
     *
     * @return either the decl tree or the functionDecl tree
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rDecl(): AST {
        var t: AST
        t = rType()
        val t1: AST = rName()
        if (isNextTok(Tokens.LeftParen)) { // function
            t = FunctionDeclTree().addKid(t).addKid(t1)
            t.addKid(rFunHead())
            t.addKid(rBlock())
            return t
        }
        t = DeclTree().addKid(t).addKid(t1)
        return t
    }

    /**
     * <
     * pre>
     * type -> 'int' type -> 'bool'
     *
     *
     * @return either the intType or boolType tree
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rType(): AST {
        val t: AST

        when {
            isNextTok(Tokens.Int) -> {
                t = IntTypeTree()
                scan()

            }
            isNextTok(Tokens.String) -> {
                t = StringTypeTree()
                scan()
            }
            else -> {
                expect(Tokens.Boolean)
                t = BoolTypeTree()
            }
        }
        return t
    }

    /**
     * <
     * pre>
     * funHead -> '(' (decl list ',')? ')' ==> formals note a funhead is a list
     * of zero or more decl's separated by commas, all in parens
     *
     *
     * @return the formals tree describing this list of formals
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rFunHead(): AST {
        val t: AST = FormalsTree()
        expect(Tokens.LeftParen)
        if (!isNextTok(Tokens.RightParen)) {
            do {
                t.addKid(rDecl())
                if (isNextTok(Tokens.Comma)) {
                    scan()
                } else {
                    break
                }
            } while (true)
        }
        expect(Tokens.RightParen)
        return t
    }

    /**
     * <
     * pre>
     * S -> 'if' e 'then' block 'else' block ==> if -> 'while' e block ==> while
     * -> 'return' e ==> return -> block -> name '=' e ==> assign
     *
     *
     * @return the tree corresponding to the statement found
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rStatement(): AST {
        var t: AST
        if (isNextTok(Tokens.If)) {
            scan()
            t = IfTree()
            t.addKid(rExpr())
            expect(Tokens.Then)
            t.addKid(rBlock())
            expect(Tokens.Else)
            t.addKid(rBlock())
            return t
        }
        if (isNextTok(Tokens.While)) {
            scan()
            t = WhileTree()
            t.addKid(rExpr())
            t.addKid(rBlock())
            return t
        }
        if (isNextTok(Tokens.Return)) {
            scan()
            t = ReturnTree()
            t.addKid(rExpr())
            return t
        }
        if (isNextTok(Tokens.LeftBrace)) {
            return rBlock()
        }
        t = rName()
        t = AssignTree().addKid(t)
        expect(Tokens.Assign)
        t.addKid(rExpr())
        return t
    }

    /**
     * <
     * pre>
     * e -> se -> se '==' se ==> = -> se '!=' se ==> != -> se '<' se ==> < -> se
     * '<=' se ==> <=  @return the tree corresponding to the expression
     *
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rExpr(): AST {
        val kid = rSimpleExpr()
        val t: AST = relationTree ?: return kid
        t.addKid(kid)
        t.addKid(rSimpleExpr())
        return t
    }

    /**
     * <
     * pre>
     * se -> t -> se '+' t ==> + -> se '-' t ==> - -> se '|' t ==> or This rule
     * indicates we should pick up as many *t*'s as possible; the
     * *t*'s will be left associative
     *
     *
     * @return the tree corresponding to the adding expression
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rSimpleExpr(): AST {
        var tree: AST
        var kid = rTerm()
        while (true) {
            tree = addOperTree ?: break
            tree.addKid(kid)
            tree.addKid(rTerm())
            kid = tree
        }
        return kid
    }

    /**
     * <
     * pre>
     * t -> f -> t '*' f ==> * -> t '/' f ==> / -> t '&' f ==> and This rule
     * indicates we should pick up as many *f*'s as possible; the
     * *f*'s will be left associative
     *
     *
     * @return the tree corresponding to the multiplying expression
     * @exception SyntaxError - thrown for any syntax error
     */
    @Throws(SyntaxError::class)
    fun rTerm(): AST {
        var tree: AST
        var kid = rFactor()

        while (true) {
            tree = multOperTree ?: break
            tree.addKid(kid)
            tree.addKid(rFactor())
            kid = tree
        }
        return kid
    }

    /**
     * <
     * pre>
     * f -> '(' e ')' -> name -> <int>
     * -> name '(' (e list ',')? ')' ==> call
     *
     *
     * @return the tree corresponding to the factor expression
     * @exception SyntaxError - thrown for any syntax error
    </int> */
    @Throws(SyntaxError::class)
    fun rFactor(): AST {
        var t: AST
        if (isNextTok(Tokens.LeftParen)) { // -> (e)
            scan()
            t = rExpr()
            expect(Tokens.RightParen)
            return t
        }
        if (isNextTok(Tokens.Integer)) {  //  -> <int>
            t = IntTree(currentToken!!)
            scan()
            return t
        }
        if (isNextTok(Tokens.String)) {
            t = StringTree(currentToken!!)
            scan()
            return t
        }
        t = rName()
        if (!isNextTok(Tokens.LeftParen)) {  //  -> name
            return t
        }
        scan() // -> name '(' (e list ',')? ) ==> call
        t = CallTree().addKid(t)
        if (!isNextTok(Tokens.RightParen)) {
            do {
                t.addKid(rExpr())
                if (isNextTok(Tokens.Comma)) {
                    scan()
                } else {
                    break
                }
            } while (true)
        }
        expect(Tokens.RightParen)
        return t
    }

    /**
     * <
     * pre>
     * name -> <id>
     *
     *
     * @return the id tree
     * @exception SyntaxError - thrown for any syntax error
    </id> */
    @Throws(SyntaxError::class)
    fun rName(): AST {
        val t: AST
        if (isNextTok(Tokens.Identifier)) {
            t = IdTree(currentToken!!)
            scan()
            return t
        }
        throw SyntaxError(currentToken, Tokens.Identifier, lex.lineNumber.toString())
    }

    private fun isNextTok(kind: Tokens): Boolean {
        if ((currentToken == null) || (currentToken?.kind != kind)) {
            return false;
        }
        return true;
    }

    @Throws(SyntaxError::class)
    private fun expect(kind: Tokens) {
        if (isNextTok(kind)) {
            scan()
            return
        }
        throw SyntaxError(currentToken, kind, lex.lineNumber.toString())
    }

    private fun scan() {
        currentToken = lex.nextToken()
        return
    }

    /**
     * Construct a new Parser;
     *
     * @param sourceProgram - source file name
     * @exception Exception - thrown for any problems at startup (e.g. I/O)
     */
    init {
        try {
            lex = Lexer(sourceProgram)
            scan()
        } catch (e: Exception) {
            println("********exception*******${e.printStackTrace()}")
            throw e
        }
    }
}


internal class SyntaxError//    this.tokenFound = tokenFound;
/**
 * record the syntax error just encountered
 *
 * @param tokenFound is the token just found by the parser
 * @param kindExpected is the token we expected to find based on the current
 * context
 */(
    private val tokenFound: Token?,
    /**
     *
     */
    //private Token tokenFound;
    private val kindExpected: Tokens,
    private val lineNumber: String
) :
    Exception() {
    fun print(): String {
        return "Error occurred at line $lineNumber \n " +
                "cannot resolve token '$tokenFound' \n " +
                "Expected: $kindExpected"

    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 1L
    }

}