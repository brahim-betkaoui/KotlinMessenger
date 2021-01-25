package betkaoui.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener{
            val email = et_login_email.text.toString()
            val password = et_login_password.text.toString()

            Log.d("Login", "Attemp to login with email/pw: $email/***")
        }

        tv_back_to_register.setOnClickListener{
            finish()
        }

    }
}