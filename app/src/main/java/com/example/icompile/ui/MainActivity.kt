package com.example.icompile.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var parser: CoreTextParser
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val viewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)

        parser = CoreTextParser()

        binding.content.append(
            loadTextFromFile(
                "${getExternalFilesDir(
                    null
                )}/iCompile/main.txt"
            ).orEmpty()
        )

        bindViews()

//        viewModel.bottomShortCut.observe(this, Observer { isVisible ->
//            isVisible.let { binding.keyboardShortcut = it }
//        })

    }

    private fun bindViews() {
        binding.btnRun.setOnClickListener {
            // getContentType()
            if (binding.content.text != null && !TextUtils.isEmpty(binding.content.text)) {

                parser.setText(binding.content.text.toString())
                val skipUnread = parser.skipUnread()


                val view = layoutInflater.inflate(R.layout.bottom_sheet_output, null)
                val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)

                view.findViewById<TextView>(R.id.output).text = skipUnread
                dialog.setContentView(view)
                dialog.show()
            }
        }

        binding.btnSave.setOnClickListener {
            // create our own folder for any file : could be asked from user in future
            if (!File("${getExternalFilesDir(null)}/iCompile").exists()) {
                File("${getExternalFilesDir(null)}/iCompile").mkdir()
            }

            val result = saveTextFile(
                binding.content.text.toString(),
                "${getExternalFilesDir(null)}/iCompile/main.txt"
            )
            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        }

        binding.btnTab.setOnClickListener {
            binding.content.append("    ")
        }

        binding.btnCurlyBraceLeft.setOnClickListener {
            binding.content.append("{")
        }

        binding.btnCurlyBraceRight.setOnClickListener {
            binding.content.append("}")
        }

        binding.btnBraceLeft.setOnClickListener {
            binding.content.append("(")
        }

        binding.btnBraceRight.setOnClickListener {
            binding.content.append(")")
        }

        binding.btnQuotation.setOnClickListener {
            binding.content.append("\"")
        }

        binding.btnSemiColon.setOnClickListener {
            binding.content.append(";")
        }

        binding.btnArrow.setOnClickListener {
            binding.content.append("->")

        }

        binding.btnLessThan.setOnClickListener {
            binding.content.append("<")
        }

        binding.btnGreaterThan.setOnClickListener {
            binding.content.append(">")
        }

        binding.btnSlash.setOnClickListener {
            binding.content.append("/")

        }

        binding.btnBackSlash.setOnClickListener {
            binding.content.append("\\")
        }

        binding.btnColon.setOnClickListener {
            binding.content.append(":")
        }

        binding.btnQuestionMark.setOnClickListener {
            binding.content.append("?")
        }
    }

    private fun getContentType() {

    }

}
