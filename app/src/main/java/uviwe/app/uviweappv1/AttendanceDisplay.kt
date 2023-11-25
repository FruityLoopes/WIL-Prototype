package uviwe.app.uviweappv1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.Executors

 var spinnerValueDisplay : String = ""
class AttendanceDisplay : AppCompatActivity() {
    private val Items: MutableList<AttendanceData> = mutableListOf()
    lateinit var attAdapter: AttDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_display)

        attAdapter = AttDisplayAdapter()
        val btnAdd = findViewById<Button>(R.id.btnAddAttDisplay)
        val spinner = findViewById<Spinner>(R.id.spinnerAttDis)
        val feed = findViewById<RecyclerView>(R.id.feedAttDisplay)

        feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = attAdapter
        }

        val listClass : ArrayList<String> = ArrayList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, listClass)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val database =
            FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference()
        val executor = Executors.newSingleThreadExecutor()

        myRef.addValueEventListener(object: ValueEventListener { //must be adjusted to timesheet values

            override fun onDataChange(snapshot: DataSnapshot) {
                listClass.clear()
                for(keys in snapshot.children){
                    if(keys.key.toString() != "Donations"){
                        listClass.add(keys.key.toString())
                    }
                }

                arrayAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase","Failed to read value", error.toException())
            }
        })
        spinner.adapter= arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerValueDisplay  = parent.getItemAtPosition(position).toString()
                val myRef = database.getReference(spinnerValueDisplay ).child("Students")
                executor.execute {

                    myRef.addValueEventListener(object :
                        ValueEventListener { //has been adjusted to timesheet values

                        override fun onDataChange(snapshot: DataSnapshot) {
                            Items.clear()


                            for (unique in snapshot.children) {
                                var count = 0
                                val Student = unique.key.toString()

                                for (check in unique.children){
                                    if(check.key.toString() != "FeePayer"){
                                        count++
                                    }

                                }
                                Items.add(
                                    AttendanceData( Student = Student ,
                                                    Days = count.toString())
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

        btnAdd.setOnClickListener(){
            showAddChildDialog()
        }
    }

    private fun showAddChildDialog() {
        val database = FirebaseDatabase.getInstance("https://wil-prototype-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference(spinnerValueDisplay ).child("Students")

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_child, null)

        val etChildName = dialogView.findViewById<EditText>(R.id.etChildName)
        val txtclass = dialogView.findViewById<TextView>(R.id.txtClassAdd)
        txtclass.text = spinnerValueDisplay
        val btnAddChild = dialogView.findViewById<Button>(R.id.btnAddChild)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        btnAddChild.setOnClickListener {
            val childName = etChildName.text.toString().trim()
            if (childName.isNotEmpty()) {
                myRef.child(childName).setValue("")
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a child's name", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }
}