package com.example.icompile.main

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
import com.example.icompile.R
import com.example.icompile.databinding.ActivityMainBinding
import com.example.icompile.syntaxhighlighting.definitions.JavaHighlightingDefinition
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {

    private var isDataSavedToFile = false

    private lateinit var onCloseListener: SearchView.OnCloseListener

    private lateinit var queryTextListener: SearchView.OnQueryTextListener

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // defining the data-binding object
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        // set for customizing the toolbar
        setSupportActionBar(binding.toolbar)

        // init all views
        bindViews()

        // populate the editor from file
        populateEditor()

        // set title with a custom font
        binding.toolbar.setTitleTextAppearance(this, R.style.titleStyle)

    }

    private fun populateEditor() {

        binding.editor = CodeBll.getCode()
    }

    // obtain the required permissions for I/O actions
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
                    CodeBll.getCode()
                }
            }

        }
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

                    val view = layoutInflater.inflate(R.layout.bottom_sheet_output, null)
                    val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)

                    val result = CodeBll.run()
                    // set the title of result
                    val title = view.findViewById<TextView>(R.id.title)
                    if (result.contains("Error", true)) {
                        title.text = "ERROR LOG"
                        title.setTextColor(Color.parseColor("#A81A50"))
                    } else {
                        title.text = "INTERMEDIATE CODE RESULT"
                        title.setTextColor(Color.parseColor("#46A37E"))
                    }


                    view.findViewById<TextView>(R.id.output).text = result
                    dialog.setContentView(view)
                    dialog.show()
                }

                true
            }

            R.id.btn_save -> {
                saveData()
                true
            }

            R.id.btn_stop -> {
//                scanner.reset()
                Toast.makeText(this, "Application stopped!", Toast.LENGTH_LONG).show()

                true
            }
            else -> false
        }
    }

    private fun saveData() {

        val code = binding.content.text.toString()

        Toast.makeText(
                this,

                CodeBll.saveCode(code),

                Toast.LENGTH_LONG
            )

            .show()
    }

    private fun bindViews() {

        binding.content.loadHighlightingDefinition(JavaHighlightingDefinition())

        binding.btnTab.setOnClickListener {
            binding.content.insert("    ")
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

}
