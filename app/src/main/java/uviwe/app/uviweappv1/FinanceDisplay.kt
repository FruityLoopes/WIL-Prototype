package uviwe.app.uviweappv1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uviweappv1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


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
        val exportButton = findViewById<Button>(R.id.btnExport)
        exportButton.setOnClickListener {
            exportToTxt(Item, spinnerValue, spinnerChild)
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
    private fun exportToTxt(dataList: List<FinanceData>, selectedClass: String, selectedChild: String) {
        try {
            // Create a file object for the external storage directory
            val file = File(
                Environment.getExternalStorageDirectory(),
                "finance_Report.txt"
            )

            // Create a FileOutputStream and an OutputStreamWriter to write to the file
            val fileOutputStream = FileOutputStream(file)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)

            // Write header to the file
            outputStreamWriter.write("Class: $selectedClass\n")
            outputStreamWriter.write("Student: $selectedChild\n")
            outputStreamWriter.write("Date, Amount\n")

            // Write data to the file
            for (item in dataList) {
                val line = "${item.Date}, ${item.Amount}\n"
                outputStreamWriter.write(line)
            }

            // Close the OutputStreamWriter and FileOutputStream
            outputStreamWriter.close()
            fileOutputStream.close()

            // Show a toast indicating successful export
            Toast.makeText(
                this,
                "Data exported to ${file.absolutePath}",
                Toast.LENGTH_SHORT
            ).show()
            Log.i("FinanceDisplay", "Data exported to ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error exporting data", Toast.LENGTH_SHORT).show()
            // Handle the exception (e.g., show an error message to the user)
            Log.e("FinanceDisplay", "Error exporting data", e)
        }
    }

}