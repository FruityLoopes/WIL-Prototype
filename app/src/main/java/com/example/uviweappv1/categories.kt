package com.example.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnAttendance: Button
class categories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        btnAttendance = findViewById(R.id.btnAttendance)

        btnAttendance.setOnClickListener({
            val intent = Intent(this, Attendance::class.java)
            startActivity(intent)
        })
    }
}