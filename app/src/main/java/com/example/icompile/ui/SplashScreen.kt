package com.example.icompile.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.icompile.R

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        // Hide status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler().postDelayed({

            startActivity(Intent(this@SplashScreen, SignUpActivity::class.java))
            finish()

        }, 2000)
    }
}