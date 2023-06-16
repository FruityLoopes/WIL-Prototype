package com.example.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // Initialize Firebase Auth
        auth = Firebase.auth

        val reg: Button = findViewById(R.id.btnSubmitRegister)
        reg.setOnClickListener{
            performRegister()
        }

        val log: Button = findViewById(R.id.btnReturnLogin)
        log.setOnClickListener{
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val email = findViewById<EditText>(R.id.txtEmailRegister)
        val password = findViewById<EditText>(R.id.txtPasswordRegister)

        if(email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this , "Please fill all fields" , Toast.LENGTH_SHORT).show()
            return
        }
        val inputEmail = email.text.toString()
        val inputPassword = password.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, login::class.java)
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
}