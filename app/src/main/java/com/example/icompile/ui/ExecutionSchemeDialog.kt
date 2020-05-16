package com.example.icompile.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.icompile.R
import kotlinx.android.synthetic.main.dialog_execution_scheme.*

class ExecutionSchemeDialog(context: Context, private val e: OnExecutionResponse) :
    Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_execution_scheme)

        arithmetic_btn.setOnClickListener {
            e.doAction(ExecutionScheme.ARITHMETIC)
            dismiss()
        }

        parenthesis_depth_btn.setOnClickListener {
            e.doAction(ExecutionScheme.DEPTH)
            dismiss()
        }

        regular_expression_btn.setOnClickListener {
            e.doAction(ExecutionScheme.REGULAR)
            dismiss()
        }

        expressions_btn.setOnClickListener {
            e.doAction(ExecutionScheme.EXPRESSIONS_IC)
            dismiss()
        }
    }
}