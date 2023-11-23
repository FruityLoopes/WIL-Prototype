package uviwe.app.uviweappv1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors


class Attendance : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var attAdapter: AttendanceAdapter
    private val Items: MutableList<AttendanceData> = mutableListOf()
    private lateinit var dateAtt: TextView
    private lateinit var spinClass: Spinner
    private var spinnerValue : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        auth = Firebase.auth

        dateAtt = findViewById(R.id.dateAtt)
        spinClass = findViewById(R.id.spClass)

        val listClass : ArrayList<String> = ArrayList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, listClass)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val recordAtt: Button = findViewById(R.id.btnRecordAtt)
        val feed: RecyclerView = findViewById(R.id.feedAtt)
        attAdapter = AttendanceAdapter { clickedAttendance ->
            Log.d("Attendance", "Clicked attendance: ${clickedAttendance.Student}, Attended: ${clickedAttendance.attended}")
        }
        feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = attAdapter
        }
        // Populate the date list with the desired range of dates
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val fb = SimpleDateFormat("yyyyMMdd" ,  Locale.getDefault())
        val formattedDate = df.format(c)
        val formattedFB = fb.format(c)



        dateAtt.text = formattedDate.toString()


        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference()
        val executor = Executors.newSingleThreadExecutor()

        myRef.addValueEventListener(object: ValueEventListener { //must be adjusted to timesheet values

            override fun onDataChange(snapshot: DataSnapshot) {
                listClass.clear()
                for(keys in snapshot.children){
                    listClass.add(keys.key.toString())
                }

                arrayAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase","Failed to read value", error.toException())
            }
        })
        spinClass.adapter= arrayAdapter

        spinClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerValue = parent.getItemAtPosition(position).toString()
                Toast.makeText(baseContext, "Selected Class $spinnerValue", Toast.LENGTH_SHORT).show()
                val myRef = database.getReference(spinnerValue).child("Students")
                executor.execute {

                    myRef.addValueEventListener(object :
                        ValueEventListener { //has been adjusted to timesheet values

                        override fun onDataChange(snapshot: DataSnapshot) {
                            Items.clear()

                            for (unique in snapshot.children) {
                                val Student = unique.key.toString()

                                Items.add(
                                    AttendanceData( Student = Student)
                                )
                            }
                            attAdapter.notifyDataSetChanged()

                        }


                        override fun onCancelled(error: DatabaseError) { //handles errors
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                    Handler(Looper.getMainLooper()).post {
                        attAdapter.submitList(Items)
                    }

                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }

        recordAtt.setOnClickListener(){
            val attendedStudents = Items.filter { it.attended }
            val myRef = database.getReference(spinnerValue).child("Attendances")
            var count = 0
            // Process the list of attended students as needed
            // For example, you can print their names:
            for (student in attendedStudents) {
                count++
                val myRef = database.getReference(spinnerValue).child("Students")
                myRef.child(student.Student).child(formattedFB ).setValue("Attended")
                Log.d("Attendance", "Attended: ${student.Student}")
            }

            myRef.child(formattedFB).setValue(count.toString())
        }

    }
}


