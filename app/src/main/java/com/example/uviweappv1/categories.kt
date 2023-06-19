package com.example.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnAttendance: Button
lateinit var btnFinance: Button
lateinit var btnDonation: Button
class categories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        btnAttendance = findViewById(R.id.btnAttendance)
        btnFinance = findViewById(R.id.btnFinance)
        btnDonation = findViewById(R.id.btnDonation)


        //Directing to the Attendace page
        btnAttendance.setOnClickListener({
            val intent = Intent(this, Attendance::class.java)
            startActivity(intent)
        })

        //Directing to the Finance page
        btnFinance.setOnClickListener({
            val intent = Intent(this, finance::class.java )
            startActivity(intent)
        })

        //Directing to the Donation page
        btnDonation.setOnClickListener({
            val intent = Intent(this, donation::class.java)
            startActivity(intent)
        })
    }
}