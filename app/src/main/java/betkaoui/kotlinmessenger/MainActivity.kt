package betkaoui.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_register.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            Log.d("MainActivity", "Email is : " + email)
            Log.d("MainActivity", "Password : $password")

            // Firebase Authentication
            // Initialize Firebase Auth
            auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task->
                    if (task.isSuccessful){
                        Log.d("Main", "createUserWithEmail:success")
                    }
                    else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        tv_already_have_an_account.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
}