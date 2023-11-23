package uviwe.app.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uviweappv1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale.Category

class login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var check = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val register: Button = findViewById(R.id.btnRegister)
        val login: Button = findViewById(R.id.btnLogin)
        // Initialize Firebase Auth
        auth = Firebase.auth
        checkFirebaseUser()


        login.setOnClickListener{
            performLogin()
        }
        register.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        if (check == true){
            finish()
        }
    }

    private fun performLogin(){

        val email = findViewById<EditText>(R.id.txtemail)
        val password = findViewById<EditText>(R.id.txtPassword)

        if(email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this , "Please fill all fields" , Toast.LENGTH_SHORT).show()
            return
        }

        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        auth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, categories::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{
                Toast.makeText(this,"Error occured ${it.localizedMessage}" , Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkFirebaseUser() {
        val firebaseUser: FirebaseUser? = auth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, categories::class.java)
            startActivity(intent)
        } else {
            check = false
        }
    }
}