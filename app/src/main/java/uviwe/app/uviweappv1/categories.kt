package uviwe.app.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.uviweappv1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class categories : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var btnAttendance: Button
    lateinit var btnFinance: Button
    lateinit var btnDonation: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        btnAttendance = findViewById(R.id.btnAttendance)
        btnFinance = findViewById(R.id.btnFinance)
        btnDonation = findViewById(R.id.btnDonation)
        auth = Firebase.auth
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        //Directing to the Attendace page
        btnAttendance.setOnClickListener() {
            val intent = Intent(this, Attendance::class.java)
            startActivity(intent)
        }

        //Directing to the Finance page
        btnFinance.setOnClickListener() {
            val intent = Intent(this, finance::class.java)
            startActivity(intent)
        }

        //Directing to the Donation page
        btnDonation.setOnClickListener() {
            val intent = Intent(this, donation::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener() {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        if (!isFinishing) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Logout")
            builder.setMessage("Are you sure you want to logout?")

            builder.setPositiveButton("Logout") { dialog, which ->
                auth.signOut()
                val intent = Intent(this, login::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->

            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}