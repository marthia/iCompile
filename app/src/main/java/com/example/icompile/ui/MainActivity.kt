package com.example.icompile.ui

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.icompile.R
import com.example.icompile.core.CoreTextParser
import com.example.icompile.core.loadTextFromFile
import com.example.icompile.core.saveTextFile
import com.example.icompile.databinding.ActivityMainBinding
import com.example.icompile.ui.viewmodel.EditorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var onCloseListener: SearchView.OnCloseListener
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private lateinit var viewModel: EditorViewModel
    private lateinit var parser: CoreTextParser
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)

        parser = CoreTextParser()

        binding.content.append(
            loadTextFromFile(
                "${getExternalFilesDir(
                    null
                )}/iCompile/main.txt"
            ).orEmpty()
        )

        parser.setText(binding.content.text.toString())

        bindViews()

//        viewModel.bottomShortCut.observe(this, Observer { isVisible ->
//            isVisible.let { binding.keyboardShortcut = it }
//        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.btn_search)?.actionView as SearchView).apply {

            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
            queryHint = "Search whole file"


            queryTextListener = object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean = false

                override fun onQueryTextSubmit(query: String): Boolean {

                    var indexOfQuery = binding.content.text?.indexOf(query, 0);
                    val wordToSpan: Spannable = SpannableString(binding.content.text)

                    var ofs = 0
                    while (ofs < binding.content.length() && indexOfQuery != -1) {


                        indexOfQuery = binding.content.text?.indexOf(query, ofs)
                        if (indexOfQuery == -1)
                            break;
                        else {

                            wordToSpan.setSpan(
                                BackgroundColorSpan(Color.YELLOW),
                                indexOfQuery!!,
                                indexOfQuery + query.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                            binding.content.setText(wordToSpan, TextView.BufferType.SPANNABLE)
                        }

                        ofs = indexOfQuery + 1
                    }
                    Log.i("onQueryTextSubmit", query)

                    return true
                }
            }

            onCloseListener = SearchView.OnCloseListener {
                binding.content.text?.clearSpans()
                false
            }

            setOnQueryTextListener(queryTextListener)

            setOnCloseListener(onCloseListener)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun reloadFromFile(): String {
        // create our own folder for any file : could be asked from user in future
        if (!File("${getExternalFilesDir(null)}/iCompile").exists()) {
            File("${getExternalFilesDir(null)}/iCompile").mkdir()
        }

        val result = saveTextFile(
            binding.content.text.toString(),
            "${getExternalFilesDir(null)}/iCompile/main.txt"
        )

        // reset to initial state and re-assign the changed text
        parser.reset()
        parser.setText(binding.content.text.toString())

        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.btn_run -> {
                // getContentType()
                if (binding.content.text != null && !TextUtils.isEmpty(binding.content.text)) {

                    val skipUnread = parser.skipInt()

                    val view = layoutInflater.inflate(R.layout.bottom_sheet_output, null)
                    val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)


                    // set the title of result
                    val title = view.findViewById<TextView>(R.id.title)
                    if (parser.isError) {
                        title.text = "ERROR LOG"
                        title.setTextColor(Color.parseColor("#A81A50"))
                    } else {
                        title.text = "Output"
                        title.setTextColor(Color.parseColor("#46A37E"))
                    }


                    view.findViewById<TextView>(R.id.output).text = skipUnread
                    dialog.setContentView(view)
                    dialog.show()
                }

                true
            }

            R.id.btn_save -> {
                val result = reloadFromFile()
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                true
            }

            R.id.btn_stop -> {
                parser.reset()
                Toast.makeText(this, "Application stopped!", Toast.LENGTH_LONG).show()

                true
            }
            else -> false
        }
    }

    private fun bindViews() {

       /* binding.content.addTextChangedListener(object : TextWatcher {


            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }


            override fun afterTextChanged(s: Editable) {

                var offset = 0
                val text = binding.content.text

                while ( text != null && offset < text.length) {
                    RESERVED.forEach {
                        val index = text.indexOf(it, offset)
                        if (index != -1) {
                            text.setSpan(
                                ForegroundColorSpan(Color.parseColor("#3b78e7")),
                                index,
                                index + it.length,
                                Spannable.SPAN_INTERMEDIATE
                            )
                        }
                    }



                        offset++
                }
               */

        /* val index = s.toString().indexOf(COMMENT)
                if (index != -1) {
                    s.setSpan(
                        ForegroundColorSpan(Color.parseColor("#d81b60")),
                        index,
                        s.indexOf("\n", index),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }*//*

            }
        })*/

        binding.btnTab.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "    ")
        }

        binding.btnCurlyBraceLeft.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "{")
        }

        binding.btnCurlyBraceRight.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "}")
        }

        binding.btnBraceLeft.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "(")
        }

        binding.btnBraceRight.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, ")")
        }

        binding.btnQuotation.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "\"")
        }

        binding.btnSemiColon.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, ";")
        }

        binding.btnArrow.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "->")

        }

        binding.btnLessThan.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "<")
        }

        binding.btnGreaterThan.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, ">")
        }

        binding.btnSlash.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "/")

        }

        binding.btnBackSlash.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "\\")
        }

        binding.btnColon.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, ":")
        }

        binding.btnQuestionMark.setOnClickListener {
            binding.content.text?.insert(binding.content.selectionStart, "?")
        }

    }

    fun findKeywordAtLine(text: String?): Int {
        val wholeText = binding.content.text.toString()
        val listOfLines = wholeText.split("\n")

        for ((index, line) in listOfLines.withIndex()) {

            if (text.let { line.contains(it.toString()) }) {
                return index

            }
        }
        return -1
    }

    private fun getContentType() {

    }

}
