package com.example.icompile.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.icompile.R
import kotlinx.android.synthetic.main.activity_login.*
import me.marthia.icompile.auth.UserBll
import me.marthia.icompile.util.AppDatabase

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setColorBackground()

        if (checkIfFirstTime()) {

            title_txt.text = "Sign Up"
            login_btn.text = "Sign Up"

            login_btn.setOnClickListener {
                val database = AppDatabase.getInstance(this)
                val userBll = UserBll(database.userDao())
                userBll.signUp(username_field.text.toString(), password_field.text.toString())

                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            }
        } else {
            title_txt.text = "Login"
            login_btn.text = "Login"
            signup_txt.visibility = View.VISIBLE
            login_btn.setOnClickListener {

                val database = AppDatabase.getInstance(this)
                val userBll = UserBll(database.userDao())
                val userList =
                    userBll.login(username_field.text.toString(), password_field.text.toString())

                userList.observe(this@SignUpActivity, Observer {
                    if (it.isNotEmpty()) {
                        val preferences = getSharedPreferences("Auth", Context.MODE_PRIVATE)
                        preferences.edit().putString(it.get(0).username, "").apply()
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    } else

                        Toast.makeText(
                            this,
                            "User not found, username or password is wrong!",
                            Toast.LENGTH_LONG
                        ).show()
                })
            }
        }
    }

    private fun checkIfFirstTime(): Boolean {

        val sharedPreferences = getSharedPreferences("Auth", Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean("isFirst", true)
    }

    fun onSignUp(view: View) {
        val sharedPreferences = getSharedPreferences("Auth", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isFirst", true).commit()
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun setColorBackground() {
        val greenGradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#a7beae"),
                Color.parseColor("#CDE0C9"),
                Color.parseColor("#E0ECDE")
            )
        )
        background.background = greenGradient
    }
}