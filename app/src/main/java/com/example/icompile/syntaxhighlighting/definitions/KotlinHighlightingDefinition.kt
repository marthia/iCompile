package com.example.icompile.syntaxhighlighting.definitions

import com.example.icompile.syntaxhighlighting.HighLightingDefinitions
import java.util.regex.Pattern

class KotlinHighlightingDefinition :
    HighLightingDefinitions {

    override fun getLinePattern(): Pattern {
        return PATTERN_LINE
    }

    override fun getNumberPattern(): Pattern {
        return PATTERN_NUMBERS
    }

    override fun getPreprocessorPattern(): Pattern {
        return PATTERN_PREPROCESSOR
    }

    override fun getKeywordPattern(): Pattern {
        return PATTERN_KEYWORDS
    }

    override fun getBuiltinsPattern(): Pattern {
        return PATTERN_BUILTINS
    }

    override fun getCommentsPattern(): Pattern {
        return PATTERN_COMMENTS
    }

    override fun getStringPattern(): Pattern {
        return PATTERN_STRING
    }

    override fun getSymbolPattern(): Pattern {
        return PATTERN_SYMBOL
    }

    override fun getIdentifierPattern(): Pattern {
        return PATTERN_IDENTIFIER
    }

    companion object {
        private val PATTERN_LINE = Pattern.compile(".*\\n")

        private val PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b")

        private val PATTERN_PREPROCESSOR = Pattern.compile(
            "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" +
                    "#error|#pragma|#extension|#version|#line)\\b",
            Pattern.MULTILINE
        )

        private val PATTERN_STRING = Pattern.compile("\"((\\\\[^\\n]|[^\"\\n])*)\"")

        private val PATTERN_KEYWORDS = Pattern.compile(
            "\\b(fun|try|break|return|override|String|Int|Double|Float|Char|val|var|this|object|" +
                    "if|if|else|when|is|for|while|inline|lateinit|true|false|as|in|interface|class|" +
                    "constructor|init|open|companion|super|throw|catch|typeof|get|by|lazy|import|" +
                    "property|set|receiver|where|setparam|external|data|abstract|public|private|" +
                    "protected|sealed|suspend|out|internal|field" +
                    ")\\b"
        )

        private val PATTERN_BUILTINS = Pattern.compile(
            "\\b(Int|Long|Float|Double|Boolean|Char|Object|String)\\b"
        )
        private val PATTERN_COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")

        private val PATTERN_SYMBOL =
            Pattern.compile("(\\{|\\}\\)|\\()") //TODO: Are we sure about this?

        private val PATTERN_IDENTIFIER = Pattern.compile(
            "((?<=class)\\s\\w*)|" +
                    "((?<=import)\\s(\\w|\\.)+[^\n])|" + //Match everything between import and semicolon

                    "((?<=package)\\s(\\w|\\.)+[^\n])"
        )
    }
}

