package com.example.icompile.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.icompile.R
import com.example.icompile.syntaxhighlighting.GenericHighlightingDefinition
import com.example.icompile.syntaxhighlighting.HighLightingDefinitions
import java.util.regex.Pattern
import kotlin.math.roundToInt

class SyntaxHighlightingEditText : AppCompatEditText {

    interface OnTextChangedListener {
        fun onTextChanged(text: String)
    }

    private var highlightingDefinition: HighLightingDefinitions =
        GenericHighlightingDefinition()

    private val mLineBounds = Rect()

    private val mPaintHighlight = Paint().apply {

        isAntiAlias = false
        style = Paint.Style.FILL
        color = Color.parseColor("#24b89300")

    }

    private var mHighlightedLine = -1

    private var mHighlightStart = -1

    private val rect = Rect()

    private val paint: Paint = Paint().apply {

        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#646464")
        textSize =
            resources.getDimensionPixelSize(R.dimen.editor_line_number_text_size)
                .toFloat()

    }


    private val updateHandler = Handler()

    private val updateRunnable = Runnable {
        val e = text

        if (onTextChangedListener != null)
            onTextChangedListener!!.onTextChanged(
                e.toString()
            )

        highlightWithoutChange(e!!)
    }

    private var onTextChangedListener: OnTextChangedListener? = null

    private var updateDelay = 1000

    private var errorLine = 0

    private var modified = true

    private var colorError: Int = 0

    private var colorNumber: Int = 0

    private var colorKeyword: Int = 0

    private var colorBuiltin: Int = 0

    private var colorComment: Int = 0

    private var colorString: Int = 0

    private var colorSymbol: Int = 0

    private var colorIdentifier: Int = 0

    private var tabWidthInCharacters = 0

    private var tabWidth = 0

    constructor(context: Context) : super(context) {

        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        var baseline = baseline

        computeLineHighlight()

        for (i in 0 until lineCount) {
            paint.let {
                canvas.drawText(
                    String.format(" %3d ", i + 1), rect.left.toFloat(),
                    baseline.toFloat(), it
                )
            }
            if ((i == mHighlightedLine)) {
                getLineBounds(mHighlightedLine, mLineBounds)
                mLineBounds.left = 0 // make sure the whole gets highlighted
                canvas.drawRect(mLineBounds, mPaintHighlight)
            }

            baseline += lineHeight
        }

    }

    /**
     * Compute the line to highlight based on selection
     */
    private fun computeLineHighlight() {
        var i: Int
        var line: Int
        val selStart: Int = selectionStart
        val text: String

        if (!isEnabled) {
            return
        }

        if (mHighlightStart != selStart) {
            text = getText()!!.toString()

            i = 0
            line = i
            while (i < selStart) {
                i = text.indexOf("\n", i)
                if (i < 0) {
                    break
                }
                if (i < selStart) {
                    ++line
                }
                ++i
            }

            mHighlightedLine = line
        }
    }

    fun isModified(): Boolean {
        return modified
    }

    fun setOnTextChangedListener(listener: OnTextChangedListener) {
        onTextChangedListener = listener
    }

    private fun setUpdateDelay(ms: Int) {
        updateDelay = ms
    }

    private fun setTabWidth(characters: Int) {
        if (tabWidthInCharacters == characters)
            return

        tabWidthInCharacters = characters
        tabWidth = (paint.measureText("m") * characters).roundToInt()
    }

    fun hasErrorLine(): Boolean {
        return errorLine > 0
    }

    fun setErrorLine(line: Int) {
        errorLine = line
    }

    fun updateHighlighting() {
        highlightWithoutChange(text!!)
    }

    fun loadHighlightingDefinition(newHighlightingDefinition: HighLightingDefinitions) {
        this.highlightingDefinition = newHighlightingDefinition
    }

    fun setTextHighlighted(hl_text: CharSequence?) {
        var text = hl_text
        if (text == null)
            text = ""

        cancelUpdate()

        errorLine = 0
        modified = false
        setText(highlight(SpannableStringBuilder(text)))
        modified = true

        if (onTextChangedListener != null)
            onTextChangedListener!!.onTextChanged(text.toString())
    }

    val cleanText: String
        get() = PATTERN_TRAILING_WHITE_SPACE
            .matcher(text)
            .replaceAll("")

    fun insert(s: String) {
        val start = selectionStart
        val end = selectionEnd

        text?.replace(
            start.coerceAtMost(end),
            start.coerceAtLeast(end),
            s,
            0,
            1
        )
    }

    fun addUniform(uniform_statement: String?) {
        var statement: String? = uniform_statement ?: return

        val e = text
        val m = PATTERN_INSERT_UNIFORM.matcher(e)
        var start: Int

        if (m.find())
            start = 0.coerceAtLeast(m.end() - 1)
        else {
            // add an empty line between the last #endif
            // and the now following uniform
            start = endIndexOfLastEndIf(e!!)
            if (start > -1)
                statement = "\n" + statement

            // move index past line break or to the start
            // of the text when no #endif was found
            ++start
        }

        e?.insert(start, "$statement;\n")
    }

    private fun endIndexOfLastEndIf(e: Editable): Int {
        val m = PATTERN_ENDIF.matcher(e)
        var idx = -1

        while (m.find())
            idx = m.end()

        return idx
    }

