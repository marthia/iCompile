package com.example.icompile.parser

import com.example.icompile.ast.AST

data class ParsingResult(val tree: AST?, val syntaxErrorLineNumber: String?)