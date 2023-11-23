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
    private var auth: FirebaseAuth,
    lateinit var pieChart: PieChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation)
        // Initialize Firebase Auth
        auth = Firebase.auth
        /*private lateinit*/ var auth: FirebaseAuth
        /*private*/ var storagRef = Firebase.storage
        //
        //Declaring Variables
        val FirstName: TextView = findViewById(R.id.txtFirstName)
        val LastName: TextView = findViewById<EditText>(R.id.txtLastName)
        val Contact: TextView = findViewById<EditText>(R.id.txtContact)
        val PayerEmail: TextView = findViewById<EditText>(R.id.txtPayerEmail)
        val Amount: TextView = findViewById<EditText>(R.id.txtAmount)
        val Notes: TextView = findViewById<EditText>(R.id.txtNotes)
        val Event: TextView = findViewById<EditText>(R.id.txtEvent)
        val AmountPaid: TextView = findViewById<EditText>(R.id.txtAmountPaid)
        //val txtAmountPaid: TextView = findViewById<EditText>(R.id.txtAmountPaid)
        val Date: TextView = findViewById<EditText>(R.id.txtDate)
        val Confirmation: TextView = findViewById<EditText>(R.id.cbConfirmation)
        //Button Decalre
        val Record: Button = findViewById(R.id.btnRecord)
        //

        // on below line we are initializing our
        // variable with their ids.
        pieChart = findViewById(R.id.pieChart)

        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(70f))
        entries.add(PieEntry(20f))
        entries.add(PieEntry(10f))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Donations")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()

        //Firebase Code
        //Declaring Variables
        //Second Method
        /*val FirstName: TextView = findViewById<EditText>(R.id.txtFirstName)}
        val LastName: TextView = findViewById<EditText>(R.id.txtLastName)}
        val Contact: TextView = findViewById<EditText>(R.id.txtContact)}
        val PayerEmail: TextView = findViewById<EditText>(R.id.txtPayerEmail)}
        val Amount: TextView = findViewById<EditText>(R.id.txtAmount)}
        val Notes: TextView = findViewById<EditText>(R.id.txtFirstName)}
        val Event: TextView = findViewById<EditText>(R.id.txtEvent)}
        val AmountPaid: TextView = findViewById<EditText>(R.id.txtNotes)}
        val Date: TextView = findViewById<EditText>(R.id.txtDate)}
        val Confirmation: TextView = findViewById<EditText>(R.id.cbConfirmation)}*/

        //NEW FIREBASE CODE IS BELOWE (Line 182 to Line 200)
        /*
        btnRecord.setOnClickListener()
        {
            myRef.child("Donations").child(currentTime).child("Image").setValue(mapImage).addOnSuccessListener {
                Toast.makeText(this, "Successful" , Toast.LENGTH_SHORT).show()
            } .addOnFailureListener{ error ->
                Toast.makeText(this, it.toString(),  Toast.LENGTH_SHORT).show()
            }

            myRef.child("Donations").child(currentTime).child("FirstName").setValue(FirstName.text.toString())
            myRef.child("Donations").child(currentTime).child("LastName").setValue(LastName.text.toString())
            myRef.child("Donations").child(currentTime).child("Contact").setValue(Contact.text.toString())
            myRef.child("Donations").child(currentTime).child("PayerEmail").setValue(PayerEmail.text.toString())
            myRef.child("Donations").child(currentTime).child("Amount").setValue(Amount.toString())
            myRef.child("Donations").child(currentTime).child("Notes").setValue(Notes.text.toString())
            myRef.child("Donations").child(currentTime).child("Event").setValue(Event.text.toString())
            myRef.child("Donations").child(currentTime).child("AmountPaid").setValue(AmountPaid.text.toString())
            myRef.child("Donations").child(currentTime).child("Confirmation").setValue(Confirmation.text.toString())

            Toast.makeText(this, "Donation Saved.", Toast.LENGTH_SHORT).show()
        }*/
    }//end of first bracket
}