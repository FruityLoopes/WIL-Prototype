package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class FinanceDisplay : AppCompatActivity() {

    lateinit var finAdapter: FinDisplayAdapter
    private val Item: MutableList<FinanceData> = mutableListOf()
    private var spinnerValue : String = ""
    private var spinnerChild : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_display)

        finAdapter = FinDisplayAdapter()
        val feed = findViewById<RecyclerView>(R.id.feedFinDisplay)
        feed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finAdapter
        }
        val spClass = findViewById<Spinner>(R.id.spinnerFinDis)
        val spChild = findViewById<Spinner>(R.id.spinnerStudentFin)

        val listClass : ArrayList<String> = ArrayList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, listClass)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout)

        val Items : ArrayList<String> = ArrayList()
        val arraysAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_layout, Items)
        arraysAdapter.setDropDownViewResource(R.layout.spinner_layout)

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

                Handler(Looper.getMainLooper()).post {
                    spClass.adapter = arrayAdapter
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase","Failed to read value", error.toException())
            }
        })

        spClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerValue = parent.getItemAtPosition(position).toString()

                val myRef = database.getReference(spinnerValue).child("Students")
                executor.execute {
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Items.clear()

                            for (unique in snapshot.children) {
                                Items.add(unique.key.toString())
                            }

                            Handler(Looper.getMainLooper()).post {
                                spChild.adapter = arraysAdapter
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO: Implement the behavior when nothing is selected
            }
        }

        spChild.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, arg3: Long) {
                spinnerChild = parent.getItemAtPosition(position).toString()

                val myRef1 = database.getReference(spinnerValue).child("Students").child(spinnerChild)
                executor.execute {

                    myRef1.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Item.clear()
                            for (unique in snapshot.children) {
                                if(unique.child("FirstName").value != null){

                                   for (uniq in unique.child("Fees Paid").children){
                                       Item.add(
                                           FinanceData( Date = formatDate(uniq.key.toString()),
                                               Amount = uniq.getValue().toString()
                                           ))
                                   }
                                } else {
                                    FinanceData( Date = "NO DATA",
                                        Amount = "NO DATA")
                                }

                            }

                            finAdapter.notifyDataSetChanged()

                            Handler(Looper.getMainLooper()).post {
                                finAdapter.submitList(Item)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("Firebase", "Failed to read value", error.toException())
                        }
                    })
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return inputDate
    }
}