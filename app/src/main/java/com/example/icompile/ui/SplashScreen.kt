package com.example.icompile.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.icompile.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({

            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()

        }, 2000)
    }
}