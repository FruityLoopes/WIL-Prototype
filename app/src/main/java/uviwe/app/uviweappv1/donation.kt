package uviwe.app.uviweappv1

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.uviweappv1.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
//
import java.util.concurrent.Executors
//
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

//
class donation : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)
        // Initialize Firebase Auth
        auth = Firebase.auth
        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference().child("Donations")
        //Declaring Variables
        val FirstName: TextView = findViewById(R.id.txtFirstName)
        val LastName: TextView = findViewById<EditText>(R.id.txtLastName)
        val Contact: TextView = findViewById<EditText>(R.id.txtContact)
        val PayerEmail: TextView = findViewById<EditText>(R.id.txtPayerEmail)
        val Amount: TextView = findViewById<EditText>(R.id.txtAmount)
        val Notes: TextView = findViewById<EditText>(R.id.txtNotes)
        val Date: TextView = findViewById<EditText>(R.id.txtDate)
        val Confirm: CheckBox = findViewById(R.id.cbConfirmation)
        //Button Decalre
        val Record: Button = findViewById(R.id.btnRecord)
        val Display = findViewById<Button>(R.id.btnDonDisplay)

        Record.setOnClickListener()
        {
            if(Confirm.isChecked){
                val currentTime = System.currentTimeMillis().toString()
                myRef.child(currentTime).child("FirstName").setValue(FirstName.text.toString())
                myRef.child(currentTime).child("LastName").setValue(LastName.text.toString())
                myRef.child(currentTime).child("Contact").setValue(Contact.text.toString())
                myRef.child(currentTime).child("PayerEmail").setValue(PayerEmail.text.toString())
                myRef.child(currentTime).child("Amount").setValue(Amount.text.toString())
                myRef.child(currentTime).child("Notes").setValue(Notes.text.toString())
                myRef.child(currentTime).child("Date").setValue(Date.text.toString())
                Toast.makeText(this, "Donation Saved.", Toast.LENGTH_SHORT).show()

                FirstName.text = ""
                LastName.text = ""
                Contact.text = ""
                PayerEmail.text = ""
                Amount.text = ""
                Notes.text = ""
                Date.text = ""
            } else {
                Toast.makeText(this, "Please Confirm", Toast.LENGTH_SHORT).show()
            }


        }

        Display.setOnClickListener(){
            val intent = Intent(this, DonationDisplay::class.java)
            startActivity(intent)
        }
    }
}