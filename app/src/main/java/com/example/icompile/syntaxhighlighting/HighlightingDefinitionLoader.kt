package com.example.icompile.syntaxhighlighting
import com.example.icompile.syntaxhighlighting.definitions.*

/**
 * Author: 0xFireball
 */
class HighlightingDefinitionLoader {

    fun selectDefinitionFromFileExtension(selectedFileExt: String): HighLightingDefinitions {
        when (selectedFileExt) {

            "js" -> return JavaScriptHighlightingDefinition()

            "java" -> return JavaHighlightingDefinition()

            "cpp", "cxx" -> return CPlusPlusHighlightingDefinition()

            "txt" -> return NoHighlightingDefinition()

            "kt" -> return KotlinHighlightingDefinition()

            else -> {

                return GenericHighlightingDefinition()
            }
        }
    }
}