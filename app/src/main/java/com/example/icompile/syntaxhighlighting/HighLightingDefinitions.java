package com.example.icompile.syntaxhighlighting;

import java.util.regex.Pattern;

public interface HighLightingDefinitions {

    Pattern getLinePattern();

    Pattern getNumberPattern();

    Pattern getPreprocessorPattern();

    Pattern getKeywordPattern();

    Pattern getBuiltinsPattern();

    Pattern getCommentsPattern();

    Pattern getStringPattern();

    Pattern getSymbolPattern();

    Pattern getIdentifierPattern();

}

