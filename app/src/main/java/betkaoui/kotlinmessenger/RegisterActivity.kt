package betkaoui.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import betkaoui.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener {
            performRegister()
        }

        tv_already_have_an_account.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_select_photo.setOnClickListener{
            Log.d("RegisterActivity", "Try to show photos selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK && data != null) {
                // Proceed and check what the selected image was...
                Log.d("RegisterActivity", "Photo was selected")

                selectedPhotoUri = data.data
                Log.d("RegisterActivity", "Photo uri : ${selectedPhotoUri.toString()}")
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                /*val bitmapDrawable = BitmapDrawable(this.resources,bitmap)
                //btn_select_photo.setBackgroundDrawable(bitmapDrawable)
                btn_select_photo.background = bitmapDrawable
                */

                val inputStream = selectedPhotoUri?.let { contentResolver.openInputStream(it) }
                val drawable = Drawable.createFromStream(inputStream, selectedPhotoUri.toString())
                //btn_select_photo.background = drawable
                btn_select_photo.text = ""
                btn_select_photo.alpha = 0f
                civ_select_photo.setImageBitmap(bitmap)
            }
            else
            {
                Log.d("RegisterActivity:", "Photo picker canceled");
            }

        }
    }

    private fun performRegister() {
        val email = et_email.text.toString()
        val password = et_password.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is : " + email)
        Log.d("RegisterActivity", "Password : $password")


        // Firebase Authentication

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    Log.d("RegisterActivity", "Path : ${selectedPhotoUri.toString()}")
                    uploadImagetoFirebaseStorage()
                    //Log.d("RegisterActivity2", "Path : ${selectedPhotoUri.toString()}")
                    //saveUserToFirebaseDatabase(selectedPhotoUri.toString())
                }
                else {
                    Log.d("AuthFail", task.result.toString())
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "failed to create user : ${it.message}")
                Toast.makeText(
                    baseContext,
                    "Failed to create user : ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun uploadImagetoFirebaseStorage(){
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image : ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener { it ->
                        Log.d("RegisterActivity", "File location : $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
            .addOnFailureListener{
                // do some logging here
                Log.d("RegisterActivity", "Uploading of image failed !!!")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, et_username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved to the user to Firebase Database")

                val intent = Intent(this, LatestMessagesAcitivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Message : ${it.message}")
            }

    }
}
