package com.example.icompile.ui

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.icompile.R
import com.example.icompile.core.Scanner
import com.example.icompile.data.InjectorUtils
import com.example.icompile.databinding.ActivityMainBinding
import com.example.icompile.syntaxhighlighting.definitions.KotlinHighlightingDefinition
import com.example.icompile.ui.viewmodel.EditorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.definitions.JavaHighlightingDefinition


class MainActivity : AppCompatActivity() {

    private lateinit var onCloseListener: SearchView.OnCloseListener

    private lateinit var queryTextListener: SearchView.OnQueryTextListener

    private lateinit var viewModel: EditorViewModel

    private val scanner = Scanner()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        setSupportActionBar(binding.toolbar)

        bindViews()


        binding.toolbar.setTitleTextAppearance(this, R.style.titleStyle)

        val factory = InjectorUtils.provideCodeRepository()

        obtainUserPermissions()

        viewModel = ViewModelProviders.of(this, factory)
            .get(EditorViewModel::class.java)

        bindData()
//        viewModel.bottomShortCut.observe(this, Observer { isVisible ->
//            isVisible.let { binding.keyboardShortcut = it }
//        })

    }

    private fun obtainUserPermissions() {

        val permissionArrays = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissions(permissionArrays, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // if user has withdrawn the permissions , obtain again
                    obtainUserPermissions()

                } else {
                    bindData()
                }
            }

        }
    }

    private fun bindData() {

        viewModel.codeTextUi.observe(this, Observer {
            it?.let { text ->
                binding.code = text
                scanner.setText(text)
            }
        })

        viewModel.getCode()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.btn_search)?.actionView as SearchView).apply {

            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
            queryHint = "Search document"


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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.btn_run -> {
                // getContentType()
                if (binding.content.text != null && !TextUtils.isEmpty(binding.content.text)) {

                    val skipUnread = scanner.skipInt()

                    val view = layoutInflater.inflate(R.layout.bottom_sheet_output, null)
                    val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)


                    // set the title of result
                    val title = view.findViewById<TextView>(R.id.title)
                    if (scanner.isError) {
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
                viewModel.setCode(binding.content.text.toString())
                scanner.setText(binding.content.text.toString())
                Toast.makeText(this, "Successfully saved!", Toast.LENGTH_LONG).show()
                true
            }

            R.id.btn_stop -> {
                scanner.reset()
                Toast.makeText(this, "Application stopped!", Toast.LENGTH_LONG).show()

                true
            }
            else -> false
        }
    }

    private fun bindViews() {

        binding.content.loadHighlightingDefinition(KotlinHighlightingDefinition())

        binding.btnTab.setOnClickListener {
            binding.content.insert("\t")
        }

        binding.btnCurlyBraceLeft.setOnClickListener {
            binding.content.insert("{")
        }

        binding.btnCurlyBraceRight.setOnClickListener {
            binding.content.insert("}")
        }

        binding.btnBraceLeft.setOnClickListener {
            binding.content.insert("(")
        }

        binding.btnBraceRight.setOnClickListener {
            binding.content.insert(")")
        }

        binding.btnQuotation.setOnClickListener {
            binding.content.insert("\"")
        }

        binding.btnSemiColon.setOnClickListener {
            binding.content.insert(";")
        }

        binding.btnArrow.setOnClickListener {
            binding.content.insert("->")

        }

        binding.btnLessThan.setOnClickListener {
            binding.content.insert("<")
        }

        binding.btnGreaterThan.setOnClickListener {
            binding.content.insert(">")
        }

        binding.btnSlash.setOnClickListener {
            binding.content.insert("/")

        }

        binding.btnBackSlash.setOnClickListener {
            binding.content.insert("\\")
        }

        binding.btnColon.setOnClickListener {
            binding.content.insert(":")
        }

        binding.btnQuestionMark.setOnClickListener {
            binding.content.insert("?")
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

}
