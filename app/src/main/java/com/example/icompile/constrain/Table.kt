package com.example.icompile.constrain

import com.example.icompile.lexer.Symbol
import java.util.*

/** <pre>
 * Binder objects group 3 fields
 * 1. a value
 * 2. the next link in the chain of symbols in the current scope
 * 3. the next link of a previous Binder for the same identifier
 * in a previous scope
</pre> *
 */
internal class Binder(
    val value: Any?,
    p: Symbol,
    t: Binder?
) {
    private val prevtop // prior symbol in same scope
            : Symbol = p
    val tail // prior binder for same symbol
            : Binder? = t

    fun getPrevtop(): Symbol {
        return prevtop
    }

}

/** <pre>
 * The Table class is similar to java.util.Dictionary, except that
 * each key must be a Symbol and there is a scope mechanism.
 *
 * Consider the following sequence of events for table t:
 * t.put(Symbol("a"),5)
 * t.beginScope()
 * t.put(Symbol("b"),7)
 * t.put(Symbol("a"),9)
 *
 * symbols will have the key/value pairs for Symbols "a" and "b" as:
 *
 * Symbol("a") ->
 * Binder(9, Symbol("b") , Binder(5, null, null) )
 * (the second field has a reference to the prior Symbol added in this
 * scope; the third field refers to the Binder for the Symbol("a")
 * included in the prior scope)
 * Binder has 2 linked lists - the second field contains list of symbols
 * added to the current scope; the third field contains the list of
 * Binders for the Symbols with the same string id - in this case, "a"
 *
 * Symbol("b") ->
 * Binder(7, null, null)
 * (the second field is null since there are no other symbols to link
 * in this scope; the third field is null since there is no Symbol("b")
 * in prior scopes)
 *
 * top has a reference to Symbol("a") which was the last symbol added
 * to current scope
 *
 * Note: What happens if a symbol is defined twice in the same scope??
</pre> *
 */
class Table  // marks - push for new scope; pop when closing
// scope
/*
    public static void main(String args[]) {
        Symbol s = Symbol.symbol("a", 1),
            s1 = Symbol.symbol("b", 2),
            s2 = Symbol.symbol("c", 3);

        Table t = new Table();
        t.beginScope();
        t.put(s,"top-level a");
        t.put(s1,"top-level b");
        t.beginScope();
        t.put(s2,"second-level c");
        t.put(s,"second-level a");
        t.endScope();
        t.put(s2,"top-level c");
        t.endScope();
}

*/ {
    private val symbols: HashMap<Symbol, Binder?> =
        HashMap<Symbol, Binder?>()
    private var top // reference to last symbol added to
            : Symbol? = null

    // current scope; this essentially is the
    // start of a linked list of symbols in scope
    private var marks // scope mark; essentially we have a stack of
            : Binder? = null

    /**
     * Gets the object associated with the specified symbol in the Table.
     */
    operator fun get(key: Symbol): Any? {
        val e = symbols[key]
        return e?.value
    }

    /**
     * Puts the specified value into the Table, bound to the specified Symbol.<br></br>
     * Maintain the list of symbols in the current scope (top);<br></br>
     * Add to list of symbols in prior scope with the same string identifier
     */
    fun put(key: Symbol, value: Any?) {
        symbols[key] = top?.let { Binder(value, it, symbols[key]) }
        top = key
    }

    /**
     * Remembers the current state of the Table; push new mark on mark stack
     */
    fun beginScope() {
        marks = top?.let { Binder(null, it, marks) }
        top = null
    }

    /**
     * Restores the table to what it was at the most recent beginScope
     * that has not already been ended.
     */
    fun endScope() {
        while (top != null) {

            val e : Binder? = symbols.get(top as Symbol)

            if (e?.tail != null)
                symbols[top!!] = e.tail else symbols.remove(top as Symbol)

            top = e?.getPrevtop()
        }

        top = marks?.getPrevtop()

        marks = marks?.tail
    }
    /**
     * @return a set of the Table's symbols.
     */
    fun keys(): Set<Symbol> {
        return symbols.keys
    }
}