package com.example.icompile.codegen

import com.example.icompile.codegen.Codes.ByteCodes

/**
 * LabelOpcode class records bytecodes with associated labels
 * e.g. LABEL xyz
 */
class LabelOpcode
/**
 * @param code is the bytecode being created
 * @param label is the string representation of the label of interest
 */(code: ByteCodes?, var label: String) :
    Code(code) {
    override fun print() {
        println(toString())
    }

    override fun toString(): String {
        return super.toString() + " " + label
    }

}