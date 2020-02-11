package com.example.icompile.syntaxhighlighting
import com.example.icompile.syntaxhighlighting.definitions.KotlinHighlightingDefinition
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.definitions.*

/**
 * Author: 0xFireball
 */
class HighlightingDefinitionLoader {

    fun selectDefinitionFromFileExtension(selectedFileExt: String): HighLightingDefinitions {
        when (selectedFileExt) {

            "js" -> return JavaScriptHighlightingDefinition()

            "java" -> return JavaHighlightingDefinition()

            "cs" -> return CSharpHighlightingDefinition()

            "cpp", "cxx" -> return CPlusPlusHighlightingDefinition()

            "lua" -> return LuaHighlightingDefinition()

            "py" -> return PythonHighlightingDefinition() //Not yet ready!

            "txt" -> return NoHighlightingDefinition()

            "kt" -> return KotlinHighlightingDefinition()

            else -> {

                return GenericHighlightingDefinition()
            }
        }
    }
}