    private fun init(context: Context) {
        setHorizontallyScrolling(true)

        filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            if (modified &&
                end - start == 1 &&
                start < source.length &&
                dstart < dest.length
            ) {
                val c = source[start]

                if (c == '\n')
                    return@InputFilter autoIndent(
                        source,
                        dest,
                        dstart,
                        dend
                    )
            }

            source
        })

        addTextChangedListener(
            object : TextWatcher {
                private var start = 0
                private var count = 0

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    this.start = start
                    this.count = count
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(e: Editable) {
                    cancelUpdate()
                    convertTabs(e, start, count)

                    if (!modified)
                        return

                    modified = true
                    updateHandler.postDelayed(
                        updateRunnable,
                        updateDelay.toLong()
                    )
                }
            })

        setSyntaxColors(context)
        /*
        setUpdateDelay(
                ShaderEditorApplication
                        .preferences
                        .getUpdateDelay());
        setTabWidth(
                ShaderEditorApplication
                        .preferences
                        .getTabWidth());
                        */
        setUpdateDelay(500)
        setTabWidth(4)
    }

    private fun setSyntaxColors(context: Context) {
        colorError = ContextCompat.getColor(
            context,
            R.color.syntax_error
        )
        colorNumber = ContextCompat.getColor(
            context,
            R.color.syntax_number
        )
        colorKeyword = ContextCompat.getColor(
            context,
            R.color.syntax_keyword
        )
        colorBuiltin = ContextCompat.getColor(
            context,
            R.color.syntax_builtin
        )
        colorComment = ContextCompat.getColor(
            context,
            R.color.syntax_comment
        )
        colorString = ContextCompat.getColor(
            context,
            R.color.syntax_string
        )
        colorSymbol = ContextCompat.getColor(
            context,
            R.color.syntax_symbol
        )
        colorIdentifier = ContextCompat.getColor(
            context,
            R.color.syntax_identifier
        )
    }

    private fun cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable)
    }

    private fun highlightWithoutChange(e: Editable) {
        modified = false
        highlight(e)
        modified = true
    }

    private fun highlight(e: Editable): Editable {
        try {
            // don't use e.clearSpans() because it will
            // remove too much
            clearSpans(
                e
            )

            if (e.isEmpty())
                return e

            if (errorLine > 0) {
                val m = highlightingDefinition.linePattern.matcher(e)

                var n = errorLine
                while (n-- > 0 && m.find())

                    e.setSpan(
                        BackgroundColorSpan(colorError),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.numberPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorNumber),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.preprocessorPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorKeyword),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.keywordPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorKeyword),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.builtinsPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorBuiltin),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.commentsPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorComment),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.stringPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorString),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            run {
                val m = highlightingDefinition.symbolPattern.matcher(e)
                while (m.find())
                    e.setSpan(
                        ForegroundColorSpan(colorSymbol),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
            }

            val m = highlightingDefinition.identifierPattern.matcher(e)
            while (m.find())
                e.setSpan(
                    ForegroundColorSpan(colorIdentifier),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

        } catch (ex: IllegalStateException) {
            // raised by Matcher.start()/.end() when
            // no successful match has been made what
            // shouldn't ever happen because of find()
        }

        return e
    }

    private fun autoIndent(
        source: CharSequence,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        var indent = ""
        var istart = dstart - 1

        // find start of this line
        var dataBefore = false
        var pt = 0

        while (istart > -1) {
            val c = dest[istart]

            if (c == '\n')
                break

            if (c != ' ' && c != '\t') {
                if (!dataBefore) {
                    // indent always after those characters
                    if (c == '{' ||
                        c == '+' ||
                        c == '-' ||
                        c == '*' ||
                        c == '/' ||
                        c == '%' ||
                        c == '^' ||
                        c == '='
                    )
                        --pt

                    dataBefore = true
                }

                // parenthesis counter
                if (c == '(')
                    --pt
                else if (c == ')')
                    ++pt
            }
            --istart
        }

        // copy indent of this line into the next
        if (istart > -1) {
            val charAtCursor = dest[dstart]

            var iend: Int = ++istart
            while (iend < dend) {
                val c = dest[iend]

                // auto expand comments
                if (charAtCursor != '\n' &&
                    c == '/' &&
                    iend + 1 < dend &&
                    dest[iend] == c
                ) {
                    iend += 2
                    break
                }

                if (c != ' ' && c != '\t')
                    break
                ++iend
            }

            indent += dest.subSequence(istart, iend)
        }

        // add new indent
        if (pt < 0)
            indent += "\t"

        // append white space of previous line and new indent
        return source.toString() + indent
    }

    private fun convertTabs(e: Editable, start_pos: Int, count: Int) {
        var start = start_pos
        if (tabWidth < 1)
            return

        val s = e.toString()

        val stop = start + count
        start = s.indexOf("\t", start)
        while (start > -1 && start < stop) {
            e.setSpan(
                TabWidthSpan(),
                start,
                start + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            ++start
        }
    }

    private inner class TabWidthSpan : ReplacementSpan() {
        override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
        ): Int {
            return tabWidth
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
        }
    }

    companion object {

        private val PATTERN_TRAILING_WHITE_SPACE = Pattern.compile(
            "[\\t ]+$",
            Pattern.MULTILINE
        )
        private val PATTERN_INSERT_UNIFORM = Pattern.compile(
            "\\b(uniform[a-zA-Z0-9_ \t;\\[\\]\r\n]+[\r\n])\\b",
            Pattern.MULTILINE
        )
        private val PATTERN_ENDIF = Pattern.compile(
            "(#endif)\\b"
        )

        private fun clearSpans(e: Editable) {
            // remove foreground color spans
            run {
                val spans = e.getSpans(
                    0,
                    e.length,
                    ForegroundColorSpan::class.java
                )

                var n = spans.size
                while (n-- > 0)
                    e.removeSpan(spans[n])
            }

            // remove background color spans
            run {
                val spans = e.getSpans(
                    0,
                    e.length,
                    BackgroundColorSpan::class.java
                )

                var n = spans.size
                while (n-- > 0)
                    e.removeSpan(spans[n])
            }
        }
    }
}