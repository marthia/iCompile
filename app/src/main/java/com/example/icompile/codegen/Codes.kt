package com.example.icompile.codegen

import java.util.*

/**
 * <pre>
 * Class Codes maintains bytecode related information:
 * 1. bytecode constants used for code generation
 * 2. bytecode strings used for printing bytecodes
 * 3. an indication on how each bytecode affects the
 * runtime stack; we need to track changes to the
 * stack to provide local variable offsets
 * e.g. consider the following program
 *
 * program {
 * int i boolean j
 * // i is offset 0; j is offset 1
 * int factorial(int n) {
 * // n is offset 0 in new frame
 * if (n < 2) then { return 1 }
 * else {int i return n*factorial(n-1)}
 * // i is offset 1 in new frame
</pre> *
 */
object Codes {
    /*
	HALT         halt execution
    POP          pop n
    FALSEBRANCH  falsebranch <label>
    GOTO         goto <label>
    STORE        store n <varname>  n is frame offset
    LOAD         load n  <varname>  n is frame offset
    LIT          lit n  -- load n
    ARGS         args n  -- n = #args
    CALL         call <funcname>
    RETURN       return <funcname>
    BOP          bop <binary op>
    READ         read
    WRITE        write
    LABEL        label <label>
	*/
    var frameChange = HashMap<ByteCodes, Int>()
    const val UnknownChange = 99

    enum class ByteCodes {
        HALT, POP, FALSEBRANCH, GOTO, STORE, LOAD, LIT, ARGS, CALL, RETURN, BOP, READ, WRITE, LABEL, FUNCTION, LINE, FORMAL
    }

    /**
     * Codes initializes the FrameChange array which records how
     * execution of each bytecode instruction will affect the runtime stack
     *
     * The following is a static block - it gets executed when this class is loaded
     */
    init {
        frameChange[ByteCodes.HALT] = 0
        frameChange[ByteCodes.POP] = UnknownChange // depends on how many popped
        frameChange[ByteCodes.FALSEBRANCH] = -1 // pop conditional expr
        frameChange[ByteCodes.GOTO] = 0
        frameChange[ByteCodes.STORE] = -1 // pop value
        frameChange[ByteCodes.LOAD] = 1 // load new value
        frameChange[ByteCodes.LIT] = 1 // load literal value
        frameChange[ByteCodes.ARGS] = UnknownChange // actual args
        frameChange[ByteCodes.CALL] = 1 // result of fct call is pushed
        frameChange[ByteCodes.RETURN] = -1 // pop return value
        frameChange[ByteCodes.BOP] = -1 // replace values with
        // second level op top level
        frameChange[ByteCodes.READ] = 1 // read in new value
        frameChange[ByteCodes.WRITE] = 0 // write value; leave on top
        frameChange[ByteCodes.LABEL] = 0 // branch label
        frameChange[ByteCodes.LINE] = 0 // source line label
        frameChange[ByteCodes.FUNCTION] = 0 // function parameters for debugger
        frameChange[ByteCodes.FORMAL] = 0 // argument parameters for debugger
    }
}