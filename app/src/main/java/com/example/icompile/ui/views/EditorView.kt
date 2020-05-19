package com.example.icompile.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.icompile.Constants.COMMENT
import com.example.icompile.Constants.COMMENT_COLOR
import com.example.icompile.Constants.KEYWORD_COLOR
import com.example.icompile.Constants.KOTLIN_KEYWORDS
import com.example.icompile.R

class EditorView : AppCompatEditText {

    // using `@JvmOverloads` generates unwanted bugs and styling issues
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        init(context, attrs, defStyleAttr)
    }

    private var editorWrapView = false
    private val mLineBounds = Rect()
    private val mPaintHighlight = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.FILL
        color = Color.parseColor("#45e7e8d1") // line color
    }
    private var mHighlightedLine = -1
    private var mHighlightStart = -1

    private val rect = Rect()
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#000000") // line number color
        textSize =
            resources.getDimensionPixelSize(R.dimen.editor_line_number_text_size)
                .toFloat()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Load the styled attributes and set their properties
        val attributes =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.EditorView, defStyleAttr, 0
            )
        // is the view wrapped
        editorWrapView =
            attributes.getBoolean(R.styleable.EditorView_editor_wrap_text, false)

        attributes.recycle()
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
                canvas.drawRect(mLineBounds, mPaintHighlight);
            }

            baseline += lineHeight
        }

    }

//   override fun onTextChanged(
//        text: CharSequence?,
//        start: Int,
//        lengthBefore: Int,
//        lengthAfter: Int
//    ) {
//        super.onTextChanged(text, start, lengthBefore, lengthAfter)
//                text?.forEach {
//                    KOTLIN_KEYWORDS.forEach {
//                        val index = text.indexOf(it, start)
//                        if (index != -1) {
//                            getText()?.setSpan(
//                                ForegroundColorSpan(Color.parseColor(KEYWORD_COLOR)),
//                                index,
//                                index + it.length,
//                                Spannable.SPAN_INTERMEDIATE
//                            )
//                        }
//                    }
//                }
//
//        val index = getText().toString().indexOf(COMMENT)
//        if (index != -1) {
//            getText()?.setSpan(
//                ForegroundColorSpan(Color.parseColor(COMMENT_COLOR)),
//                index,
//                getText()!!.indexOf("\n", index),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//    }

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
    fun insert(s: String) {
        val start = selectionStart
        val end = selectionEnd

        text?.replace(
            Math.min(start, end),
            Math.max(start, end),
            s,
            0,
            1
        )
    }

}